package de.maltemoeser.bcgraph.restrictions.graph;

import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.restrictions.Restriction;


public class NoOpReturnOutputRestriction implements Restriction<BCTransaction> {

    @Override
    public boolean evaluate(BCTransaction entity) {
        for(BCOutput output : entity.getOutputs()) {
            if(output.isOpReturn()) {
                return false;
            }
        }
        return true;
    }
}
