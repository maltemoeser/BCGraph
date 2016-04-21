package de.maltemoeser.bcgraph.coinjoin;

import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SubSetSolverTest extends Neo4jTest {

    @Test
    public void testExtractMostFrequentOutputValue() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction tx = getSimpleCoinJoinTransaction();
            SubSetSolver solver = new SubSetSolver(tx);

            solver.extractMostFrequentOutputValue();
            assertEquals(61000000L, solver.getMostFrequentOutputValue());
            assertEquals(3, solver.getNumberOfParticipants());
        }
    }

    @Test
    public void testComputeOutputSubSets() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction tx = getSimpleCoinJoinTransaction();
            SubSetSolver solver = new SubSetSolver(tx);

            solver.extractMostFrequentOutputValue();
            solver.computeOutputSubSets();
            assertEquals(100000000L, solver.getValueOfLargestOutputSubSet());
        }
    }

    @Test
    public void testComputeAllowedFeeVariance() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction tx = getSimpleCoinJoinTransaction();
            SubSetSolver solver = new SubSetSolver(tx);

            solver.extractMostFrequentOutputValue();
            solver.computeOutputSubSets();
            solver.computeAllowedFeeVariance();
            assertEquals(10000, solver.getFeeVariance(), 1);

            assertFalse(solver.hasOutputValuesCloseToEachOther());
        }
    }

    @Test
    public void testSubSetValuesCloseToEachOther() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            transaction.addInput(getNewOutput(0, 1000000));
            transaction.addInput(getNewOutput(0, 1000000));
            transaction.addInput(getNewOutput(0, 1000000));
            transaction.addInput(getNewOutput(0, 1000000));
            transaction.addOutput(getNewOutput(0, 1000000));
            transaction.addOutput(getNewOutput(0, 1000000));
            transaction.addOutput(getNewOutput(0, 990010));
            transaction.addOutput(getNewOutput(0, 990020));
            transaction.setFee(9970);

            SubSetSolver solver = new SubSetSolver(transaction);
            solver.extractMostFrequentOutputValue();
            solver.computeOutputSubSets();
            solver.computeAllowedFeeVariance();
            assertTrue(solver.hasOutputValuesCloseToEachOther());
        }
    }

    @Test
    public void testComputeInputSubSets() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction tx = getSimpleCoinJoinTransaction();
            SubSetSolver solver = new SubSetSolver(tx);

            solver.extractMostFrequentOutputValue();
            solver.computeOutputSubSets();
            solver.computeAllowedFeeVariance();
            solver.computeMaximumSubSetSize();
            solver.computeInputSubSets();

            assertFalse(solver.hasOutputValuesCloseToEachOther());

            // there are only 3 possible input subsets that match output subsets
            assertEquals(3, solver.getInputSubSets().size());
        }
    }

    @Test
    public void testComputeInputFullSets() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction tx = getSimpleCoinJoinTransaction();
            SubSetSolver solver = new SubSetSolver(tx);

            solver.extractMostFrequentOutputValue();
            solver.computeOutputSubSets();
            solver.computeAllowedFeeVariance();
            solver.computeMaximumSubSetSize();
            solver.computeInputSubSets();
            solver.computePossibleInputFullSets();

            // there is only one possible full set
            assertTrue(solver.hasSingleSolution());

            // make sure we aggregated the correct inputs
            testValueOfTwoElementInputSubSet(solver, 100000000);
        }
    }

    @Test
    public void testLargeCoinJoinTransaction() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getLargeCoinJoinTransaction();
            SubSetSolver solver = new SubSetSolver(transaction);

            assertTrue(solver.solve());

            assertEquals(6, solver.getInputSubSets().size());
            assertTrue(solver.hasSingleSolution());

            // make sure we aggregated the correct inputs
            testValueOfTwoElementInputSubSet(solver, 15600000);
        }
    }

    private static void testValueOfTwoElementInputSubSet(SubSetSolver solver, long value) {
        InputFullSet ifs = solver.getInputFullSets().stream().findFirst().get();
        assertEquals(value, ifs.getSubSets().stream().filter(inputSubSet -> inputSubSet.size() == 2).findFirst().get().getCumulativeValue());
    }

    private BCTransaction getSimpleCoinJoinTransaction() {
        BCTransaction transaction = getNewTransaction();
        transaction.addInput(getNewOutput(0, 61010000L));
        transaction.addInput(getNewOutput(0, 50000000L));
        transaction.addInput(getNewOutput(0, 50000000L));
        transaction.addInput(getNewOutput(0, 79200000L));

        transaction.addOutput(getNewOutput(0, 18200000L));
        transaction.addOutput(getNewOutput(1, 61000000L));
        transaction.addOutput(getNewOutput(2, 61000000L));
        transaction.addOutput(getNewOutput(3, 61000000L));
        transaction.addOutput(getNewOutput(4, 39000000L));
        transaction.setFee(10000L);
        return transaction;
    }

    // Another simple CoinJoin transaction
    private BCTransaction getLargeCoinJoinTransaction() {
        BCTransaction transaction = getNewTransaction();
        transaction.addInput(getNewOutput(0, 20000000L));
        transaction.addInput(getNewOutput(0, 38400000L));
        transaction.addInput(getNewOutput(0, 5000000L));
        transaction.addInput(getNewOutput(0, 26000000L));
        transaction.addInput(getNewOutput(0, 33000000L));
        transaction.addInput(getNewOutput(0, 10600000L));
        transaction.addInput(getNewOutput(0, 23100000L));

        transaction.addOutput(getNewOutput(0, 15400000L));
        transaction.addOutput(getNewOutput(1, 10600000L));
        transaction.addOutput(getNewOutput(2, 15400000L));
        transaction.addOutput(getNewOutput(3, 15400000L));
        transaction.addOutput(getNewOutput(4, 15400000L));
        transaction.addOutput(getNewOutput(5, 17600000L));
        transaction.addOutput(getNewOutput(6, 4580000L));
        transaction.addOutput(getNewOutput(7, 15400000L));
        transaction.addOutput(getNewOutput(8, 200000L));
        transaction.addOutput(getNewOutput(9, 15400000L));
        transaction.addOutput(getNewOutput(10, 23000000L));
        transaction.addOutput(getNewOutput(11, 7700000L));

        transaction.setFee(20000L);
        return transaction;
    }

    // Helper method to print results
    public static void printInputFullSets(HashSet<InputFullSet> sets) {
        for (InputFullSet ifs : sets) {
            for (InputSubSet iss : ifs.getSubSets()) {
                System.out.println(iss.getInputs());
            }
            System.out.println();
        }
    }
}
