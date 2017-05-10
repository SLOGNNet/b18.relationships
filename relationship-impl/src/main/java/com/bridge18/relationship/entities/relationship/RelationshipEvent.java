package com.bridge18.relationship.entities.relationship;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;
import org.pcollections.PVector;

import java.util.Date;
import java.util.Optional;

public interface RelationshipEvent extends Jsonable, AggregateEvent<RelationshipEvent> {
    int NUM_SHARDS = 4;
    AggregateEventShards<RelationshipEvent> TAG = AggregateEventTag.sharded(RelationshipEvent.class, NUM_SHARDS);

    @Override
    default AggregateEventTagger<RelationshipEvent> aggregateTag() {
        return TAG;
    }

    @Value.Immutable
    @JsonDeserialize
    @ImmutableStyle
    interface AbstractRelationshipCreated extends RelationshipEvent {
        @Value.Parameter
        String getId();

        @Value.Parameter
        Optional<String> getProvider();
        @Value.Parameter
        Optional<String> getCustomer();
        @Value.Parameter
        Optional<Date> getStartDate();
        @Value.Parameter
        Optional<Date> getTerminationDate();
        @Value.Parameter
        Optional<String> getNotes();
        @Value.Parameter
        Optional<PVector<Assignment>> getAssignments();
    }

    @Value.Immutable
    @JsonDeserialize
    @ImmutableStyle
    interface AbstractRelationshipUpdated extends RelationshipEvent {
        @Value.Parameter
        String getId();

        @Value.Parameter
        Optional<String> getProvider();
        @Value.Parameter
        Optional<String> getCustomer();
        @Value.Parameter
        Optional<Date> getStartDate();
        @Value.Parameter
        Optional<Date> getTerminationDate();
        @Value.Parameter
        Optional<String> getNotes();
        @Value.Parameter
        Optional<PVector<Assignment>> getAssignments();
    }

    @Value.Immutable
    @JsonDeserialize
    @ImmutableStyle
    interface AbstractRelationshipDeleted extends RelationshipEvent {
        @Value.Parameter
        String getId();
    }

    @Value.Immutable
    @JsonDeserialize
    @ImmutableStyle
    interface AbstractAssignmentCreated extends RelationshipEvent{
        @Value.Parameter
        String getId();
        @Value.Parameter
        Optional<String> getAssignment();
        @Value.Parameter
        Optional<AssignmentType> getType();
        @Value.Parameter
        Optional<String> getNotes();
    }

    @Value.Immutable
    @JsonDeserialize
    @ImmutableStyle
    interface AbstractAssignmentDeleted extends RelationshipEvent{
        @Value.Parameter
        String getId();
        @Value.Parameter
        Optional<String> getAssignment();
    }
}
