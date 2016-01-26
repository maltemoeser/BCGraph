package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.RelType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

/**
 * BCEntity is the superclass of all wrapper classes that provide access to the nodes of the underlying graph constants.
 */
public abstract class BCEntity {

    final Node underlyingNode;

    BCEntity(Node node) {
        this.underlyingNode = node;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    /**
     * The internal ID of the Neo4j database should not be used as a unique identifier.
     *
     * @return the constants-internal node ID.
     */
    public long getInternalId() {
        return underlyingNode.getId();
    }

    public boolean isBlock() {
        return checkNodeForLabel(LabelType.Block);
    }

    public boolean isTransaction() {
        return checkNodeForLabel(LabelType.Transaction);
    }

    public boolean isAddress() {
        return checkNodeForLabel(LabelType.Address);
    }

    private boolean checkNodeForLabel(LabelType labelType) {
        return underlyingNode.hasLabel(labelType);
    }

    /**
     * As outputs do not have labels (they are uniquely identifiable by the tx hash and the index),
     * we instead look for the presence of the correct transaction type that creates them.
     */
    public boolean isOutput() {
        return underlyingNode.hasRelationship(Direction.INCOMING, RelType.TX_OUTPUT);
    }

    @Override
    public int hashCode() {
        return underlyingNode.hashCode();
    }
}
