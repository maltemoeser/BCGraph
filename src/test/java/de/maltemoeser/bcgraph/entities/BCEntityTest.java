package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import static org.junit.Assert.*;

public class BCEntityTest extends Neo4jTest {

    @Test
    public void testUnderlyingNode() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            Node node = graphDatabaseService.createNode();
            BCBlock block = new BCBlock(node);
            assertEquals(node, block.getUnderlyingNode());
        }
    }

    @Test
    public void testIsBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            assertTrue(getNewBlock().isBlock());
        }
    }

    @Test
    public void testIsTransaction() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            assertTrue(getNewTransaction().isTransaction());
        }
    }

    @Test
    public void testIsAddress() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            assertTrue(getNewAddress().isAddress());
        }
    }

    @Test
    public void testIsOutput() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            BCOutput output = getNewOutput();

            // an output is defined by its relation to a transaction, therefore this must be false
            assertFalse(output.isOutput());

            // now we add the relevant relationship
            transaction.addOutput(output);
            assertTrue(output.isOutput());
        }
    }
}
