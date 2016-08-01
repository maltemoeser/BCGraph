package de.maltemoeser.bcgraph.restrictions.graph;

import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.restrictions.Restriction;


public class MinimumNumberOfOutputsRestriction implements Restriction<BCTransaction> {

    private int minimumNumberOfOutputs;

    public MinimumNumberOfOutputsRestriction(int minimumNumberOfOutputs) {
        this.minimumNumberOfOutputs = minimumNumberOfOutputs;
    }

    @Override
    public boolean evaluate(BCTransaction transaction) {
        return transaction.getNumberOfOutputs() >= minimumNumberOfOutputs;
    }

}
