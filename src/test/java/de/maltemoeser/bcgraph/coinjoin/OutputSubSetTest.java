package de.maltemoeser.bcgraph.coinjoin;

import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OutputSubSetTest extends Neo4jTest {

    @Test
    public void testOutputSubSet() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput output = getNewOutput(0, 20);
            OutputSubSet outputSubSet = new OutputSubSet(output, 11);

            assertEquals(31, outputSubSet.getValue());

            outputSubSet = new OutputSubSet(null, 12);
            assertEquals(12, outputSubSet.getValue());
        }
    }
}
