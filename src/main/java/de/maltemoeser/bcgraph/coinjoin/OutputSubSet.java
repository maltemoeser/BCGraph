package de.maltemoeser.bcgraph.coinjoin;

import de.maltemoeser.bcgraph.entities.BCOutput;

public class OutputSubSet {

    protected final BCOutput output;
    protected final long spendingValue;

    public OutputSubSet(BCOutput output, long spendingValue) {
        this.output = output;
        this.spendingValue = spendingValue;
    }

    public long getValue() {
        if (output == null) {
            return spendingValue;
        } else {
            return output.getValue() + spendingValue;
        }
    }
}
