package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.constants.RelType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Bitcoin address is a hash of a public key (P2PKH) or a script program (P2SH).
 */
public class BCAddress extends BCEntity {

    public BCAddress(Node node) {
        super(node);
    }

    public void setHash(String hash) {
        underlyingNode.setProperty(NodeProperty.ADDRESS_HASH, hash);
    }

    public String getHash() {
        return (String) underlyingNode.getProperty(NodeProperty.ADDRESS_HASH);
    }

    /**
     * Return a list of all outputs that send to this address.
     */
    public List<BCOutput> getOutputs() {
        List<BCOutput> outputs = new ArrayList<>();
        for(Relationship rel : underlyingNode.getRelationships(Direction.INCOMING, RelType.BELONGS_TO)) {
            outputs.add(new BCOutput(rel.getOtherNode(underlyingNode)));
        }
        return outputs;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BCAddress && underlyingNode.equals(((BCAddress) o).getUnderlyingNode());
    }

    @Override
    public String toString() {
        return "Address[" + getHash() + "]";
    }
}
