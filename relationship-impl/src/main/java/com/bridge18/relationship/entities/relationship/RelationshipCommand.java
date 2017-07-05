package com.bridge18.relationship.entities.relationship;


import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;
import org.pcollections.PVector;

import java.util.Date;
import java.util.Optional;

public interface RelationshipCommand extends Jsonable {
    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize
    interface AbstractCreateRelationship extends RelationshipCommand, CompressedJsonable, PersistentEntity.ReplyType<RelationshipState> {
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
    @ImmutableStyle
    @JsonDeserialize
    interface AbstractUpdateRelationship extends RelationshipCommand, CompressedJsonable, PersistentEntity.ReplyType<RelationshipState> {
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
    @ImmutableStyle
    @JsonDeserialize
    interface AbstractCreateAssignment extends RelationshipCommand, CompressedJsonable, PersistentEntity.ReplyType<RelationshipState> {
        @Value.Parameter
        Optional<String> getAssignment();
        @Value.Parameter
        Optional<AssignmentType> getType();
        @Value.Parameter
        Optional<String> getNotes();
    }

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize
    interface AbstractDeleteAssignment extends RelationshipCommand, CompressedJsonable, PersistentEntity.ReplyType<Done>{
        @Value.Parameter
        Optional<String> getAssignment();
    }

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize
    interface AbstractGetRelationship extends RelationshipCommand, CompressedJsonable, PersistentEntity.ReplyType<RelationshipState> {

    }

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize
    interface AbstractDeleteRelationship extends RelationshipCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

    }
}
