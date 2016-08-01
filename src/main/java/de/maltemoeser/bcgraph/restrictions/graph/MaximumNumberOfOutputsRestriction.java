package de.maltemoeser.bcgraph.restrictions.graph;

import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.restrictions.Restriction;


public class MaximumNumberOfOutputsRestriction implements Restriction<BCTransaction> {

    private int maximumNumberOfOutputs;

    public MaximumNumberOfOutputsRestriction(int maximumNumberOfOutputs) {
        this.maximumNumberOfOutputs = maximumNumberOfOutputs;
    }

    @Override
    public boolean evaluate(BCTransaction transaction) {
        return transaction.getNumberOfOutputs() <= maximumNumberOfOutputs;
    }

}
