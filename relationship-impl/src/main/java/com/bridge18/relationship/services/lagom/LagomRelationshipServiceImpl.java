package com.bridge18.relationship.services.lagom;

import akka.Done;
import akka.NotUsed;
import com.bridge18.relationship.api.LagomRelationshipService;
import com.bridge18.relationship.dto.relationship.AssignmentDTO;
import com.bridge18.relationship.dto.relationship.PaginatedSequence;
import com.bridge18.relationship.dto.relationship.RelationshipDTO;
import com.bridge18.relationship.entities.relationship.Assignment;
import com.bridge18.relationship.entities.relationship.RelationshipState;
import com.bridge18.relationship.repository.RelationshipRepository;
import com.bridge18.relationship.services.objects.RelationshipService;
import com.google.common.collect.Lists;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class LagomRelationshipServiceImpl implements LagomRelationshipService {
    private RelationshipService relationshipService;
    private RelationshipRepository relationshipRepository;

    static final int PAGE_SIZE = 20;

    @Inject
    public LagomRelationshipServiceImpl(RelationshipService relationshipService, RelationshipRepository relationshipRepository) {
        this.relationshipService = relationshipService;
        this.relationshipRepository = relationshipRepository;
    }

    @Override
    public ServiceCall<RelationshipDTO, RelationshipDTO> createRelationship() {
        return request -> {
            PVector<Assignment> assignments = Optional.ofNullable(request.assignments).isPresent() ?
                    TreePVector.from(
                            Lists.transform(request.assignments,
                                    assignmentDTO -> Assignment.builder()
                                            .assignment(assignmentDTO.assignment)
                                            .type(assignmentDTO.type)
                                            .notes(assignmentDTO.notes)
                                            .build()
                            )
                    ) : null;
            return relationshipService.createRelationship(Optional.ofNullable(request.provider),
                    Optional.ofNullable(request.customer), Optional.ofNullable(request.startDate),
                    Optional.ofNullable(request.terminationDate), Optional.ofNullable(request.notes),
                    Optional.ofNullable(assignments))

                    .thenApply(this::convertRelationshipStateToRelationshipDTO);
        };
    }

    @Override
    public ServiceCall<RelationshipDTO, RelationshipDTO> updateRelationship(String id) {
        return request -> {
            PVector<Assignment> assignments = Optional.ofNullable(request.assignments).isPresent() ?
                    TreePVector.from(
                            Lists.transform(request.assignments,
                                    assignmentDTO -> Assignment.builder()
                                            .assignment(assignmentDTO.assignment)
                                            .type(assignmentDTO.type)
                                            .notes(assignmentDTO.notes)
                                            .build()
                            )
                    ) : null;
            return relationshipService.updateRelationship(id, Optional.ofNullable(request.provider),
                    Optional.ofNullable(request.customer), Optional.ofNullable(request.startDate),
                    Optional.ofNullable(request.terminationDate), Optional.ofNullable(request.notes),
                    Optional.ofNullable(assignments))

                    .thenApply(this::convertRelationshipStateToRelationshipDTO);
        };
    }

    @Override
    public ServiceCall<NotUsed, Done> deleteRelationship(String id) {
        return request ->
                relationshipService.deleteRelationship(id);
    }

    @Override
    public ServiceCall<AssignmentDTO, RelationshipDTO> createAssignment(String id) {
        return request ->
                relationshipService.createAssignment(id,
                        Optional.ofNullable(request.assignment),
                        Optional.ofNullable(request.type),
                        Optional.ofNullable(request.notes)
                )
                        .thenApply(this::convertRelationshipStateToRelationshipDTO);
    }

    @Override
    public ServiceCall<NotUsed, Done> deleteAssignment(String id, String assignment) {
        return request ->
                relationshipService.deleteAssignment(id, assignment);
    }

    @Override
    public ServiceCall<NotUsed, RelationshipDTO> getRelationship(String id) {
        return request ->
                relationshipService.getRelationship(id)
                        .thenApply(this::convertRelationshipStateToRelationshipDTO);
    }

    private RelationshipDTO convertRelationshipStateToRelationshipDTO(RelationshipState relationshipState) {
        List<AssignmentDTO> assignments = relationshipState.getAssignments().isPresent() ?
                Lists.transform(relationshipState.getAssignments().get(),
                        assignment ->
                                new AssignmentDTO(assignment.getAssignment().orElse(null),
                                        assignment.getType().orElse(null),
                                        assignment.getNotes().orElse(null))
                ) : null;

        RelationshipDTO relationshipDTO = new RelationshipDTO(relationshipState.getId(),
                relationshipState.getProvider().orElse(null),
                relationshipState.getCustomer().orElse(null),
                relationshipState.getStartDate().orElse(null),
                relationshipState.getTerminationDate().orElse(null),
                relationshipState.getNotes().orElse(null),
                assignments
        );

        return relationshipDTO;
    }

    @Override
    public ServiceCall<NotUsed, PaginatedSequence<RelationshipDTO>> getRelationshipSummaries(Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        return request -> relationshipRepository.getRelationships(pageNumber.orElse(1), pageSize.orElse(PAGE_SIZE));
    }
}
