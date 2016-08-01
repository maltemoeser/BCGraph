package de.maltemoeser.bcgraph.restrictions.graph;

import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.restrictions.Restriction;


public class MaximumNumberOfInputsRestriction implements Restriction<BCTransaction> {

    private int maximumNumberOfInputs;

    public MaximumNumberOfInputsRestriction(int maximumNumberOfInputs) {
        this.maximumNumberOfInputs =  maximumNumberOfInputs;
    }

    @Override
    public boolean evaluate(BCTransaction transaction) {
        return transaction.getNumberOfInputs() <= maximumNumberOfInputs;
    }
}
