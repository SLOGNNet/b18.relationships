package com.bridge18.relationship.entities.relationship;


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
    interface AbstractGetRelationship extends RelationshipCommand, CompressedJsonable, PersistentEntity.ReplyType<RelationshipState> {

    }
}
