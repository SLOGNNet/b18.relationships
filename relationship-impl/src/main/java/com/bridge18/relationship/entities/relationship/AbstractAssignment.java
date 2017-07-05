package com.bridge18.relationship.entities.relationship;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize
public interface AbstractAssignment extends Jsonable {
    @Value.Parameter
    Optional<String> getAssignment();
    @Value.Parameter
    Optional<AssignmentType> getType();
    @Value.Parameter
    Optional<String> getNotes();
}
