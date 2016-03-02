package de.maltemoeser.bcgraph.traversal.evaluator;

import de.maltemoeser.bcgraph.entities.BCTransaction;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;


public abstract class TransactionEvaluator {

    BCTransaction transaction;

    public Evaluation evaluate(Path path) {
        transaction = new BCTransaction(path.endNode());

        if (!transaction.isTransaction()) {
            throw new IllegalArgumentException("A TransactionEvaluator can only be used with transaction nodes.");
        }

        return doEvaluate(path);
    }

    public abstract Evaluation doEvaluate(Path path);
}
