package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.constants.RelType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.ArrayList;
import java.util.Collection;

public class BCOutput extends BCEntity {

    public BCOutput(Node node) {
        super(node);
    }

    public int getIndex() {
        return (int) underlyingNode.getProperty(NodeProperty.OUTPUT_INDEX);
    }

    public void setIndex(int index) {
        underlyingNode.setProperty(NodeProperty.OUTPUT_INDEX, index);
    }

    public long getValue() {
        return (long) underlyingNode.getProperty(NodeProperty.OUTPUT_VALUE);
    }

    public void setValue(long value) {
        underlyingNode.setProperty(NodeProperty.OUTPUT_VALUE, value);
    }


    // MULTISIG

    public boolean isSentToMultiSig() {
        return underlyingNode.hasLabel(LabelType.MultiSig);
    }

    public void setSentToMultiSig(boolean isMultiSig) {
        if (isMultiSig) {
            underlyingNode.addLabel(LabelType.MultiSig);
        } else {
            underlyingNode.removeLabel(LabelType.MultiSig);
        }
    }

    public void setSentToMultiSig(int m, int n) {
        setSentToMultiSig(true);
        setNumberOfRequiredSignatures(m);
        setNumberOfTotalSignatures(n);
    }

    public int getNumberOfRequiredSignatures() {
        return (int) underlyingNode.getProperty(NodeProperty.OUTPUT_MULTISIG_REQUIRED);
    }

    private void setNumberOfRequiredSignatures(int m) {
        underlyingNode.setProperty(NodeProperty.OUTPUT_MULTISIG_REQUIRED, m);
    }

    public int getNumberOfTotalSignatures() {
        return (int) underlyingNode.getProperty(NodeProperty.OUTPUT_MULTISIG_TOTAL);
    }

    private void setNumberOfTotalSignatures(int n) {
        underlyingNode.setProperty(NodeProperty.OUTPUT_MULTISIG_TOTAL, n);
    }


    // P2SH

    public boolean isPayToScriptHash() {
        return underlyingNode.hasLabel(LabelType.P2SH);
    }

    public void setPayToScriptHash(boolean isP2SH) {
        if (isP2SH) {
            underlyingNode.addLabel(LabelType.P2SH);
        } else {
            underlyingNode.removeLabel(LabelType.P2SH);
        }
    }


    // OP_RETURN

    public boolean isOpReturn() {
        return underlyingNode.hasLabel(LabelType.OP_Return);
    }

    public void setOpReturn(boolean isOpReturn) {
        if (isOpReturn) {
            underlyingNode.addLabel(LabelType.OP_Return);
        } else {
            underlyingNode.removeLabel(LabelType.OP_Return);
        }
    }

    /**
     * An output is spent if it consumed as an input in another transaction.
     */
    public boolean isSpent() {
        return underlyingNode.hasRelationship(RelType.TX_INPUT);
    }

    public BCTransaction getTransaction() {
        return new BCTransaction(underlyingNode.getSingleRelationship(RelType.TX_OUTPUT, Direction.INCOMING).getOtherNode(underlyingNode));
    }

    /**
     * Returns the transaction that spends the output, or null if it hasn't been spent yet.
     */
    public BCTransaction getRedeemingTransaction() {
        Relationship inputRel = underlyingNode.getSingleRelationship(RelType.TX_INPUT, Direction.INCOMING);
        if (inputRel != null) {
            return new BCTransaction(inputRel.getOtherNode(underlyingNode));
        }
        return null;
    }

    /**
     * Adds a connection to the address specified in the output's ScriptPubKey
     */
    public void connectToAddress(BCAddress address) {
        underlyingNode.createRelationshipTo(address.getUnderlyingNode(), RelType.BELONGS_TO);
    }

    public int getNumberOfAddresses() {
        return underlyingNode.getDegree(RelType.BELONGS_TO, Direction.OUTGOING);
    }

    /**
     * In case of multisig outputs, an output may refer to multiple addresses.
     */
    public Collection<BCAddress> getAddresses() {
        Collection<BCAddress> addresses = new ArrayList<>();
        for (Relationship rel : underlyingNode.getRelationships(Direction.OUTGOING, RelType.BELONGS_TO)) {
            addresses.add(new BCAddress(rel.getEndNode()));
        }
        return addresses;
    }

    /**
     * Returns the single address attached to an output, and fails if there are multiple.
     */
    public BCAddress getSingleAddress() {
        return new BCAddress(underlyingNode.getSingleRelationship(RelType.BELONGS_TO, Direction.OUTGOING).getEndNode());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BCOutput &&
                underlyingNode.equals(((BCOutput) o).getUnderlyingNode());
    }

    @Override
    public String toString() {
        return "Output[index: " + getIndex() + ", value: " + getValue() + "]";
    }
}
