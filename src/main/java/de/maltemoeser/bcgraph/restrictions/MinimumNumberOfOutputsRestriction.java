package de.maltemoeser.bcgraph.restrictions;

import de.maltemoeser.bcgraph.entities.BCTransaction;


public class MinimumNumberOfOutputsRestriction implements Restriction<BCTransaction> {

    int minimumNumberOfOutputs;

    public MinimumNumberOfOutputsRestriction(int minimumNumberOfOutputs) {
        this.minimumNumberOfOutputs = minimumNumberOfOutputs;
    }

    @Override
    public boolean evaluate(BCTransaction transaction) {
        return transaction.getNumberOfOutputs() >= minimumNumberOfOutputs;
    }

}
