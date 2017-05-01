package com.bridge18.relationship.repository;

import com.bridge18.relationship.dto.relationship.RelationshipDTO;
import com.bridge18.relationship.dto.relationship.PaginatedSequence;

import java.util.concurrent.CompletionStage;

public interface RelationshipRepository {
    CompletionStage<PaginatedSequence<RelationshipDTO>> getRelationships(int pageNumber, int pageSize);
}
