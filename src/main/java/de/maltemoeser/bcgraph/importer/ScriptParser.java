package de.maltemoeser.bcgraph.importer;

import de.maltemoeser.bcgraph.constants.OutputType;
import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.utils.ScriptUtils;
import org.bitcoinj.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptParser {

    final static Logger logger;

    static {
        logger = LoggerFactory.getLogger("output");
    }

    protected static void setOutputType(BCOutput bcOutput, OutputType outputType, Script script) {
        switch (outputType) {
            case OP_RETURN:
                bcOutput.setOpReturn(true);
                break;
            case P2SH:
                bcOutput.setPayToScriptHash(true);
                break;
            case MULTISIG:
                int m = script.getNumberOfSignaturesRequiredToSpend();
                int n = ScriptUtils.getTotalNumberOfSignaturesInMultiSigScript(script);
                bcOutput.setSentToMultiSig(m, n);
                break;
            case UNKNOWN:
                try {
                    logger.info(bcOutput.getInternalId() + ": " + script.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Determines the script type of a given script.
     *
     * @param script should be in the form of a valid scriptPubKey
     * @return a value of OutputType
     */
    public static OutputType getOutputTypeFromScript(Script script) {
        OutputType outputType = OutputType.UNKNOWN;

        try {
            if (script.isOpReturn()) {
                outputType = OutputType.OP_RETURN;
            } else if (script.isPayToScriptHash()) {
                outputType = OutputType.P2SH;
            } else if (script.isSentToMultiSig()) {
                outputType = OutputType.MULTISIG;
            } else if (script.isSentToAddress()) {
                outputType = OutputType.ADDRESS;
            } else if (script.isSentToRawPubKey()) {
                outputType = OutputType.PUBKEY;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return outputType;
    }
}
