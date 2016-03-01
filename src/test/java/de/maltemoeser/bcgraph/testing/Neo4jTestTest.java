package de.maltemoeser.bcgraph.testing;

import de.maltemoeser.bcgraph.entities.BCAddress;
import de.maltemoeser.bcgraph.entities.BCBlock;
import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class Neo4jTestTest extends Neo4jTest {

    @Test
    public void testCreatingEntities() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {

            BCBlock block;
            BCTransaction transaction;
            BCOutput output;
            BCAddress address;

            block = getNewBlock();
            assertTrue(block.isBlock());

            transaction = getNewTransaction();
            assertTrue(transaction.isTransaction());

            transaction = getNewTransactionAtHeight(0);
            assertTrue(transaction.isTransaction());
            block = transaction.getBlock();
            assertTrue(block.isBlock());
            assertEquals(0, block.getHeight());

            output = getNewOutput(0, 9999999999L);
            assertEquals(0, output.getIndex());
            assertEquals(9999999999L, output.getValue());

            output = getNewOutput(1, 8888888888L, "address");
            assertEquals(1, output.getIndex());
            assertEquals(8888888888L, output.getValue());
            assertEquals(1, output.getAddresses().size());

            address = output.getSingleAddress();
            assertTrue(address.isAddress());
            assertEquals("address", address.getHash());

            address = getNewAddress();
            assertTrue(address.isAddress());
        }
    }
}
