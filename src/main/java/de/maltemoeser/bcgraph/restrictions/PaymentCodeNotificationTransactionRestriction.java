package de.maltemoeser.bcgraph.restrictions;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;

import java.util.Arrays;


public class PaymentCodeNotificationTransactionRestriction implements Restriction<Transaction> {

    @Override
    public boolean evaluate(Transaction transaction) {
        for(TransactionOutput output : transaction.getOutputs()) {
            Script script = output.getScriptPubKey();
            if(script.isOpReturn()) {
                return isPaymentCodePayload(script);
            }
        }
        return false;
    }

    protected static boolean isPaymentCodePayload(Script script) {
        byte[] data = script.getChunks().get(1).data;

        if(data == null) {
            return false;
        }

        // Payment code is 80 bytes long
        if(data.length != 80) {
            return false;
        }

        // byte 0: version, must be 1
        // byte 1: features bit field, can currently be 0 or 1 (bitmessage notification)
        // byte 2: compressed public key prefix
        if(data[0] == 1 && (data[1] == 0 || data[1] == 1) && (data[2] == 2 || data[2] == 3) ) {
            ECKey key = ECKey.fromPublicOnly(Arrays.copyOfRange(data, 2, 35));
            return true;
        }
        return false;
    }
}
