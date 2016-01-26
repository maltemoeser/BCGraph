package de.maltemoeser.bcgraph.importer;

import com.google.inject.Inject;
import de.maltemoeser.bcgraph.constants.OutputType;
import de.maltemoeser.bcgraph.database.Database;
import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.entities.BCOutputService;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.entities.BCTransactionService;
import de.maltemoeser.bcgraph.utils.ScriptUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.neo4j.graphdb.GraphDatabaseService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class TransactionImporter {

    GraphDatabaseService graphDatabaseService;

    private BCTransactionService transactionService;
    private BCOutputService outputService;

    private Transaction bitcoinTransaction;

    private BCTransaction transaction;
    private boolean isCoinbase = false;

    private Collection<BCOutput> bcInputs = new ArrayList<>();
    private Collection<BCOutput> bcOutputs = new ArrayList<>();

    @Inject
    public TransactionImporter(Database database) {
        this.graphDatabaseService = database.getGraphDatabaseService();
    }

    @Inject
    public void setTransactionService(BCTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Inject
    public void setOutputService(BCOutputService outputService) {
        this.outputService = outputService;
    }

    public BCTransaction importTransaction(Transaction bitcoinTransaction) {
        this.bitcoinTransaction = bitcoinTransaction;
        isCoinbase = this.bitcoinTransaction.isCoinBase();

        createTransactionNode();
        importInputs();
        importOutputs();
        calculateFeeAndValue();

        return this.transaction;
    }

    // For Testing
    protected void setBitcoinTransaction(Transaction transaction) {
        bitcoinTransaction = transaction;
        isCoinbase = bitcoinTransaction.isCoinBase();
    }

    protected void createTransactionNode() {
        transaction = transactionService.createTransaction(bitcoinTransaction.getHashAsString());
        transaction.setLockTime(bitcoinTransaction.getLockTime());
        transaction.setCoinbase(isCoinbase);
    }

    protected void importInputs() {
        if (!isCoinbase) {
            doParseInputs();
        }
    }

    /**
     * Connect an input by looking up the correct output, increase the input value and importTransaction P2SH scripts.
     */
    private void doParseInputs() {
        Collection<BCTransaction> previousTransactions = new HashSet<>();

        for (TransactionInput input : bitcoinTransaction.getInputs()) {
            TransactionOutPoint outPoint = input.getOutpoint();
            BCTransaction previousTransaction = transactionService.getTransactionByTransactionOutPoint(outPoint);

            int outputIndex = (int) outPoint.getIndex();
            BCOutput myOutput = previousTransaction.getOutputByIndex(outputIndex);
            bcInputs.add(myOutput);

            // connect transaction to previous output / new input
            transaction.addInput(myOutput);
            previousTransactions.add(previousTransaction);

            if (myOutput.isPayToScriptHash()) {
                updateP2SHInput(input, myOutput);
            }
        }
        transaction.connectToPreviousTransactions(previousTransactions);
    }

    /**
     * Create outputs and importTransaction their script.
     */
    protected void importOutputs() {
        for (TransactionOutput output : bitcoinTransaction.getOutputs()) {
            BCOutput bcOutput = createOutputNode(output);
            try {
                parseOutputScript(bcOutput, output.getScriptPubKey());
            } catch (ScriptException e) {
                e.printStackTrace();
            }

            bcOutputs.add(bcOutput);
            transaction.addOutput(bcOutput);
        }
    }

    /**
     * Creates an output node with the following parameters
     * - index of the output
     * - value of the output (in Satoshi)
     *
     * @param transactionOutput the transaction output
     * @return the node representing the output
     */
    protected BCOutput createOutputNode(TransactionOutput transactionOutput) {
        return outputService.createOutput(
                transactionOutput.getIndex(),
                transactionOutput.getValue().longValue()
        );
    }

    /**
     * Attempts to importTransaction the redeem conditions provided in the P2SH input.
     *
     * @param input    the P2SH input
     * @param bcOutput the output that is to be updated.
     */
    private void updateP2SHInput(TransactionInput input, BCOutput bcOutput) {
        Script scriptPubKey = ScriptUtils.getScriptFromP2SHInput(input);
        parseOutputScript(bcOutput, scriptPubKey);
    }

    protected void parseOutputScript(BCOutput bcOutput, Script script) {
        OutputType outputType = ScriptParser.getOutputTypeFromScript(script);
        ScriptParser.setOutputType(bcOutput, outputType, script);
    }


    public void calculateFeeAndValue() {
        calculateFeeAndValue(bcInputs, bcOutputs);
    }

    protected void calculateFeeAndValue(Collection<BCOutput> inputs, Collection<BCOutput> outputs) {
        long fee;
        long sumOfOutputs = getCumulativeValueOfOutputs(outputs);

        if (isCoinbase || inputs.size() == 0) {
            fee = 0;
        } else {
            long sumOfInputs = getCumulativeValueOfOutputs(inputs);
            fee = sumOfInputs - sumOfOutputs;
        }
        transaction.setFee(fee);
        transaction.setValue(sumOfOutputs);
    }

    public static long getCumulativeValueOfOutputs(Collection<BCOutput> outputs) {
        return outputs.stream().mapToLong(BCOutput::getValue).sum();
    }

    protected BCTransaction getBCTransaction() {
        return transaction;
    }
}
