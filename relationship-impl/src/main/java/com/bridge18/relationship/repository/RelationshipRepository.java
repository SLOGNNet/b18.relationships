package com.bridge18.relationship.repository;

import com.bridge18.relationship.dto.relationship.PaginatedSequence;
import com.bridge18.relationship.entities.relationship.RelationshipState;

public interface RelationshipRepository {
    PaginatedSequence<RelationshipState> getRelationships(int pageNumber, int pageSize);
}
