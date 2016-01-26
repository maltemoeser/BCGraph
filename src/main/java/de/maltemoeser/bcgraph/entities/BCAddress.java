package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.NodeProperty;
import org.neo4j.graphdb.Node;

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

    @Override
    public boolean equals(Object o) {
        return o instanceof BCAddress && underlyingNode.equals(((BCAddress) o).getUnderlyingNode());
    }

    @Override
    public String toString() {
        return "Address[" + getHash() + "]";
    }
}
