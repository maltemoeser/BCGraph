package de.maltemoeser.bcgraph.importer;

import de.maltemoeser.bcgraph.constants.OutputType;
import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import de.maltemoeser.bcgraph.testing.TestUtils;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScriptParserTest extends Neo4jTest {

    @Test
    public void testGetStandardOutputType() {
        TransactionOutput transactionOutput = TestUtils.getStandardTransactionOutput();
        testOutputForType(transactionOutput, OutputType.ADDRESS);
    }

    @Test
    public void testGetMultisigOutputType() {
        TransactionOutput transactionOutput = TestUtils.getMultisigTransactionOutput();
        testOutputForType(transactionOutput, OutputType.MULTISIG);
    }

    @Test
    public void testGetP2SHOutputType() {
        TransactionOutput transactionOutput = TestUtils.getP2SHTransactionOutput();
        testOutputForType(transactionOutput, OutputType.P2SH);
    }

    @Test
    public void testGetOpReturnOutputType() {
        TransactionOutput transactionOutput = TestUtils.getOpReturnTransactionOutput();
        testOutputForType(transactionOutput, OutputType.OP_RETURN);
    }

    public void testOutputForType(TransactionOutput transactionOutput, OutputType outputType) {
        assertEquals(outputType, ScriptParser.getOutputTypeFromScript(transactionOutput.getScriptPubKey()));
    }

    @Test
    public void testSetP2SHOutputType() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput output = getNewOutput();
            ScriptParser.setOutputType(output, OutputType.P2SH, null);
            assertTrue(output.isPayToScriptHash());
        }
    }

    @Test
    public void testSetMultisigOutputType() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput output = getNewOutput();
            // 3 keys
            List<ECKey> keys = new ArrayList<>();
            keys.add(new ECKey());
            keys.add(new ECKey());
            keys.add(new ECKey());
            // 2-of-3 script
            Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);
            ScriptParser.setOutputType(output, OutputType.MULTISIG, script);
            assertTrue(output.isSentToMultiSig());
            assertEquals(2, output.getNumberOfRequiredSignatures());
            assertEquals(3, output.getNumberOfTotalSignatures());
        }
    }

    @Test
    public void testSetOpReturnOutputType() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput output = getNewOutput();
            ScriptParser.setOutputType(output, OutputType.OP_RETURN, null);
            assertTrue(output.isOpReturn());
        }
    }
}
