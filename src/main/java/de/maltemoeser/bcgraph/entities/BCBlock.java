package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.constants.RelType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class BCBlock extends BCEntity {

    public BCBlock(Node node) {
        super(node);
    }

    /**
     * Every block is identified by a 64-character hash.
     *
     * @return the block hash
     */
    public String getHash() {
        return (String) underlyingNode.getProperty(NodeProperty.BLOCK_HASH);
    }

    public void setHash(String hash) {
        underlyingNode.setProperty(NodeProperty.BLOCK_HASH, hash);
    }

    /**
     * The height represents the position of the block in the blockchain.
     */
    public int getHeight() {
        return (Integer) underlyingNode.getProperty(NodeProperty.BLOCK_HEIGHT);
    }

    public void setHeight(int height) {
        underlyingNode.setProperty(NodeProperty.BLOCK_HEIGHT, height);
    }

    public long getUnixTime() {
        return (Long) underlyingNode.getProperty(NodeProperty.BLOCK_UNIXTIME);
    }

    public void setUnixTime(long unixTime) {
        underlyingNode.setProperty(NodeProperty.BLOCK_UNIXTIME, unixTime);
    }

    /**
     * Adds a transaction to the block.
     *
     * @param transaction
     */
    public void addTransaction(BCTransaction transaction) {
        transaction.getUnderlyingNode().createRelationshipTo(underlyingNode, RelType.IN_BLOCK);
    }

    public Collection<BCTransaction> getTransactions() {
        Collection<BCTransaction> transactions = new ArrayList<>();
        Iterable<Relationship> relationships = underlyingNode.getRelationships(RelType.IN_BLOCK, Direction.INCOMING);
        for(Relationship relationship : relationships) {
            transactions.add(new BCTransaction(relationship.getOtherNode(underlyingNode)));
        }
        return transactions;
    }

    /**
     * Each block bundles a set of valid transactions.
     *
     * @return the number of transactions in the block
     */
    public int getNumberOfTransactions() {
        return underlyingNode.getDegree(RelType.IN_BLOCK);
    }

    /**
     * Blocks form a chain by linking to their predecessor.
     *
     * @param predecessor the previous block
     */
    public void connectToPreviousBlock(BCBlock predecessor) {
        Node preNode = predecessor.getUnderlyingNode();
        underlyingNode.createRelationshipTo(preNode, RelType.PREV_BLOCK);
    }

    /**
     * By retrieving the previous block we can traverse through the block chain.
     * If the current block is the genesis block, a NullPointerException will be thrown.
     *
     * @return the previous block (at current height - 1)
     */
    public BCBlock getPreviousBlock() throws NullPointerException {
        Node node = underlyingNode.getSingleRelationship(RelType.PREV_BLOCK, Direction.OUTGOING).getEndNode();
        return new BCBlock(node);
    }

    /**
     * By retrieving the next block we can traverse through the block chain.
     * If the current block is the leader, a NullPointerException will be thrown.
     *
     * @return the next block (at current height + 1)
     */
    public BCBlock getNextBlock() throws NullPointerException {
        Node node = underlyingNode.getSingleRelationship(RelType.PREV_BLOCK, Direction.INCOMING).getStartNode();
        return new BCBlock(node);
    }

    /**
     * The latest block is the last block inserted.
     */
    public boolean isLatestBlock() {
        return underlyingNode.hasLabel(LabelType.LatestBlock);
    }

    public void setLatestBlock(boolean isLatest) {
        if (isLatest) {
            underlyingNode.addLabel(LabelType.LatestBlock);
        } else {
            underlyingNode.removeLabel(LabelType.LatestBlock);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BCBlock && underlyingNode.equals(((BCBlock) o).getUnderlyingNode());
    }

    @Override
    public String toString() {
        return "Block[" + getHash() + "]";
    }
}
