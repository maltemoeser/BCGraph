package de.maltemoeser.bcgraph.utils;

import de.maltemoeser.bcgraph.testing.BitcoinTest;
import de.maltemoeser.bcgraph.testing.TestUtils;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScriptUtilsTest extends BitcoinTest {

    ECKey key1;
    ECKey key2;
    ECKey key3;
    List<ECKey> twoKeys;
    List<ECKey> threeKeys;

    @Before
    public void setUpKeysAndScripts() {
        key1 = new ECKey();
        key2 = new ECKey();
        key3 = new ECKey();

        twoKeys = new LinkedList<>();
        twoKeys.add(key1);
        twoKeys.add(key2);

        threeKeys = new LinkedList<>();
        threeKeys.add(key1);
        threeKeys.add(key2);
        threeKeys.add(key3);
    }

    @Test
    public void testIsTwoOfTwoMultiSig(){
        // 2-of-2
        Script validMultiSigOutputScript = ScriptBuilder.createMultiSigOutputScript(2, twoKeys);
        assertTrue(ScriptUtils.isTwoOfTwoMultiSig(validMultiSigOutputScript));

        // 1-of-2
        Script invalidMultiSigOutputScript = ScriptBuilder.createMultiSigOutputScript(1, twoKeys);
        assertFalse(ScriptUtils.isTwoOfTwoMultiSig(invalidMultiSigOutputScript));

        // Standard output
        Script standardOutputScript = ScriptBuilder.createOutputScript(new ECKey());
        assertFalse(ScriptUtils.isTwoOfTwoMultiSig(standardOutputScript));
    }

    @Test
    public void testGetScriptFromP2SHInput(){
        Transaction tx = TestUtils.getP2SHInputTransaction();
        TransactionInput input = tx.getInput(0);
        Script script = ScriptUtils.getScriptFromP2SHInput(input);
        assertTrue(script.isSentToMultiSig());
        assertTrue(ScriptUtils.isTwoOfTwoMultiSig(script));
    }

    @Test
    public void testOpcodeAsInteger() {
        int nil = ScriptOpCodes.OP_0;
        assertEquals(0, ScriptUtils.opcodeAsInteger(nil));

        int one = ScriptOpCodes.OP_1;
        assertEquals(1, ScriptUtils.opcodeAsInteger(one));

        int sixteen = ScriptOpCodes.OP_16;
        assertEquals(16, ScriptUtils.opcodeAsInteger(sixteen));
    }
}
