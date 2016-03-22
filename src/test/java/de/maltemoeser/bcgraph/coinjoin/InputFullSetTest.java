package de.maltemoeser.bcgraph.coinjoin;


import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InputFullSetTest extends Neo4jTest {

    InputFullSet inputFullSet;
    InputSubSet subSet1;
    InputSubSet subSet2;

    @Before
    public void createInputsAndSubSets() {
        try (org.neo4j.graphdb.Transaction transaction = graphDatabaseService.beginTx()) {
            inputFullSet = new InputFullSet();

            BCOutput input1 = getNewOutput(0, 10);
            BCOutput input2 = getNewOutput(0, 20);
            BCOutput input3 = getNewOutput(0, 50);

            subSet1 = new InputSubSet();
            subSet1.addInput(input1);
            subSet1.addInput(input2);

            subSet2 = new InputSubSet();
            subSet2.addInput(input2);
            subSet2.addInput(input3);

            transaction.success();
        }
    }

    @Test
    public void testCRUD() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            assertEquals(0, inputFullSet.size());

            inputFullSet.addSubSet(subSet1);
            assertTrue(inputFullSet.getSubSets().contains(subSet1));
            assertEquals(1, inputFullSet.size());

            inputFullSet.addSubSet(subSet2);
            assertTrue(inputFullSet.getSubSets().contains(subSet2));
            assertEquals(2, inputFullSet.size());

            inputFullSet.removeSubSet(subSet1);
            assertFalse(inputFullSet.getSubSets().contains(subSet1));
            assertEquals(1, inputFullSet.size());
        }
    }

    @Test
    public void testDetectDuplicateInputs() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            inputFullSet.addSubSet(subSet1);
            assertEquals(2, inputFullSet.getNumberOfInputs());
            assertFalse(inputFullSet.containsDuplicateInputs());

            inputFullSet.addSubSet(subSet2);
            assertEquals(4, inputFullSet.getNumberOfInputs());
            assertTrue(inputFullSet.containsDuplicateInputs());
        }
    }

    @Test
    public void testGetSubSetValues() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            // only the first subset
            inputFullSet.addSubSet(subSet1);
            assertEquals(1, inputFullSet.getInputSubSetValues().size());
            long firstInputSubSetValue = inputFullSet.getInputSubSetValues().get(0);
            assertEquals(30L, firstInputSubSetValue);

            // now with the seconds subset
            inputFullSet.addSubSet(subSet2);
            assertEquals(2, inputFullSet.getInputSubSetValues().size());
            firstInputSubSetValue = inputFullSet.getInputSubSetValues().get(0);
            long secondInputSubSetValue = inputFullSet.getInputSubSetValues().get(1);
            assertEquals(100L, firstInputSubSetValue + secondInputSubSetValue);
        }
    }

    @Test
    public void testCopyingOfInputFullSet() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            inputFullSet.addSubSet(subSet1);
            inputFullSet.addSubSet(subSet2);

            InputFullSet copy = inputFullSet.getCopy();
            assertEquals(inputFullSet, copy);

            InputSubSet subSet3 = new InputSubSet();
            subSet3.addInput(getNewOutput());
            inputFullSet.addSubSet(subSet3);

            assertNotEquals(inputFullSet, copy);
            assertEquals(2, copy.size());
            assertEquals(3, inputFullSet.size());
        }
    }
}
