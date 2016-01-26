package de.maltemoeser.bcgraph.importer;

import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import de.maltemoeser.bcgraph.testing.TestUtils;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransactionImporterTest extends Neo4jTest {

    TransactionImporter transactionImporter;

    @Before
    public void createTransactionImporter() {
        transactionImporter = injector.getInstance(TransactionImporter.class);
    }

    @Test
    public void testCreateStandardOutput() {
        TransactionOutput transactionOutput = TestUtils.getStandardTransactionOutput();

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {

            BCOutput output = transactionImporter.createOutputNode(transactionOutput);

            assertEquals(16077121L, output.getValue());
            assertEquals(0, output.getIndex());
        }
    }

    @Test
    public void testCreateP2SHOutput() {
        TransactionOutput transactionOutput = TestUtils.getP2SHTransactionOutput();

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput output = transactionImporter.createOutputNode(transactionOutput);

            assertEquals(38252466L, output.getValue());
            assertEquals(1, output.getIndex());

            transactionImporter.parseOutputScript(output, transactionOutput.getScriptPubKey());

            assertTrue(output.isPayToScriptHash());
        }
    }

    @Test
    public void testCreateMultisigOutput() {
        Transaction tx = TestUtils.getMultiSigOutputTransaction();
        TransactionOutput transactionOutput = tx.getOutput(0);

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput output = transactionImporter.createOutputNode(transactionOutput);

            assertEquals(7800L, output.getValue());
            assertEquals(0, output.getIndex());

            transactionImporter.parseOutputScript(output, transactionOutput.getScriptPubKey());

            assertEquals(1, output.getNumberOfRequiredSignatures());
            assertEquals(3, output.getNumberOfTotalSignatures());
            assertTrue(output.isSentToMultiSig());
        }
    }

    @Test
    public void testCreateTransactionNode() {
        Transaction bitcoinTransaction = TestUtils.getP2PKHTransaction();
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            transactionImporter.setBitcoinTransaction(bitcoinTransaction);
            transactionImporter.createTransactionNode();
            BCTransaction transaction = transactionImporter.getBCTransaction();
            assertEquals("41c6aa2787f66574f9400bd1d69cafb9fa8a0919a85e40a22b23c17da9526556", transaction.getHash());
            assertEquals(0, transaction.getLockTime());
            assertFalse(transaction.isCoinbase());
        }
    }

    @Test
    public void testCreateCoinbaseTransactionNode() {
        Transaction bitcoinTransaction = TestUtils.getCoinbaseTransaction();
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            transactionImporter.setBitcoinTransaction(bitcoinTransaction);
            transactionImporter.createTransactionNode();
            BCTransaction transaction = transactionImporter.getBCTransaction();
            assertEquals("2556a4729a22b9db53ef78ccf74d51599c0c17565dd11445df3a4b474e3b9d15", transaction.getHash());
            assertTrue(transaction.isCoinbase());
        }
    }

    @Test
    public void testCreateOutputs() {
        Transaction bitcoinTransaction = TestUtils.getP2PKHTransaction();

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            transactionImporter.setBitcoinTransaction(bitcoinTransaction);
            transactionImporter.createTransactionNode();
            transactionImporter.importOutputs();
            BCTransaction transaction = transactionImporter.getBCTransaction();
            assertEquals(2, transaction.getNumberOfOutputs());
        }
    }

    @Test
    public void testCreateInputs() {
        Transaction bitcoinTransaction = TestUtils.getP2PKHTransaction();

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {

            BCTransaction previousTx = getNewTransaction();
            previousTx.setHash("1d4ecf2069021361d6bd7db0c84eb395b1aa9adaa6157ac68f9d40f05c013f16");
            BCOutput output0 = getNewOutput();
            output0.setIndex(0);
            previousTx.addOutput(output0);
            BCOutput output1 = getNewOutput();
            output1.setIndex(1);
            previousTx.addOutput(output1);

            transactionImporter.setBitcoinTransaction(bitcoinTransaction);
            transactionImporter.createTransactionNode();
            transactionImporter.importInputs();
            BCTransaction transaction = transactionImporter.getBCTransaction();

            // Check correct creation of inputs
            assertEquals(1, transaction.getNumberOfInputs());
            assertTrue(transaction.getInputs().contains(output1));

            // Check link to previous transaction
            assertTrue(transaction.getPreviousTransactions().contains(previousTx));
        }
    }

    @Test
    public void testCreateFullP2PKHTransaction() {
        Transaction bitcoinTransaction = TestUtils.getP2PKHTransaction();

        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {

            createPreviousTransactionForP2PKHTransaction();

            transactionImporter.importTransaction(bitcoinTransaction);
            BCTransaction transaction = transactionImporter.getBCTransaction();

            assertEquals(1, transaction.getNumberOfInputs());
            assertEquals(2, transaction.getNumberOfOutputs());
            assertEquals(100000L, transaction.getFee());
        }
    }

    @Test
    public void testUpdateP2SHOutput() {
        Transaction bitcoinTransaction = TestUtils.getP2SHInputTransaction();
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction previousTx1 = getNewTransaction();
            previousTx1.setHash("cae9065abd72bf56015a48fdb1d15fb05dd2678996a42febfec1ebdc76dbef21");
            BCOutput output0 = getNewOutput();
            output0.setIndex(0);
            output0.setValue(14282385L);
            output0.setPayToScriptHash(true);
            previousTx1.addOutput(output0);
            BCOutput output1 = getNewOutput();
            output1.setIndex(1);
            output1.setValue(7100000L);
            previousTx1.addOutput(output1);

            BCTransaction previousTx2 = getNewTransaction();
            previousTx2.setHash("f2f078f8a1b2d5e5735ca82389146ff2e13585a52da13c488dc2194fd6849b21");
            BCOutput output2 = getNewOutput();
            output2.setIndex(0);
            output2.setValue(7100000L);
            previousTx2.addOutput(output2);
            BCOutput output3 = getNewOutput();
            output3.setIndex(1);
            output3.setValue(28904386L);
            output3.setPayToScriptHash(true);
            previousTx2.addOutput(output3);

            transactionImporter.importTransaction(bitcoinTransaction);
            assertEquals(2, output0.getNumberOfRequiredSignatures());
            assertEquals(2, output0.getNumberOfTotalSignatures());
            assertTrue(output0.isPayToScriptHash());
            assertTrue(output0.isSentToMultiSig());
        }
    }

    private void createPreviousTransactionForP2PKHTransaction() {
        BCTransaction previousTx = getNewTransaction();
        previousTx.setHash("1d4ecf2069021361d6bd7db0c84eb395b1aa9adaa6157ac68f9d40f05c013f16");

        BCOutput output0 = getNewOutput();
        output0.setIndex(0);
        output0.setValue(4183734);
        previousTx.addOutput(output0);

        BCOutput output1 = getNewOutput();
        output1.setIndex(1);
        output1.setValue(1990154184);
        previousTx.addOutput(output1);
    }
}
