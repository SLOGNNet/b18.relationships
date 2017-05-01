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
    public CompletionStage<PaginatedSequence<RelationshipDTO>> getRelationships(int pageNumber, int pageSize) {
        List<MongoRelationship> relationships = datastore.createQuery(MongoRelationship.class)
                .asList(new FindOptions().skip(pageNumber > 0 ? (pageNumber - 1) * pageSize : 0)
                        .limit(pageSize)
                );
        return CompletableFuture.completedFuture(
                new PaginatedSequence<>(
                        TreePVector.from(
                                relationships.stream()
                                        .map(this::transformRelationshipToRelationshipDTO)
                                        .collect(Collectors.toList())
                        ),
                        pageSize,
                        (int) datastore.getCount(datastore.createQuery(MongoRelationship.class))
                )


        );
    }

    private RelationshipDTO transformRelationshipToRelationshipDTO(MongoRelationship relationship) {
        List<AssignmentDTO> assignmentDTOList = relationship.getAssignments() != null ?
                Lists.transform(relationship.getAssignments(), mongoAssignment ->
                        new AssignmentDTO(
                                mongoAssignment.getAssignment(),
                                mongoAssignment.getType(),
                                mongoAssignment.getNotes()
                        )
                ) : null;

        return new RelationshipDTO(
                relationship.getRelationshipId(), relationship.getProvider(),
                relationship.getCustomer(), relationship.getStartDate(),
                relationship.getTerminationDate(), relationship.getNotes(),
                assignmentDTOList
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
                            (datastore, e) -> deleteRelationshipSummary(datastore, e.getId())
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
                        datastore.ensureIndexes(MongoRelationship.class);
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
                        new MongoRelationship(e.getId(), e.getProvider().orElse(null),
                                e.getCustomer().orElse(null), e.getStartDate().orElse(null),
                                e.getTerminationDate().orElse(null), e.getNotes().orElse(null),
                                transformPVectorToList(e.getAssignments())
                        )
                );
            });
        }

        private List<MongoAssignment> transformPVectorToList(Optional<PVector<Assignment>> assignmentPVector) {
            if (!assignmentPVector.isPresent() || assignmentPVector.get().isEmpty()) {
                return new ArrayList<>();
            }

            return assignmentPVector.get().stream()
                    .map(assignment -> new MongoAssignment(
                            assignment.getAssignment().orElse(null),
                            assignment.getType().orElse(null),
                            assignment.getNotes().orElse(null)
                    ))
                    .collect(Collectors.toList());
        }

        private CompletionStage<Void> updateRelationshipSummary(Datastore datastore,
                                                                RelationshipUpdated e) {

            return CompletableFuture.runAsync(() -> {
                UpdateOperations<MongoRelationship> updateOperations = setNotNullFieldsInUpdateOperations(datastore, e);

                datastore.update(
                        datastore.createQuery(MongoRelationship.class).field("relationshipId").equal(e.getId()),
                        updateOperations
                );
            });
        }

        private UpdateOperations<MongoRelationship> setNotNullFieldsInUpdateOperations(Datastore datastore,
                                                                                       RelationshipUpdated e) {
            UpdateOperations updateOperations = datastore.createUpdateOperations(MongoRelationship.class);

            if (e.getProvider().isPresent()) updateOperations.set("provider", e.getProvider().get());
            if (e.getCustomer().isPresent()) updateOperations.set("customer", e.getCustomer().get());
            if (e.getStartDate().isPresent()) updateOperations.set("startDate", e.getStartDate().get());
            if (e.getTerminationDate().isPresent())
                updateOperations.set("terminationDate", e.getTerminationDate().get());
            if (e.getNotes().isPresent()) updateOperations.set("notes", e.getNotes().get());
            if (e.getAssignments().isPresent())
                updateOperations.set("assignments", transformPVectorToList(e.getAssignments()));

            return updateOperations;
        }

        private CompletionStage<Void> deleteRelationshipSummary(Datastore datastore, String relationshipId) {
            return CompletableFuture.runAsync(() -> {
                        Query<MongoRelationship> relationshipsToDelete = datastore.createQuery(MongoRelationship.class)
                                .field("relationshipId")
                                .equal(relationshipId);
                        datastore.delete(relationshipsToDelete);
                    }
            );
        }

        private CompletionStage<Void> insertAssignment(Datastore datastore, AssignmentCreated e) {
            return CompletableFuture.runAsync(() -> {
                datastore.update(
                        datastore.createQuery(MongoRelationship.class).field("relationshipId").equal(e.getId()),
                        datastore.createUpdateOperations(MongoRelationship.class)
                                .push("assignments", new MongoAssignment(
                                        e.getAssignment().orElse(null),
                                        e.getType().orElse(null),
                                        e.getNotes().orElse(null)
                                ))
                );
            });
        }

        private CompletionStage<Void> deleteAssignment(Datastore datastore, AssignmentDeleted e) {
            return CompletableFuture.runAsync(() -> {
                datastore.update(
                        datastore.createQuery(MongoRelationship.class).field("relationshipId").equal(e.getId()),
                        datastore.createUpdateOperations(MongoRelationship.class)
                                .removeAll("assignments", new MongoAssignment(e.getAssignment().get(), null, null))
                );
            });
        }
    }
}
