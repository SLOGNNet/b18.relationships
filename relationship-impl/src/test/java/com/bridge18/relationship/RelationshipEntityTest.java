package com.bridge18.relationship;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.bridge18.relationship.entities.relationship.*;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.sun.org.apache.regexp.internal.RE;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RelationshipEntityTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("RelationshipEntityTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testBlockingOfCommandsBeforeCreation(){
        PersistentEntityTestDriver<RelationshipCommand, RelationshipEvent, RelationshipState> persistentEntityTestDriver =
                new PersistentEntityTestDriver(system, new RelationshipEntity(), "test-relationship-2");

        GetRelationship getRelationshipCmd = GetRelationship.builder().build();
        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> getOutcome = persistentEntityTestDriver.run(getRelationshipCmd);
        assertTrue(getOutcome.issues().get(0) instanceof PersistentEntityTestDriver.UnhandledCommand);

        UpdateRelationship updateRelationshipCmd = UpdateRelationship.builder().build();
        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> updateOutcome = persistentEntityTestDriver.run(updateRelationshipCmd);
        assertTrue(updateOutcome.issues().get(0) instanceof PersistentEntityTestDriver.UnhandledCommand);

        DeleteRelationship deleteRelationshipCmd = DeleteRelationship.builder().build();
        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> deleteOutcome = persistentEntityTestDriver.run(deleteRelationshipCmd);
        assertTrue(deleteOutcome.issues().get(0) instanceof PersistentEntityTestDriver.UnhandledCommand);
    }

    @Test
    public void testBlockingOfCommandsAfterDeletion(){
        PersistentEntityTestDriver<RelationshipCommand, RelationshipEvent, RelationshipState> persistentEntityTestDriver =
                new PersistentEntityTestDriver(system, new RelationshipEntity(), "test-relationship-3");

        CreateRelationship createRelationshipCmd = CreateRelationship.builder().build();
        persistentEntityTestDriver.run(createRelationshipCmd);

        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> createOutcome = persistentEntityTestDriver.run(createRelationshipCmd);
        assertTrue(createOutcome.issues().get(0) instanceof PersistentEntityTestDriver.UnhandledCommand);


        DeleteRelationship deleteRelationshipCmd = DeleteRelationship.builder().build();
        persistentEntityTestDriver.run(deleteRelationshipCmd);


        GetRelationship getRelationshipCmd = GetRelationship.builder().build();
        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> getOutcome = persistentEntityTestDriver.run(getRelationshipCmd);
        assertTrue(getOutcome.issues().get(0) instanceof PersistentEntityTestDriver.UnhandledCommand);

        UpdateRelationship updateRelationshipCmd = UpdateRelationship.builder().build();
        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> updateOutcome = persistentEntityTestDriver.run(updateRelationshipCmd);
        assertTrue(updateOutcome.issues().get(0) instanceof PersistentEntityTestDriver.UnhandledCommand);

        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> createOutcome_2 = persistentEntityTestDriver.run(createRelationshipCmd);
        assertTrue(createOutcome_2.issues().get(0) instanceof PersistentEntityTestDriver.UnhandledCommand);
    }


    @Test
    public void test() {
        PersistentEntityTestDriver<RelationshipCommand, RelationshipEvent, RelationshipState> persistentEntityTestDriver =
                new PersistentEntityTestDriver(system, new RelationshipEntity(), "test-relationship-1");

        Date startDate = new Date();
        Date terminationDate = new Date();
        PVector<Assignment> assignments = TreePVector.from(
                Arrays.asList(
                        Assignment.builder().assignment("assignment-1").type(AssignmentType.TRIP_SEGMENT).notes("notes-1").build(),
                        Assignment.builder().assignment("assignment-2").type(AssignmentType.LOAD).notes("notes-2").build(),
                        Assignment.builder().assignment("assignment-3").type(AssignmentType.TRIP_SEGMENT).notes("notes-3").build()
                )
        );

        CreateRelationship createRelationshipCmd = CreateRelationship.builder()
                .provider("provider-1")
                .customer("customer-1")
                .startDate(startDate)
                .terminationDate(terminationDate)
                .notes("notes-1")
                .assignments(assignments)
                .build();

        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> outcome1 = persistentEntityTestDriver.run(createRelationshipCmd);

        assertEquals(1, outcome1.events().size());

        RelationshipState relationshipState = (RelationshipState) outcome1.getReplies().get(0);
        assertEquals("test-relationship-1", relationshipState.getId());
        assertEquals(Optional.of("provider-1"), relationshipState.getProvider());
        assertEquals(Optional.of("customer-1"), relationshipState.getCustomer());
        assertEquals(Optional.of(startDate), relationshipState.getStartDate());
        assertEquals(Optional.of(terminationDate), relationshipState.getTerminationDate());
        assertEquals(Optional.of("notes-1"), relationshipState.getNotes());
        assertEquals(Optional.of(assignments), relationshipState.getAssignments());

        assertEquals(Collections.emptyList(), outcome1.issues());


        Date startDate2 = new Date();
        Date terminationDate2 = new Date();
        PVector<Assignment> assignments2 = TreePVector.from(
                Arrays.asList(
                        Assignment.builder().assignment("assignment-4").type(AssignmentType.TRIP_SEGMENT).notes("notes-4").build(),
                        Assignment.builder().assignment("assignment-5").type(AssignmentType.TRIP_SEGMENT).notes("notes-5").build(),
                        Assignment.builder().assignment("assignment-6").type(AssignmentType.LOAD).notes("notes-6").build()
                )
        );

        UpdateRelationship updateRelationshipCmd = UpdateRelationship.builder()
                .provider("provider-2")
                .customer("customer-2")
                .startDate(startDate2)
                .terminationDate(terminationDate2)
                .notes("notes-2")
                .assignments(assignments2)
                .build();
        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> outcome2 = persistentEntityTestDriver.run(updateRelationshipCmd);

        assertEquals(1, outcome2.events().size());

        relationshipState = (RelationshipState) outcome2.getReplies().get(0);

        assertEquals("test-relationship-1", relationshipState.getId());
        assertEquals(Optional.of("provider-2"), relationshipState.getProvider());
        assertEquals(Optional.of("customer-2"), relationshipState.getCustomer());
        assertEquals(Optional.of(startDate2), relationshipState.getStartDate());
        assertEquals(Optional.of(terminationDate2), relationshipState.getTerminationDate());
        assertEquals(Optional.of("notes-2"), relationshipState.getNotes());
        assertEquals(Optional.of(assignments2), relationshipState.getAssignments());

        assertEquals(1, outcome2.getReplies().size());
        assertEquals(Collections.emptyList(), outcome2.issues());


        GetRelationship getRelationshipCmd = GetRelationship.builder().build();
        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> outcome3 = persistentEntityTestDriver.run(getRelationshipCmd);

        assertEquals(0, outcome3.events().size());

        relationshipState = (RelationshipState) outcome3.getReplies().get(0);

        assertEquals("test-relationship-1", relationshipState.getId());
        assertEquals(Optional.of("provider-2"), relationshipState.getProvider());
        assertEquals(Optional.of("customer-2"), relationshipState.getCustomer());
        assertEquals(Optional.of(startDate2), relationshipState.getStartDate());
        assertEquals(Optional.of(terminationDate2), relationshipState.getTerminationDate());
        assertEquals(Optional.of("notes-2"), relationshipState.getNotes());

        assertEquals(1, outcome2.getReplies().size());
        assertEquals(Collections.emptyList(), outcome2.issues());


        DeleteRelationship deleteRelationshipCmd = DeleteRelationship.builder().build();
        PersistentEntityTestDriver.Outcome<RelationshipEvent, RelationshipState> outcome4 = persistentEntityTestDriver.run(deleteRelationshipCmd);
        assertEquals(1, outcome4.events().size());
        assertEquals(RelationshipDeleted.builder().id("test-relationship-1").build(),
                outcome4.events().get(0));
    }
}
