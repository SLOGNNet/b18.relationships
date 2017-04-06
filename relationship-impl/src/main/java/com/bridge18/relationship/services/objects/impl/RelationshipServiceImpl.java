package com.bridge18.relationship.services.objects.impl;

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
    public CompletionStage<RelationshipState> getRelationship(String id) {
        GetRelationship cmd = GetRelationship.builder().build();

        PersistentEntityRef ref = persistentEntityRegistry.refFor(RelationshipEntity.class, id);

        return ref.ask(cmd);
    }
}
