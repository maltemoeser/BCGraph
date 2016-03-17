package de.maltemoeser.bcgraph.restrictions;

import de.maltemoeser.bcgraph.entities.BCTransaction;


public class MaximumNumberOfOutputsRestriction implements Restriction<BCTransaction> {

    int maximumNumberOfOutputs;

    public MaximumNumberOfOutputsRestriction(int maximumNumberOfOutputs) {
        this.maximumNumberOfOutputs = maximumNumberOfOutputs;
    }

    @Override
    public boolean evaluate(BCTransaction transaction) {
        return transaction.getNumberOfOutputs() <= maximumNumberOfOutputs;
    }

}
