package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.constants.RelType;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class BCTransactionTest extends Neo4jTest {

    static final String transactionHash = "ac546efddf18dcfcb4a42de81e38dcd00ecaa6a3c8e910954ffd6eadf81f90e8";
    static final long transactionValue = 2533646437L;
    static final long transactionFee = 10000L;
    static final long transactionLockTime = 1;

    @Test
    public void testBCTransactionGetter() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            Node node = graphDatabaseService.createNode(LabelType.Transaction);
            node.setProperty(NodeProperty.TRANSACTION_HASH, transactionHash);
            node.setProperty(NodeProperty.TRANSACTION_VALUE, transactionValue);
            node.setProperty(NodeProperty.TRANSACTION_FEE, transactionFee);
            node.setProperty(NodeProperty.TRANSACTION_LOCKTIME, transactionLockTime);

            BCTransaction transaction = new BCTransaction(node);

            assertTrue(transaction.isTransaction());
            assertEquals(transactionHash, transaction.getHash());
            assertEquals(transactionValue, transaction.getValue());
            assertEquals(transactionFee, transaction.getFee());
            assertEquals(transactionLockTime, transaction.getLockTime());
        }
    }

    @Test
    public void testGetHeight() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            BCBlock block = getNewBlock();
            block.addTransaction(transaction);
            final int HEIGHT = 12;
            block.setHeight(HEIGHT);
            assertEquals(HEIGHT, transaction.getHeight());
        }
    }

    @Test
    public void testBCTransactionSetter() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();

            transaction.setHash(transactionHash);
            transaction.setValue(transactionValue);
            transaction.setFee(transactionFee);
            transaction.setLockTime(transactionLockTime);

            assertEquals(transactionHash, transaction.getUnderlyingNode().getProperty(NodeProperty.TRANSACTION_HASH));
            assertEquals(transactionValue, transaction.getUnderlyingNode().getProperty(NodeProperty.TRANSACTION_VALUE));
            assertEquals(transactionFee, transaction.getUnderlyingNode().getProperty(NodeProperty.TRANSACTION_FEE));
            assertEquals(transactionLockTime, transaction.getUnderlyingNode().getProperty(NodeProperty.TRANSACTION_LOCKTIME));
        }
    }

    @Test
    public void testGraphBasedProperties() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();

            for (int i = 0; i < 2; i++) {
                transaction.addInput(getNewOutput());
            }

            for (int i = 0; i < 3; i++) {
                transaction.addOutput(getNewOutput());
            }

            assertEquals(2, transaction.getNumberOfInputs());
            assertEquals(3, transaction.getNumberOfOutputs());

            assertEquals(2, transaction.getInputs().size());
            assertEquals(3, transaction.getOutputs().size());
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidOutputIndex() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            transaction.addOutput(getNewOutput(0));
            transaction.addOutput(getNewOutput(1));

            // should fail
            transaction.getOutputByIndex(2);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testMissingOutputIndex() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            transaction.addOutput(getNewOutput(0));
            transaction.addOutput(getNewOutput(2));

            // should fail
            transaction.getOutputByIndex(1);
        }
    }


    @Test
    public void testIsCoinbase() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            assertFalse(transaction.isCoinbase());
            transaction.setCoinbase(true);
            assertTrue(transaction.isCoinbase());
            transaction.setCoinbase(false);
            assertFalse(transaction.isCoinbase());
        }
    }

    @Test
    public void testGetBlock() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCBlock block = getNewBlock();
            BCTransaction transaction = getNewTransaction();
            block.addTransaction(transaction);
            assertEquals(block, transaction.getBlock());
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void violateUpperLimitOfOutputs() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            BCOutput output = getNewOutput();
            output.setIndex(0);
            transaction.addOutput(output);
            transaction.getOutputByIndex(1);
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void violateLowerLimitOfOutputs() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            transaction.getOutputByIndex(-1);
        }
    }

    @Test
    public void testGetOutputByIndex() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            BCOutput output0 = getNewOutput(0);
            BCOutput output1 = getNewOutput(1);
            BCOutput output2 = getNewOutput(2);
            transaction.addOutput(output0);
            transaction.addOutput(output1);
            transaction.addOutput(output2);

            assertEquals(output1, transaction.getOutputByIndex(1));
            assertEquals(output2, transaction.getOutputByIndex(2));
            assertEquals(output0, transaction.getOutputByIndex(0));
        }
    }

    private BCOutput getNewOutput(int index) {
        BCOutput output = getNewOutput();
        output.setIndex(index);
        return output;
    }

    @Test
    public void testConnectToPreviousTransaction() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            BCTransaction prev = getNewTransaction();

            transaction.connectToPreviousTransaction(prev);

            assertTrue(prev.getUnderlyingNode().hasRelationship(Direction.INCOMING, RelType.PREV_TX));
        }
    }

    @Test
    public void testGetPreviousTransactions() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            BCTransaction prev1 = getNewTransaction();
            BCTransaction prev2 = getNewTransaction();

            transaction.connectToPreviousTransaction(prev1);
            transaction.connectToPreviousTransaction(prev2);

            assertEquals(2, transaction.getPreviousTransactions().size());
            assertTrue(transaction.getPreviousTransactions().contains(prev2));
            assertTrue(transaction.getPreviousTransactions().contains(prev1));
        }
    }

    @Test
    public void testConnectToPreviousTransactions() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            BCTransaction prev1 = getNewTransaction();
            BCTransaction prev2 = getNewTransaction();

            Collection<BCTransaction> prevTransactions = new ArrayList<>();
            prevTransactions.add(prev1);
            prevTransactions.add(prev2);

            transaction.connectToPreviousTransactions(prevTransactions);

            assertTrue(prev1.getUnderlyingNode().hasRelationship(Direction.INCOMING, RelType.PREV_TX));
            assertTrue(prev2.getUnderlyingNode().hasRelationship(Direction.INCOMING, RelType.PREV_TX));
        }
    }

    @Test
    public void testGetUnspentOutputs() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction1 = getNewTransaction();

            BCOutput output1 = getNewOutput();
            BCOutput output2 = getNewOutput();
            BCOutput output3 = getNewOutput();

            transaction1.addOutput(output1);
            transaction1.addOutput(output2);
            transaction1.addOutput(output3);

            BCTransaction transaction2 = getNewTransaction();
            transaction2.addInput(output2);

            Collection<BCOutput> utxo1 = transaction1.getUnspentOutputs();
            assertEquals(2, utxo1.size());
            assertTrue(utxo1.contains(output1));
            assertFalse(utxo1.contains(output2));
            assertTrue(utxo1.contains(output3));
        }
    }

    @Test
    public void testGetUnspentOutputsWithHeight() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction1 = getNewTransaction();
            BCBlock block1 = getNewBlock();
            block1.setHeight(1);

            BCOutput output1 = getNewOutput();
            BCOutput output2 = getNewOutput();
            BCOutput output3 = getNewOutput();

            transaction1.addOutput(output1);
            transaction1.addOutput(output2);
            transaction1.addOutput(output3);

            BCTransaction transaction2 = getNewTransaction();
            transaction2.addInput(output1);

            BCBlock block2 = getNewBlock();
            block2.setHeight(2);
            block2.addTransaction(transaction2);

            BCTransaction transaction3 = getNewTransaction();
            transaction3.addInput(output2);

            BCBlock block3 = getNewBlock();
            block3.setHeight(3);
            block3.addTransaction(transaction3);

            assertEquals(1, transaction1.getUnspentOutputs().size());
            assertEquals(1, transaction1.getUnspentOutputs(3).size());
            assertEquals(2, transaction1.getUnspentOutputs(2).size());
            assertEquals(3, transaction1.getUnspentOutputs(1).size());
        }
    }
}
