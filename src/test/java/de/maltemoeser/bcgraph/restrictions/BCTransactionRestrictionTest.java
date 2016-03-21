package de.maltemoeser.bcgraph.restrictions;

import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.testing.Neo4jTest;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BCTransactionRestrictionTest extends Neo4jTest {

    @Test
    public void testMinInputCountRestriction() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            transaction.addInput(getNewOutput());

            Restrictor<BCTransaction> restrictor = new EntityRestrictor<BCTransaction>()
                    .restrict(new MinimumNumberOfInputsRestriction(2));
            assertFalse(restrictor.evaluate(transaction));

            transaction.addInput(getNewOutput());
            assertTrue(restrictor.evaluate(transaction));
        }
    }

    @Test
    public void testMaxInputCountRestriction() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            transaction.addInput(getNewOutput());

            Restrictor<BCTransaction> restrictor = new EntityRestrictor<BCTransaction>()
                    .restrict(new MaximumNumberOfInputsRestriction(1));
            assertTrue(restrictor.evaluate(transaction));

            transaction.addInput(getNewOutput());
            assertFalse(restrictor.evaluate(transaction));
        }
    }

    @Test
    public void testMinOutputCountRestriction() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            transaction.addOutput(getNewOutput());

            Restrictor<BCTransaction> restrictor = new EntityRestrictor<BCTransaction>()
                    .restrict(new MinimumNumberOfOutputsRestriction(2));
            assertFalse(restrictor.evaluate(transaction));

            transaction.addOutput(getNewOutput());
            assertTrue(restrictor.evaluate(transaction));
        }
    }

    @Test
    public void testMaxOutputCountRestriction() {
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            BCTransaction transaction = getNewTransaction();
            transaction.addOutput(getNewOutput());

            Restrictor<BCTransaction> restrictor = new EntityRestrictor<BCTransaction>()
                    .restrict(new MaximumNumberOfOutputsRestriction(1));
            assertTrue(restrictor.evaluate(transaction));

            transaction.addOutput(getNewOutput());
            assertFalse(restrictor.evaluate(transaction));
        }
    }

}
