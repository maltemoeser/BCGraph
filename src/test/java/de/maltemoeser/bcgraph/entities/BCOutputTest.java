package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import java.util.Collection;

import static org.junit.Assert.*;

public class BCOutputTest extends Neo4jTest {

    static final long OUTPUT_VALUE = 1337;
    static final int OUTPUT_INDEX = 0;
    Node node = null;
    BCOutput output = null;

    @Before
    public void setUp() {
        try (org.neo4j.graphdb.Transaction tx = graphDatabaseService.beginTx()) {
            node = graphDatabaseService.createNode();
            node.setProperty(NodeProperty.OUTPUT_VALUE, OUTPUT_VALUE);
            node.setProperty(NodeProperty.OUTPUT_INDEX, OUTPUT_INDEX);
            node.addLabel(LabelType.P2SH);
            node.setProperty(NodeProperty.OUTPUT_MULTISIG_TOTAL, 3);
            node.setProperty(NodeProperty.OUTPUT_MULTISIG_REQUIRED, 2);

            output = new BCOutput(node);

            tx.success();
        }
    }

    @Test
    public void testBCOutput() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            assertEquals(output.getValue(), OUTPUT_VALUE);
            assertEquals(output.getIndex(), OUTPUT_INDEX);

            assertTrue(output.isPayToScriptHash());

            assertFalse(output.isSentToMultiSig());

            assertEquals(output.getNumberOfRequiredSignatures(), 2);
            assertEquals(output.getNumberOfTotalSignatures(), 3);

            output.setPayToScriptHash(false);
            assertFalse(output.isPayToScriptHash());

            // OP_RETURN
            assertFalse(output.isOpReturn());
            output.setOpReturn(true);
            assertTrue(output.isOpReturn());
            output.setOpReturn(false);
            assertFalse(output.isOpReturn());
        }
    }

    @Test
    public void testValueRanges() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput o = getNewOutput();
            o.setValue(0);
            assertEquals(0, o.getValue());
            o.setValue(2100000000000000L);
            assertEquals(2100000000000000L, o.getValue());
        }
    }

    @Test
    public void testSetSentToMultiSig() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            output.setSentToMultiSig(3, 6);
            assertTrue(output.isSentToMultiSig());
            assertEquals(output.getNumberOfRequiredSignatures(), 3);
            assertEquals(output.getNumberOfTotalSignatures(), 6);
            output.setSentToMultiSig(false);
            assertFalse(output.isSentToMultiSig());
        }
    }

    @Test
    public void testGetConnectedTransactions() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            Node node1 = graphDatabaseService.createNode();
            node1.addLabel(LabelType.Transaction);
            BCTransaction creating = new BCTransaction(node);

            Node node2 = graphDatabaseService.createNode();
            node2.addLabel(LabelType.Transaction);
            BCTransaction spending = new BCTransaction(node);

            node = graphDatabaseService.createNode();
            output = new BCOutput(node);
            creating.addOutput(output);

            assertNull(output.getRedeemingTransaction());

            assertFalse(output.isSpent());
            spending.addInput(output);
            assertTrue(output.isSpent());

            assertEquals(creating, output.getTransaction());
            assertEquals(spending, output.getRedeemingTransaction());
        }
    }

    @Test
    public void testGetAddress() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput output = getNewOutput();
            BCAddress address1 = getNewAddress();

            assertEquals(0, output.getNumberOfAddresses());

            output.connectToAddress(address1);

            assertEquals(1, output.getNumberOfAddresses());
            assertEquals(address1, output.getSingleAddress());

            BCAddress address2 = getNewAddress();
            output.connectToAddress(address2);

            Collection<BCAddress> addresses = output.getAddresses();
            assertEquals(2, addresses.size());
            assertTrue(addresses.contains(address1));
            assertTrue(addresses.contains(address2));
        }
    }
}
