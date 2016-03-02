package de.maltemoeser.bcgraph.traversal.evaluator;

import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * An evaluator rule that stops once the path's end node falls below a certain block height.
 */
public class MinimumHeightEvaluator extends TransactionEvaluator implements Evaluator {

    protected int height;

    public MinimumHeightEvaluator(int height) {
        this.height = height;
    }

    @Override
    public Evaluation doEvaluate(Path path) {
        if (transaction.getHeight() < height) {
            return Evaluation.EXCLUDE_AND_PRUNE;
        } else {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }
    }
}
