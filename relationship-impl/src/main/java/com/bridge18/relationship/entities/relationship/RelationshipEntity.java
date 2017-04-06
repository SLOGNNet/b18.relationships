package com.bridge18.relationship.entities.relationship;


import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

public class RelationshipEntity extends PersistentEntity<RelationshipCommand, RelationshipEvent, RelationshipState> {
    @Override
    public Behavior initialBehavior(Optional<RelationshipState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(
                snapshotState.orElse(RelationshipState.builder().id(entityId()).build())
        );

        b.setCommandHandler(
                CreateRelationship.class,
                (cmd, ctx) ->
                        ctx.thenPersist(
                                RelationshipCreated.builder()
                                        .id(entityId())
                                        .provider(cmd.getProvider())
                                        .customer(cmd.getCustomer())
                                        .startDate(cmd.getStartDate())
                                        .terminationDate(cmd.getTerminationDate())
                                        .notes(cmd.getNotes())
                                        .assignments(cmd.getAssignments())
                                        .build(),
                                evt -> {
                                    ctx.reply(state());
                                }
                        )
        );

        b.setReadOnlyCommandHandler(
                GetRelationship.class,
                (cmd, ctx) ->
                        ctx.reply(state())
        );

        b.setEventHandler(
                RelationshipCreated.class,
                evt ->
                        RelationshipState.builder()
                                .id(entityId())
                                .provider(evt.getProvider())
                                .customer(evt.getCustomer())
                                .startDate(evt.getStartDate())
                                .terminationDate(evt.getTerminationDate())
                                .notes(evt.getNotes())
                                .assignments(evt.getAssignments())
                                .build()
        );

        return b.build();
    }
}
