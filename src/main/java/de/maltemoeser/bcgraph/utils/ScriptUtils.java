package de.maltemoeser.bcgraph.utils;

import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptOpCodes;

import java.util.List;

import static org.bitcoinj.script.ScriptOpCodes.*;

public class ScriptUtils {

    private ScriptUtils() {
        throw new AssertionError();
    }

    /**
     * Detects if a script is a two-of-two escrow multisig script.
     */
    public static boolean isTwoOfTwoMultiSig(Script script) {
        List<ScriptChunk> chunks = script.getChunks();
        if (!script.isSentToMultiSig()) return false;
        if (chunks.size() != 5) return false;

        // Check if it requires two signatures
        ScriptChunk m = chunks.get(0);
        return m.equalsOpCode(ScriptOpCodes.OP_2);
    }

    public static int getTotalNumberOfSignaturesInMultiSigScript(Script script) {
        List<ScriptChunk> chunks = script.getChunks();
        ScriptChunk n = chunks.get(chunks.size() - 2);
        if (!n.isOpCode()) throw new ScriptException("Not a valid multi-sig transaction");
        return opcodeAsInteger(n.opcode);
    }

    // Inspired by decodeFromOpN in bitcoinj.script.Script
    public static int opcodeAsInteger(int opcode) {
        if (opcode == OP_0) {
            return 0;
        } else if (opcode >= OP_1 && opcode <= OP_16) {
            return opcode + 1 - OP_1;
        } else {
            throw new UnsupportedOperationException("Cannot convert opcodes larger than OP_16 to int.");
        }
    }

    public static Script getScriptFromP2SHInput(TransactionInput input) {
        List<ScriptChunk> chunks = input.getScriptSig().getChunks();
        ScriptChunk serializedScript = chunks.get(chunks.size() - 1);
        return new Script(serializedScript.data);
    }
}
