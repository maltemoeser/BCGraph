package de.maltemoeser.bcgraph.entities;

import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.constants.NodeProperty;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;
import org.neo4j.graphdb.Node;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testGetOutputs() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCAddress address = getNewAddress();
            assertEquals(0, address.getOutputs().size());

            BCOutput output1 = getNewOutput();
            output1.connectToAddress(address);
            assertEquals(1, address.getOutputs().size());

            BCOutput output2 = getNewOutput();
            output2.connectToAddress(address);
            assertEquals(2, address.getOutputs().size());

            Collection<BCOutput> outputs = address.getOutputs();
            assertTrue(outputs.contains(output1));
            assertTrue(outputs.contains(output2));
        }
    }
}
