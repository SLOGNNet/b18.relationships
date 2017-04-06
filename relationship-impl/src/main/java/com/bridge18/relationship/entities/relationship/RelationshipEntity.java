package com.bridge18.relationship.entities.relationship;


import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        b.setCommandHandler(
                UpdateRelationship.class,
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

        b.setCommandHandler(
                CreateAssignment.class,
                (cmd, ctx) ->
                        ctx.thenPersist(
                                AssignmentCreated.builder()
                                        .assignment(cmd.getAssignment())
                                        .type(cmd.getType())
                                        .notes(cmd.getNotes())
                                        .build(),
                                evt -> ctx.reply(state())
                        )
        );

        b.setCommandHandler(
                DeleteAssignment.class,
                (cmd, ctx) ->
                        ctx.thenPersist(
                                AssignmentDeleted.builder()
                                        .assignment(cmd.getAssignment())
                                        .build(),
                                evt -> ctx.reply(Done.getInstance())
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

        b.setEventHandler(
                RelationshipUpdated.class,
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

        b.setEventHandler(
                AssignmentCreated.class,
                evt -> {
                    PVector<Assignment> assignments = state().getAssignments().orElse(TreePVector.empty())
                            .plus(Assignment.builder()
                                    .assignment(evt.getAssignment())
                                    .type(evt.getType())
                                    .notes(evt.getNotes())
                                    .build()
                            );
                    return RelationshipState.builder().from(state())
                            .assignments(assignments)
                            .build();
                }
        );

        b.setEventHandler(
                AssignmentDeleted.class,
                evt -> {
                    PVector<Assignment> assignments = TreePVector.from(
                            state().getAssignments()
                                    .orElse(TreePVector.empty())
                                    .stream()
                                    .filter(assignment ->
                                            !assignment.getAssignment().equals(evt.getAssignment())
                                    )
                                    .collect(Collectors.toList())
                    );

                    return RelationshipState.builder().from(state())
                            .assignments(assignments)
                            .build();
                }
        );

        return b.build();
    }
}
