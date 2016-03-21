package de.maltemoeser.bcgraph.testing;

import org.apache.commons.io.FileUtils;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.params.MainNetParams;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public class TestUtils {

    public static final int TEST_BLOCK_HEIGHT = 350010;
    public static final String TEST_BLOCK_HASH = "00000000000000000ec32843883a983fe86f8823b87480520e88261784eea941";
    public static final int TEST_BLOCK_NO_TX = 2149;

    public static final String COINBASE_HASH = "2556a4729a22b9db53ef78ccf74d51599c0c17565dd11445df3a4b474e3b9d15";
    public static final String SECOND_TRANSACTION_HASH = "e01c017bcf2fb6703e9b74a2e99ae2da0598d438c27e7ee6733b2bab37bc4993";

    /** Test data from serialized block **/

    public static Block getTestBlock() {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        File file = new File(classLoader.getResource("block_350010.dat").getFile());
        byte[] blockBytes = null;
        try {
            blockBytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Block(MainNetParams.get(), blockBytes);
    }

    public static Transaction getP2SHInputTransaction() {
        return getTransactionByHash("6ec634c025424ef192078adb39185f0732ace816f8f02e702e4c4efc6fc26c9b");
    }

    public static Transaction getP2SHOutputTransaction() {
        return getTransactionByHash("c8c241121d41dff8ba82231e3f13a8aa15a4caff1cf56f75dc8c851e6fad23d6");
    }

    public static Transaction getMultiSigOutputTransaction() {
        return getTransactionByHash("5a69546390a36c0448ab6b5692f77f32c69aab43836cbdd85d8811e37f49539b");
    }

    public static Transaction getOpReturnTransaction() {
        return getTransactionByHash("9c6ad82f63c2e3a23be46c5a128794fdd73ce52d010a31c5fb56dc60ed22c0f1");
    }

    public static Transaction getP2PKHTransaction() {
        return getTransactionByHash("41c6aa2787f66574f9400bd1d69cafb9fa8a0919a85e40a22b23c17da9526556");
    }

    public static Transaction getCoinbaseTransaction() {
        return getTransactionByHash("2556a4729a22b9db53ef78ccf74d51599c0c17565dd11445df3a4b474e3b9d15");
    }

    public static TransactionOutput getStandardTransactionOutput() {
        return getP2PKHTransaction().getOutput(0);
    }

    public static TransactionOutput getP2SHTransactionOutput() {
        return getP2SHOutputTransaction().getOutput(1);
    }

    public static TransactionOutput getOpReturnTransactionOutput() {
        return getOpReturnTransaction().getOutput(0);
    }

    public static TransactionOutput getMultisigTransactionOutput() {
        return getMultiSigOutputTransaction().getOutput(0);
    }

    private static Transaction getTransactionByHash(String hash){
        Block block = getTestBlock();
        for(Transaction tx : block.getTransactions()){
            if(tx.getHashAsString().equals(hash)){
                return tx;
            }
        }
        throw new NoSuchElementException();
    }

    /** Other data **/

    public static final String STEALTH_TRANSACTION = "9001cbd710b1e51c2f28e5b155b87c9c6856c87b15ba0737e3963ebad2d6fa3e";

    public static Transaction getStealthTransaction() {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        File file = new File(classLoader.getResource("stealth_tx.dat").getFile());
        byte[] txBytes = null;
        try {
            txBytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Transaction(MainNetParams.get(), txBytes);
    }
}
