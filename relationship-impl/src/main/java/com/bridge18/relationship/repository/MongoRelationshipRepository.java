package com.bridge18.relationship.repository;


import akka.Done;
import com.bridge18.readside.mongodb.readside.MongodbReadSide;
import com.bridge18.relationship.dto.relationship.AssignmentDTO;
import com.bridge18.relationship.dto.relationship.PaginatedSequence;
import com.bridge18.relationship.dto.relationship.RelationshipDTO;
import com.bridge18.relationship.entities.relationship.*;
import com.google.common.collect.Lists;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.pcollections.PSequence;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static com.bridge18.relationship.core.CompletionStageUtils.doAll;

public class MongoRelationshipRepository implements RelationshipRepository {
    private Datastore datastore;

    @Inject
    public MongoRelationshipRepository(ReadSide readSide, Datastore datastore) {
        readSide.register(RelationshipEventProcessor.class);
        this.datastore = datastore;
    }

    @Override
    public PaginatedSequence<RelationshipState> getRelationships(int pageNumber, int pageSize) {
        List<RelationshipState> relationships = datastore.createQuery(RelationshipState.class)
                .asList(new FindOptions().skip(pageNumber > 0 ? (pageNumber - 1) * pageSize : 0)
                        .limit(pageSize)
                );
        return new PaginatedSequence<>(
                TreePVector.from(
                        relationships
                ),
                pageSize,
                (int) datastore.getCount(datastore.createQuery(RelationshipState.class))

        );
    }

    private static class RelationshipEventProcessor extends ReadSideProcessor<RelationshipEvent> {

        private final MongodbReadSide readSide;

        @Inject
        public RelationshipEventProcessor(MongodbReadSide readSide) {
            this.readSide = readSide;
        }

        @Override
        public ReadSideHandler<RelationshipEvent> buildHandler() {
            return readSide.<RelationshipEvent>builder("mongoRelationshipEventOffset")
                    .setGlobalPrepare(this::globalPrepare)
                    .setPrepare(this::prepareStatements)
                    .setEventHandler(RelationshipCreated.class,
                            this::insertRelationshipSummary
                    )
                    .setEventHandler(RelationshipUpdated.class,
                            this::updateRelationshipSummary
                    )
                    .setEventHandler(RelationshipDeleted.class,
                            this::deleteRelationshipSummary
                    )
                    .setEventHandler(AssignmentCreated.class,
                            this::insertAssignment
                    )
                    .setEventHandler(AssignmentDeleted.class,
                            this::deleteAssignment
                    )
                    .build();
        }

        @Override
        public PSequence<AggregateEventTag<RelationshipEvent>> aggregateTags() {
            return RelationshipEvent.TAG.allTags();
        }

        private CompletionStage<Done> globalPrepare(Datastore datastore) {
            return doAll(
                    //@TODO: indexing?

                    CompletableFuture.runAsync(() -> {
                        datastore.ensureIndexes(RelationshipState.class);
                    })
            );
        }

        private CompletionStage<Done> prepareStatements(Datastore datastore, AggregateEventTag<RelationshipEvent> tag) {
            return doAll(
                    //@TODO: indexing?
            );
        }

        private CompletionStage<Void> insertRelationshipSummary(Datastore datastore,
                                                                RelationshipCreated e) {

            return CompletableFuture.runAsync(() -> {
                datastore.save(
                        createRelationshipState(e)
                );
            });
        }

        private RelationshipState createRelationshipState(RelationshipCreated e){
            RelationshipState.Builder builder = RelationshipState.builder().id(e.getId());

            if(e.getProvider().isPresent()) builder.provider(e.getProvider().get());
            if(e.getCustomer().isPresent()) builder.customer(e.getCustomer().get());
            if(e.getStartDate().isPresent()) builder.startDate(e.getStartDate().get());
            if(e.getTerminationDate().isPresent()) builder.terminationDate(e.getTerminationDate().get());
            if(e.getNotes().isPresent()) builder.notes(e.getNotes().get());
            if(e.getAssignments().isPresent()) builder.assignments(e.getAssignments().get());

            return builder.build();
        }

        private CompletionStage<Void> updateRelationshipSummary(Datastore datastore,
                                                                RelationshipUpdated e) {

            return CompletableFuture.runAsync(() -> {
                UpdateOperations<RelationshipState> updateOperations = setNotNullFieldsInUpdateOperations(datastore, e);

                datastore.update(
                        datastore.createQuery(RelationshipState.class).field("id").equal(e.getId()),
                        updateOperations
                );
            });
        }


        private UpdateOperations<RelationshipState> setNotNullFieldsInUpdateOperations(Datastore datastore,
                                                                                       RelationshipUpdated e) {
            UpdateOperations updateOperations = datastore.createUpdateOperations(RelationshipState.class);

            if (e.getProvider().isPresent()) updateOperations.set("provider", e.getProvider().get());
            if (e.getCustomer().isPresent()) updateOperations.set("customer", e.getCustomer().get());
            if (e.getStartDate().isPresent()) updateOperations.set("startDate", e.getStartDate().get());
            if (e.getTerminationDate().isPresent()) updateOperations.set("terminationDate", e.getTerminationDate().get());
            if (e.getNotes().isPresent()) updateOperations.set("notes", e.getNotes().get());
            if (e.getAssignments().isPresent()) updateOperations.set("assignments", e.getAssignments().get());

            return updateOperations;
        }

        private CompletionStage<Void> deleteRelationshipSummary(Datastore datastore, RelationshipDeleted e) {
            return CompletableFuture.runAsync(() -> {
                        Query<RelationshipState> relationshipsToDelete = datastore.createQuery(RelationshipState.class)
                                .field("id")
                                .equal(e.getId());
                        datastore.delete(relationshipsToDelete);
                    }
            );
        }

        private CompletionStage<Void> insertAssignment(Datastore datastore, AssignmentCreated e) {
            return CompletableFuture.runAsync(() -> {
                datastore.update(
                        datastore.createQuery(RelationshipState.class).field("id").equal(e.getId()),
                        datastore.createUpdateOperations(RelationshipState.class)
                                .push("assignments", createAssignment(e))
                );
            });
        }

        private Assignment createAssignment(AssignmentCreated e){
            Assignment.Builder builder = Assignment.builder();

            if(e.getAssignment().isPresent()) builder.assignment(e.getAssignment().get());
            if(e.getType().isPresent()) builder.type(e.getType().get());
            if(e.getNotes().isPresent()) builder.notes(e.getNotes().get());

            return builder.build();
        }

        private CompletionStage<Void> deleteAssignment(Datastore datastore, AssignmentDeleted e) {
            return CompletableFuture.runAsync(() -> {
                datastore.update(
                        datastore.createQuery(RelationshipState.class).field("id").equal(e.getId()),
                        datastore.createUpdateOperations(RelationshipState.class)
                                .removeAll("assignments",
                                        Assignment.builder().assignment(e.getAssignment().get()).build())
                );
            });
        }
    }
}
