package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import static org.junit.Assert.assertEquals;

public class BCAddressTest extends Neo4jTest {

    final static String addressHash = "1HACFAkiijAefHBr1CAkjjaVHWLPqbVuvq";

    @Test
    public void testBCAddressGetter() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            Node node = graphDatabaseService.createNode(LabelType.Address);
            node.setProperty(NodeProperty.ADDRESS_HASH, addressHash);
            BCAddress address = new BCAddress(node);
            assertEquals(addressHash, address.getHash());
        }
    }

    @Test
    public void testBCAddressSetter() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCAddress address = getNewAddress();
            address.setHash(addressHash);
            assertEquals(addressHash, address.getUnderlyingNode().getProperty(NodeProperty.ADDRESS_HASH));
        }
    }

}
