package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.constants.RelType;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import java.util.Collection;

import static org.junit.Assert.*;

public class BCBlockTest extends Neo4jTest {

    final static String blockHash = "000000000000000011d4990b549b02e1632f32d42349ab2b0071cadcd848d2e1";
    final static int blockHeight = 373334;
    final static long blockUnixTime = 1441584698;

    @Test
    public void testBCBlockGetters() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {

            Node node = graphDatabaseService.createNode(LabelType.Block);
            node.setProperty(NodeProperty.BLOCK_HASH, blockHash);
            node.setProperty(NodeProperty.BLOCK_HEIGHT, blockHeight);
            node.setProperty(NodeProperty.BLOCK_UNIXTIME, blockUnixTime);

            BCBlock block = new BCBlock(node);

            assertTrue(block.isBlock());
            assertEquals(blockHash, block.getHash());
            assertEquals(blockHeight, block.getHeight());
            assertEquals(blockUnixTime, block.getUnixTime());
        }
    }

    @Test
    public void testBCBlockSetters() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block = getNewBlock();
            block.setHash(blockHash);
            block.setHeight(blockHeight);
            block.setUnixTime(blockUnixTime);

            Node node = block.getUnderlyingNode();
            assertTrue(node.hasLabel(LabelType.Block));
            assertEquals(blockHash, node.getProperty(NodeProperty.BLOCK_HASH));
            assertEquals(blockHeight, node.getProperty(NodeProperty.BLOCK_HEIGHT));
            assertEquals(blockUnixTime, node.getProperty(NodeProperty.BLOCK_UNIXTIME));
        }
    }

    @Test
    public void testAddTransactionToBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block = new BCBlock(graphDatabaseService.createNode(LabelType.Block));
            BCTransaction transaction = new BCTransaction(graphDatabaseService.createNode(LabelType.Transaction));
            block.addTransaction(transaction);

            Node blockNode = block.getUnderlyingNode();
            Node transactionNode = transaction.getUnderlyingNode();

            assertTrue(blockNode.hasRelationship(RelType.IN_BLOCK, Direction.INCOMING));
            assertTrue(transactionNode.hasRelationship(RelType.IN_BLOCK, Direction.OUTGOING));
            assertTrue(transactionNode
                    .getSingleRelationship(RelType.IN_BLOCK, Direction.OUTGOING)
                    .getEndNode()
                    .equals(blockNode));
        }
    }

    @Test
    public void testNumberOfTransactionsInBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block = getNewBlock();
            assertEquals(0, block.getNumberOfTransactions());

            BCTransaction transaction1 = new BCTransaction(graphDatabaseService.createNode(LabelType.Transaction));
            block.addTransaction(transaction1);
            BCTransaction transaction2 = new BCTransaction(graphDatabaseService.createNode(LabelType.Transaction));
            block.addTransaction(transaction2);
            assertEquals(2, block.getNumberOfTransactions());

            Collection<BCTransaction> transactions = block.getTransactions();
            assertEquals(2, transactions.size());
            assertTrue(transactions.contains(transaction1));
            assertTrue(transactions.contains(transaction2));
        }
    }

    @Test
    public void testConnectToPredecessor() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block1 = getNewBlock();
            BCBlock block2 = getNewBlock();

            block2.connectToPreviousBlock(block1);

            assertTrue(block1.getUnderlyingNode().hasRelationship(RelType.PREV_BLOCK, Direction.INCOMING));
            assertTrue(block2.getUnderlyingNode().hasRelationship(RelType.PREV_BLOCK, Direction.OUTGOING));

            assertEquals(
                    block2.getUnderlyingNode(),
                    block1.getUnderlyingNode().getSingleRelationship(RelType.PREV_BLOCK, Direction.INCOMING)
                            .getOtherNode(block1.getUnderlyingNode()));
            assertEquals(
                    block1.getUnderlyingNode(),
                    block2.getUnderlyingNode().getSingleRelationship(RelType.PREV_BLOCK, Direction.OUTGOING)
                            .getOtherNode(block2.getUnderlyingNode()));
        }
    }

    @Test
    public void testGetPreviousAndNextBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block1 = getNewBlock();
            BCBlock block2 = getNewBlock();
            BCBlock block3 = getNewBlock();

            block2.connectToPreviousBlock(block1);
            block3.connectToPreviousBlock(block2);

            assertEquals(block1, block2.getPreviousBlock());
            assertEquals(block2, block3.getPreviousBlock());
            assertEquals(block2, block1.getNextBlock());
            assertEquals(block3, block2.getNextBlock());
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerExceptionsInGetPreviousBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block1 = getNewBlock();
            block1.getPreviousBlock();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointerExceptionsInGetNextBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block1 = getNewBlock();
            block1.getNextBlock();
        }
    }

    @Test
    public void testSetLatestBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block = getNewBlock();
            setLatestBlockFalse(block);
            setLatestBlockTrue(block);
            setLatestBlockFalse(block);
        }
    }

    private void setLatestBlockFalse(BCBlock block) {
        block.setLatestBlock(false);
        assertFalse(block.getUnderlyingNode().hasLabel(LabelType.LatestBlock));
        assertFalse(block.isLatestBlock());
    }

    private void setLatestBlockTrue(BCBlock block) {
        block.setLatestBlock(true);
        assertTrue(block.getUnderlyingNode().hasLabel(LabelType.LatestBlock));
        assertTrue(block.isLatestBlock());
    }

    @Test
    public void testEquals() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block1 = getNewBlock();
            BCBlock block2 = new BCBlock(block1.getUnderlyingNode());
            BCBlock block3 = getNewBlock();

            assertTrue(block1.equals(block2));
            assertFalse(block1.equals(block3));
        }
    }
}
