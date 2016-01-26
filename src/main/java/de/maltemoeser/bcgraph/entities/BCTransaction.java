package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.constants.RelType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

public class BCTransaction extends BCEntity {

    public BCTransaction(Node node) {
        super(node);
    }

    public String getHash() {
        return (String) underlyingNode.getProperty(NodeProperty.TRANSACTION_HASH);
    }

    public void setHash(String hash) {
        underlyingNode.setProperty(NodeProperty.TRANSACTION_HASH, hash);
    }

    public long getValue() {
        return (long) underlyingNode.getProperty(NodeProperty.TRANSACTION_VALUE);
    }

    public void setValue(long value) {
        underlyingNode.setProperty(NodeProperty.TRANSACTION_VALUE, value);
    }

    public long getFee() {
        return (long) underlyingNode.getProperty(NodeProperty.TRANSACTION_FEE);
    }

    public void setFee(long fee) {
        underlyingNode.setProperty(NodeProperty.TRANSACTION_FEE, fee);
    }

    public long getLockTime() {
        return (long) underlyingNode.getProperty(NodeProperty.TRANSACTION_LOCKTIME);
    }

    public void setLockTime(long lockTime) {
        underlyingNode.setProperty(NodeProperty.TRANSACTION_LOCKTIME, lockTime);
    }

    public boolean isCoinbase() {
        return underlyingNode.hasLabel(LabelType.Coinbase);
    }

    public void setCoinbase(boolean isCoinbase) {
        if (isCoinbase) {
            underlyingNode.addLabel(LabelType.Coinbase);
        } else {
            underlyingNode.removeLabel(LabelType.Coinbase);
        }
    }

    public BCBlock getBlock() {
        return new BCBlock(underlyingNode.getSingleRelationship(RelType.IN_BLOCK, Direction.OUTGOING).getEndNode());
    }

    public int getHeight() {
        return getBlock().getHeight();
    }

    public Collection<BCOutput> getOutputs() {
        Collection<BCOutput> outputs = new ArrayList<>();
        for (Relationship rel : underlyingNode.getRelationships(RelType.TX_OUTPUT)) {
            outputs.add(new BCOutput(rel.getEndNode()));
        }
        return outputs;
    }

    /**
     * Returns a list of unspent transaction outputs.
     * @return a collection of unspent outputs
     */
    public Collection<BCOutput> getUnspentOutputs() {
        Collection<BCOutput> outputs = getOutputs();
        outputs.removeIf(BCOutput::isSpent);
        return outputs;
    }

    /**
     * Returns a list of outputs that are unspent at a certain height, i.e. have been spent in a block with a higher height.
     * @param height the block height until which an output must not been spent
     * @return a collection of unspent outputs
     */
    public Collection<BCOutput> getUnspentOutputs(int height) {
        Collection<BCOutput> outputs = getOutputs();
        outputs.removeIf(o -> (o.isSpent() && o.getRedeemingTransaction().getHeight() <= height));
        return outputs;
    }

    public Collection<BCOutput> getInputs() {
        Collection<BCOutput> inputs = new ArrayList<>();
        for (Relationship rel : underlyingNode.getRelationships(RelType.TX_INPUT)) {
            inputs.add(new BCOutput(rel.getEndNode()));
        }
        return inputs;
    }

    public int getNumberOfInputs() {
        return underlyingNode.getDegree(RelType.TX_INPUT);
    }

    public int getNumberOfOutputs() {
        return underlyingNode.getDegree(RelType.TX_OUTPUT);
    }

    // Connects the transaction to an output that is used as an input.
    public void addInput(BCOutput input) {
        underlyingNode.createRelationshipTo(input.getUnderlyingNode(), RelType.TX_INPUT);
    }

    public void addOutput(BCOutput output) {
        underlyingNode.createRelationshipTo(output.getUnderlyingNode(), RelType.TX_OUTPUT);
    }

    public void connectToPreviousTransactions(Iterable<BCTransaction> transactions) {
        for (BCTransaction transaction : transactions) {
            connectToPreviousTransaction(transaction);
        }
    }

    public void connectToPreviousTransaction(BCTransaction previousTransaction) {
        underlyingNode.createRelationshipTo(previousTransaction.getUnderlyingNode(), RelType.PREV_TX);
    }

    public Collection<BCTransaction> getPreviousTransactions() {
        Collection<BCTransaction> col = new ArrayList<>();
        for (Relationship rel : underlyingNode.getRelationships(RelType.PREV_TX)) {
            col.add(new BCTransaction(rel.getEndNode()));
        }
        return col;
    }

    public BCOutput getOutputByIndex(int index) {
        if (index < 0 || index > getNumberOfOutputs() - 1) {
            throw new IndexOutOfBoundsException();
        }

        Iterable<Relationship> relationshipsToTransactionOutputs = underlyingNode.getRelationships(RelType.TX_OUTPUT, Direction.OUTGOING);

        for (Relationship rel : relationshipsToTransactionOutputs) {
            Node endNode = rel.getEndNode();
            int outputIndex = (int) endNode.getProperty(NodeProperty.OUTPUT_INDEX);
            if (index == outputIndex) {
                return new BCOutput(endNode);
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BCTransaction && underlyingNode.equals(((BCTransaction) o).getUnderlyingNode());
    }

    @Override
    public String toString() {
        return "Transaction[" + getHash() + "]";
    }
}
