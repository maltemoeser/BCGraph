package de.maltemoeser.bcgraph.coinjoin;

import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;

import static org.junit.Assert.*;


public class InputSubSetTest extends Neo4jTest {

    @Test
    public void testCRUD() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput input1 = getNewOutput(0, 10);
            BCOutput input2 = getNewOutput(0, 20);

            InputSubSet inputSubSet = new InputSubSet();
            assertTrue(inputSubSet.addInput(input1));
            assertTrue(inputSubSet.addInput(input2));
            assertFalse(inputSubSet.addInput(input1));

            assertEquals(2, inputSubSet.size());
            assertEquals(30, inputSubSet.getCumulativeValue());

            assertTrue(inputSubSet.removeInput(input1));
            assertEquals(20, inputSubSet.getCumulativeValue());
        }
    }

    @Test
    public void testCopy() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCOutput input1 = getNewOutput(0, 10);
            BCOutput input2 = getNewOutput(0, 20);
            BCOutput input3 = getNewOutput(0, 50);

            InputSubSet inputSubSet = new InputSubSet();
            inputSubSet.addInput(input1);
            inputSubSet.addInput(input2);

            InputSubSet copy = inputSubSet.getCopy();

            assertEquals(inputSubSet, copy);

            inputSubSet.addInput(input3);
            assertNotEquals(inputSubSet, copy);
            assertEquals(3, inputSubSet.size());
            assertEquals(2, copy.size());
        }
    }
}
