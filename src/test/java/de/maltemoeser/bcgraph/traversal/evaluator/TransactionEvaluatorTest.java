package de.maltemoeser.bcgraph.traversal.evaluator;

import de.maltemoeser.bcgraph.constants.RelType;
import de.maltemoeser.bcgraph.entities.BCBlock;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.testing.BlockTransactionPair;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import de.maltemoeser.bcgraph.testing.TestGraphUtils;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TransactionEvaluatorTest extends Neo4jTest {

    TestGraphUtils testGraphUtils;
    BCBlock secondBlock;
    BCTransaction firstTransaction;
    BCTransaction lastTransaction;
    BCTransaction thirdTransaction;

    @Before
    public void setUpGraph() {
        testGraphUtils = injector.getInstance(TestGraphUtils.class);
        try (org.neo4j.graphdb.Transaction tx = graphDatabaseService.beginTx()) {
            BlockTransactionPair block0 = testGraphUtils.createTransactionAndBlockAtHeight(0);
            BlockTransactionPair block1 = testGraphUtils.createTransactionAndBlockAtHeight(1);
            BlockTransactionPair block2 = testGraphUtils.createTransactionAndBlockAtHeight(2);
            BlockTransactionPair block3 = testGraphUtils.createTransactionAndBlockAtHeight(3);
            BlockTransactionPair block4 = testGraphUtils.createTransactionAndBlockAtHeight(4);

            block1.getBlock().connectToPreviousBlock(block0.getBlock());
            block2.getBlock().connectToPreviousBlock(block1.getBlock());
            block3.getBlock().connectToPreviousBlock(block2.getBlock());
            block4.getBlock().connectToPreviousBlock(block3.getBlock());

            block1.getTransaction().connectToPreviousTransaction(block0.getTransaction());
            block2.getTransaction().connectToPreviousTransaction(block1.getTransaction());
            block3.getTransaction().connectToPreviousTransaction(block2.getTransaction());
            block4.getTransaction().connectToPreviousTransaction(block3.getTransaction());

            secondBlock = block2.getBlock();
            firstTransaction = block0.getTransaction();
            lastTransaction = block4.getTransaction();
            thirdTransaction = block2.getTransaction();

            tx.success();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectNodeType() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            Evaluator minimumHeightEvaluator = new MinimumHeightEvaluator(1);

            Traverser paths = graphDatabaseService.traversalDescription()
                    .relationships(RelType.PREV_BLOCK, Direction.BOTH)
                    .evaluator(minimumHeightEvaluator)
                    .traverse(secondBlock.getUnderlyingNode());

            for(Node node : paths.nodes()) {
                System.out.println(node);
            }
        }
    }

    @Test
    public void testMinimumHeightEvaluator() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            Evaluator minimumHeightEvaluator = new MinimumHeightEvaluator(1);

            Traverser paths = graphDatabaseService.traversalDescription()
                    .relationships(RelType.PREV_TX, Direction.BOTH)
                    .evaluator(minimumHeightEvaluator)
                    .traverse(thirdTransaction.getUnderlyingNode());

            int numberOfNodes = 0;
            for (Node node : paths.nodes()) {
                assertTrue(new BCTransaction(node).getHeight() >= 1);
                numberOfNodes++;
            }
            assertEquals(4, numberOfNodes);
        }
    }

    @Test
    public void testMaximumHeightEvaluator() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            Evaluator maximumHeightEvaluator = new MaximumHeightEvaluator(3);

            Traverser paths = graphDatabaseService.traversalDescription()
                    .relationships(RelType.PREV_TX, Direction.BOTH)
                    .evaluator(maximumHeightEvaluator)
                    .traverse(thirdTransaction.getUnderlyingNode());

            int numberOfNodes = 0;
            for (Node node : paths.nodes()) {
                assertTrue(new BCTransaction(node).getHeight() <= 3);
                numberOfNodes++;
            }
            assertEquals(4, numberOfNodes);
        }
    }

    @Test
    public void testMinimumAndMaximumHeightEvaluator() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            Evaluator maximumHeightEvaluator = new MaximumHeightEvaluator(3);
            Evaluator minimumHeightEvaluator = new MinimumHeightEvaluator(1);

            Traverser paths = graphDatabaseService.traversalDescription()
                    .relationships(RelType.PREV_TX, Direction.BOTH)
                    .evaluator(maximumHeightEvaluator)
                    .evaluator(minimumHeightEvaluator)
                    .traverse(thirdTransaction.getUnderlyingNode());

            int numberOfNodes = 0;
            for (Node node : paths.nodes()) {
                assertTrue(new BCTransaction(node).getHeight() >= 1);
                assertTrue(new BCTransaction(node).getHeight() <= 3);
                numberOfNodes++;
            }
            assertEquals(3, numberOfNodes);
        }
    }
}
