package de.maltemoeser.bcgraph.traversal.evaluator;

import de.maltemoeser.bcgraph.entities.BCTransaction;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * An evaluator rule that stops once the path's end node exceeds a certain block height.
 */
public class MaximumHeightEvaluator implements Evaluator {

    protected int height;

    public MaximumHeightEvaluator(int height) {
        this.height = height;
    }

    @Override
    public Evaluation evaluate(Path path) {
        BCTransaction transaction = new BCTransaction(path.endNode());

        if (!transaction.isTransaction()) {
            throw new UnsupportedOperationException("MaximumHeightEvaluator can only be used on transaction nodes.");
        }

        if (transaction.getHeight() > height) {
            return Evaluation.EXCLUDE_AND_PRUNE;
        } else {
            return Evaluation.INCLUDE_AND_CONTINUE;
        }
    }
}
