package com.bridge18.relationship.services.objects.impl;

import akka.Done;
import com.bridge18.relationship.entities.relationship.*;
import com.bridge18.relationship.services.objects.RelationshipService;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PVector;

import javax.inject.Inject;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class RelationshipServiceImpl implements RelationshipService {
    private final PersistentEntityRegistry persistentEntityRegistry;

    @Inject
    public RelationshipServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        persistentEntityRegistry.register(RelationshipEntity.class);
    }

    @Override
    public CompletionStage<RelationshipState> createRelationship(Optional<String> provider, Optional<String> customer, Optional<Date> startDate, Optional<Date> terminationDate, Optional<String> notes, Optional<PVector<Assignment>> assignments) {
        CreateRelationship cmd = CreateRelationship.builder()
                .provider(provider)
                .customer(customer)
                .startDate(startDate)
                .terminationDate(terminationDate)
                .notes(notes)
                .assignments(assignments)
                .build();

        PersistentEntityRef ref = persistentEntityRegistry.refFor(RelationshipEntity.class,
                UUID.randomUUID().toString());

        return ref.ask(cmd);
    }

    @Override
    public CompletionStage<RelationshipState> updateRelationship(String id, Optional<String> provider, Optional<String> customer, Optional<Date> startDate, Optional<Date> terminationDate, Optional<String> notes, Optional<PVector<Assignment>> assignments) {
        UpdateRelationship cmd = UpdateRelationship.builder()
                .provider(provider)
                .customer(customer)
                .startDate(startDate)
                .terminationDate(terminationDate)
                .notes(notes)
                .assignments(assignments)
                .build();

        PersistentEntityRef ref = persistentEntityRegistry.refFor(RelationshipEntity.class, id);

        return ref.ask(cmd);
    }

    @Override
    public CompletionStage<Done> deleteRelationship(String id) {
        DeleteRelationship cmd = DeleteRelationship.builder().build();

        PersistentEntityRef ref = persistentEntityRegistry.refFor(RelationshipEntity.class, id);

        return ref.ask(cmd);
    }

    @Override
    public CompletionStage<RelationshipState> getRelationship(String id) {
        GetRelationship cmd = GetRelationship.builder().build();

        PersistentEntityRef ref = persistentEntityRegistry.refFor(RelationshipEntity.class, id);

        return ref.ask(cmd);
    }

    @Override
    public CompletionStage<RelationshipState> createAssignment(String id, Optional<String> assignment, Optional<AssignmentType> type, Optional<String> notes) {
        CreateAssignment cmd = CreateAssignment.builder()
                .assignment(assignment)
                .type(type)
                .notes(notes)
                .build();

        PersistentEntityRef ref = persistentEntityRegistry.refFor(RelationshipEntity.class, id);

        return ref.ask(cmd);
    }

    @Override
    public CompletionStage<Done> deleteAssignment(String id, String assignment) {
        DeleteAssignment cmd = DeleteAssignment.builder()
                .assignment(assignment)
                .build();

        PersistentEntityRef ref = persistentEntityRegistry.refFor(RelationshipEntity.class, id);

        return ref.ask(cmd);
    }
}
