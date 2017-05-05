package com.bridge18.relationship;

import com.bridge18.relationship.entities.relationship.*;
import com.bridge18.relationship.services.objects.RelationshipService;
import com.bridge18.relationship.services.objects.impl.RelationshipServiceImpl;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RelationshipServiceTest {
    private PersistentEntityRegistry persistentEntityRegistry;

    private RelationshipService relationshipService;

    @Before
    public void before() {
        persistentEntityRegistry = Mockito.mock(PersistentEntityRegistry.class);

        Mockito.doNothing().when(persistentEntityRegistry).register(RelationshipEntity.class);

        relationshipService = new RelationshipServiceImpl(persistentEntityRegistry);
    }

    @Test
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        PersistentEntityRef ref = Mockito.mock(PersistentEntityRef.class);

        Mockito.when(persistentEntityRegistry.refFor(Mockito.any(), Mockito.any())).thenReturn(ref);

        Date startDate = new Date();
        Date terminationDate = new Date();
        PVector<Assignment> assignments = TreePVector.from(
                Arrays.asList(
                        Assignment.builder().assignment("assignment-1").type(AssignmentType.TRIP_SEGMENT).notes("notes-1").build(),
                        Assignment.builder().assignment("assignment-2").type(AssignmentType.LOAD).notes("notes-2").build(),
                        Assignment.builder().assignment("assignment-3").type(AssignmentType.TRIP_SEGMENT).notes("notes-3").build()
                )
        );

        Mockito.when(ref.ask(Mockito.any(CreateRelationship.class))).thenReturn(CompletableFuture.completedFuture(
                RelationshipState.builder()
                        .id("1")
                        .provider("provider-1")
                        .customer("customer-1")
                        .startDate(startDate)
                        .terminationDate(terminationDate)
                        .notes("notes-1")
                        .assignments(assignments)
                        .build()
                )
        );

        RelationshipState relationshipState = relationshipService.createRelationship(
                Optional.of("provider-1"),
                Optional.of("customer-1"),
                Optional.of(startDate),
                Optional.of(terminationDate),
                Optional.of("notes-1"),
                Optional.of(assignments)
        ).toCompletableFuture().get(5, SECONDS);

        assertNotNull(relationshipState.getId());
        assertEquals(Optional.of("provider-1"), relationshipState.getProvider());
        assertEquals(Optional.of("customer-1"), relationshipState.getCustomer());
        assertEquals(Optional.of(startDate), relationshipState.getStartDate());
        assertEquals(Optional.of(terminationDate), relationshipState.getTerminationDate());
        assertEquals(Optional.of("notes-1"), relationshipState.getNotes());
        assertEquals(Optional.of(assignments), relationshipState.getAssignments());


        Mockito.when(ref.ask(Mockito.any(UpdateRelationship.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        RelationshipState.builder().id("1")
                                .provider("provider-2")
                                .customer("customer-2")
                                .startDate(startDate)
                                .terminationDate(terminationDate)
                                .notes("notes-2")
                                .assignments(assignments)
                                .build()
                ));

        RelationshipState updatedRelationshipState = relationshipService
                .updateRelationship(
                        relationshipState.getId(),
                        Optional.of("provider-2"),
                        Optional.of("customer-2"),
                        Optional.of(startDate),
                        Optional.of(terminationDate),
                        Optional.of("notes-2"),
                        Optional.of(assignments)
                )
                .toCompletableFuture().get(5, SECONDS);

        assertEquals(Optional.of("provider-2"), updatedRelationshipState.getProvider());
        assertEquals(Optional.of("customer-2"), updatedRelationshipState.getCustomer());
        assertEquals(Optional.of(startDate), updatedRelationshipState.getStartDate());
        assertEquals(Optional.of(terminationDate), updatedRelationshipState.getTerminationDate());
        assertEquals(Optional.of("notes-2"), updatedRelationshipState.getNotes());
        assertEquals(Optional.of(assignments), updatedRelationshipState.getAssignments());


        Mockito.when(ref.ask(Mockito.any(GetRelationship.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        RelationshipState.builder().id("1")
                                .provider("provider-2")
                                .customer("customer-2")
                                .startDate(startDate)
                                .terminationDate(terminationDate)
                                .notes("notes-2")
                                .assignments(assignments)
                                .build()
                ));

        RelationshipState getRelationshipState = relationshipService.getRelationship(
                relationshipState.getId()
        ).toCompletableFuture().get(5, SECONDS);

        assertEquals(Optional.of("provider-2"), getRelationshipState.getProvider());
        assertEquals(Optional.of("customer-2"), getRelationshipState.getCustomer());
        assertEquals(Optional.of(startDate), getRelationshipState.getStartDate());
        assertEquals(Optional.of(terminationDate), getRelationshipState.getTerminationDate());
        assertEquals(Optional.of("notes-2"), getRelationshipState.getNotes());
        assertEquals(Optional.of(assignments), getRelationshipState.getAssignments());
    }

    @Test
    public void testWithNull() throws InterruptedException, ExecutionException, TimeoutException {
        PersistentEntityRef ref = Mockito.mock(PersistentEntityRef.class);

        Mockito.when(persistentEntityRegistry.refFor(Mockito.any(), Mockito.any())).thenReturn(ref);

        Mockito.when(ref.ask(Mockito.any(UpdateRelationship.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        RelationshipState.builder()
                                .id("1")
                                .provider(Optional.empty())
                                .customer("customer-2")
                                .startDate(Optional.empty())
                                .terminationDate(Optional.empty())
                                .notes("notes-2")
                                .assignments(Optional.empty())
                                .build()
                ));

        RelationshipState updatedRelationshipState = relationshipService
                .updateRelationship(
                        "1",
                        Optional.empty(),
                        Optional.of("customer-2"),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of("notes-2"),
                        Optional.empty()
                )
                .toCompletableFuture().get(5, SECONDS);

        assertEquals(Optional.empty(), updatedRelationshipState.getProvider());
        assertEquals(Optional.of("customer-2"), updatedRelationshipState.getCustomer());
        assertEquals(Optional.empty(), updatedRelationshipState.getStartDate());
        assertEquals(Optional.empty(), updatedRelationshipState.getTerminationDate());
        assertEquals(Optional.of("notes-2"), updatedRelationshipState.getNotes());
        assertEquals(Optional.empty(), updatedRelationshipState.getAssignments());
    }
}
