package com.bridge18.relationship;

import akka.actor.ActorSystem;
import com.bridge18.relationship.api.LagomRelationshipService;
import com.bridge18.relationship.dto.relationship.AssignmentDTO;
import com.bridge18.relationship.dto.relationship.RelationshipDTO;
import com.bridge18.relationship.entities.relationship.AssignmentType;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.inject.Bindings.bind;

public class LagomRelationshipServiceTest {

    static ActorSystem system;

    private final static ServiceTest.Setup setup = defaultSetup().withCassandra(true)
            .configureBuilder(b ->
                    b.configure("cassandra-query-journal.eventual-consistency-delay", "0")
                            .overrides(bind(ReadSide.class).to(Mockito.mock(ReadSide.class).getClass()))
            );

    private static ServiceTest.TestServer testServer;

    private static LagomRelationshipService testService;

    @BeforeClass
    public static void beforeAll() {
        system = ActorSystem.create("LagomRelationshipServiceTest");
        testServer = ServiceTest.startServer(setup);
        testService = testServer.client(LagomRelationshipService.class);
    }

    @Test
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        Date startDate = new Date();
        Date terminationDate = new Date();
        List<AssignmentDTO> assignments = Arrays.asList(
                new AssignmentDTO("assignment-1", AssignmentType.LOAD, "notes-1"),
                new AssignmentDTO("assignment-2", AssignmentType.TRIP_SEGMENT, "notes-2"),
                new AssignmentDTO("assignment-3", AssignmentType.LOAD, "notes-3")
        );
        RelationshipDTO inputRelationshipDTO = new RelationshipDTO(null, "provider-1",
                "customer-1", startDate, terminationDate, "notes-1", assignments);
        RelationshipDTO createdRelationshipDTO = testService.createRelationship().invoke(inputRelationshipDTO)
                .toCompletableFuture().get(10, SECONDS);

        assertNotNull(createdRelationshipDTO.id);
        assertEquals("provider-1", createdRelationshipDTO.provider);
        assertEquals("customer-1", createdRelationshipDTO.customer);
        assertEquals(startDate, createdRelationshipDTO.startDate);
        assertEquals(terminationDate, createdRelationshipDTO.terminationDate);
        assertEquals("notes-1", createdRelationshipDTO.notes);
        assertEquals(assignments, createdRelationshipDTO.assignments);

        inputRelationshipDTO = new RelationshipDTO(null, "provider-2",
                "customer-2", startDate, terminationDate, "notes-2", assignments);
        RelationshipDTO updatedRelationshipDTO = testService.updateRelationship(createdRelationshipDTO.id)
                .invoke(inputRelationshipDTO).toCompletableFuture().get(10, SECONDS);

        assertEquals(createdRelationshipDTO.id, updatedRelationshipDTO.id);
        assertEquals("provider-2", updatedRelationshipDTO.provider);
        assertEquals("customer-2", updatedRelationshipDTO.customer);
        assertEquals(startDate, updatedRelationshipDTO.startDate);
        assertEquals(terminationDate, updatedRelationshipDTO.terminationDate);
        assertEquals("notes-2", updatedRelationshipDTO.notes);
        assertEquals(assignments, updatedRelationshipDTO.assignments);

        RelationshipDTO getRelationshipDTO = testService.getRelationship(createdRelationshipDTO.id)
                .invoke().toCompletableFuture().get(10, SECONDS);
        assertEquals(createdRelationshipDTO.id, getRelationshipDTO.id);
        assertEquals("provider-2", getRelationshipDTO.provider);
        assertEquals("customer-2", getRelationshipDTO.customer);
        assertEquals(startDate, getRelationshipDTO.startDate);
        assertEquals(terminationDate, getRelationshipDTO.terminationDate);
        assertEquals("notes-2", getRelationshipDTO.notes);
        assertEquals(assignments, getRelationshipDTO.assignments);
    }
}
