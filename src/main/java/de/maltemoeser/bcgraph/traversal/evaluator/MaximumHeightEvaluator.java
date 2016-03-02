package de.maltemoeser.bcgraph.traversal.evaluator;

import de.maltemoeser.bcgraph.entities.BCTransaction;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * An evaluator rule that stops once the path's end node exceeds a certain block height.
 */
public class MaximumHeightEvaluator extends TransactionEvaluator implements Evaluator {

    protected int height;

    public MaximumHeightEvaluator(int height) {
        this.height = height;
    }

    @Override
    public Evaluation doEvaluate(Path path) {
        if (transaction.getHeight() > height) {
            return Evaluation.EXCLUDE_AND_PRUNE;
        } else {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }
    }
}
