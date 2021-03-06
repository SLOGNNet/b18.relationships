package com.bridge18.relationship.api;

import akka.Done;
import akka.NotUsed;
import com.bridge18.relationship.dto.relationship.AssignmentDTO;
import com.bridge18.relationship.dto.relationship.RelationshipDTO;
import com.bridge18.relationship.dto.relationship.PaginatedSequence;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceAcl;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface LagomRelationshipService extends Service {
    ServiceCall<RelationshipDTO, RelationshipDTO> createRelationship();
    ServiceCall<RelationshipDTO, RelationshipDTO> updateRelationship(String id);
    ServiceCall<NotUsed, RelationshipDTO> getRelationship(String id);
    ServiceCall<NotUsed, Done> deleteRelationship(String id);
    ServiceCall<AssignmentDTO, RelationshipDTO> createAssignment(String id);
    ServiceCall<NotUsed, Done> deleteAssignment(String id, String assignment);
    ServiceCall<NotUsed, PaginatedSequence<RelationshipDTO>> getRelationshipSummaries(Optional<Integer> pageNumber, Optional<Integer> pageSize);


    @Override
    default Descriptor descriptor() {
        return named("relationship").withCalls(
                restCall(Method.POST, "/v1/api/relationship", this::createRelationship),
                restCall(Method.PUT, "/v1/api/relationship/:id", this::updateRelationship),
                restCall(Method.DELETE, "/v1/api/relationship/:id", this::deleteRelationship),
                restCall(Method.POST, "/v1/api/relationship/:id/assignment", this::createAssignment),
                restCall(Method.DELETE, "/v1/api/relationship/:id/assignment/:assignment", this::deleteAssignment),
                restCall(Method.GET, "/v1/api/relationship/:id", this::getRelationship),
                restCall(Method.GET, "/v1/api/relationship?pageSize&pageNumber", this::getRelationshipSummaries)
        ).withAutoAcl(true)
                .withServiceAcls(
                        ServiceAcl.methodAndPath(Method.OPTIONS, "\\*")
                );
    }
}
