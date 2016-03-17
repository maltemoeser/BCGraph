package de.maltemoeser.bcgraph.restrictions;

import de.maltemoeser.bcgraph.entities.BCTransaction;


public class MaximumNumberOfInputsRestriction implements Restriction<BCTransaction> {

    int maximumNumberOfInputs;

    public MaximumNumberOfInputsRestriction(int maximumNumberOfInputs) {
        this.maximumNumberOfInputs =  maximumNumberOfInputs;
    }

    @Override
    public boolean evaluate(BCTransaction transaction) {
        return transaction.getNumberOfInputs() <= maximumNumberOfInputs;
    }
}
