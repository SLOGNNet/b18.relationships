package com.bridge18.relationship.services.objects;


import akka.Done;
import com.bridge18.relationship.entities.relationship.Assignment;
import com.bridge18.relationship.entities.relationship.AssignmentType;
import com.bridge18.relationship.entities.relationship.RelationshipState;
import org.pcollections.PVector;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface RelationshipService {
    CompletionStage<RelationshipState> createRelationship(Optional<String> provider,
                                                          Optional<String> customer,
                                                          Optional<Date> startDate,
                                                          Optional<Date> terminationDate,
                                                          Optional<String> notes,
                                                          Optional<PVector<Assignment>> assignments
    );

    CompletionStage<RelationshipState> updateRelationship(String id,
                                                          Optional<String> provider,
                                                          Optional<String> customer,
                                                          Optional<Date> startDate,
                                                          Optional<Date> terminationDate,
                                                          Optional<String> notes,
                                                          Optional<PVector<Assignment>> assignments
    );

    CompletionStage<Done> deleteRelationship(String id);

    CompletionStage<RelationshipState> getRelationship(String id);

    CompletionStage<RelationshipState> createAssignment(String id,
                                                        Optional<String> assignment,
                                                        Optional<AssignmentType> type,
                                                        Optional<String> notes);

    CompletionStage<Done> deleteAssignment(String id,
                                           String assignment);
}
