package de.maltemoeser.bcgraph.coinjoin;

import de.maltemoeser.bcgraph.entities.BCOutput;

/**
 * An output subset consists of a change output and a spend value.
 * Because the spends form a kind of anonymity set, we cannot identify
 * the exact spend output belonging to a change output.
 */
public class OutputSubSet {

    protected final BCOutput output;
    protected final long spendingValue;

    public OutputSubSet(BCOutput output, long spendingValue) {
        this.output = output;
        this.spendingValue = spendingValue;
    }

    /**
     * Computes the value of this output subset.
     * - If the subset contains change, the value is the sum of spend and change output.
     * - Otherwise, it will return the value of the spend only.
     * @return the cumulative value of the outputs in the subset
     */
    public long getValue() {
        if (output == null) {
            return spendingValue;
        } else {
            return output.getValue() + spendingValue;
        }
    }
}
