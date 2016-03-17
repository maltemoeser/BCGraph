package de.maltemoeser.bcgraph.restrictions;

import de.maltemoeser.bcgraph.entities.BCTransaction;

public class MinimumNumberOfInputsRestriction implements Restriction<BCTransaction> {

    int minimumNumberOfInputs;

    public MinimumNumberOfInputsRestriction(int minimumNumberOfInputs) {
        this.minimumNumberOfInputs = minimumNumberOfInputs;
    }

    @Override
    public boolean evaluate(BCTransaction transaction) {
        return transaction.getNumberOfInputs() >= minimumNumberOfInputs;
    }

}
