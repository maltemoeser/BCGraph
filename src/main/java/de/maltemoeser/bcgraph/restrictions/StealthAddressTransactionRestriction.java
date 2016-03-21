package de.maltemoeser.bcgraph.restrictions;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;

import java.util.Arrays;


public class StealthAddressTransactionRestriction implements Restriction<Transaction> {

    @Override
    public boolean evaluate(Transaction transaction) {
        for(TransactionOutput output : transaction.getOutputs()) {
            Script script = output.getScriptPubKey();
            if(script.isOpReturn()) {
                return isStealthAddressPayload(script);
            }
        }
        return false;
    }

    protected static boolean isStealthAddressPayload(Script script) {
        byte[] data = script.getChunks().get(1).data;

        if(data == null) {
            return false;
        }

        if(data.length != 38) {
            return false;
        }

        if(data[0] == 6) {
            ECKey key = ECKey.fromPublicOnly(Arrays.copyOfRange(data, 5, data.length));
            return true;
        }

        return false;
    }
}
