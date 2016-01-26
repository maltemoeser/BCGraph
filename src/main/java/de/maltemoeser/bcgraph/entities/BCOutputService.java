package de.maltemoeser.bcgraph.entities;

public class BCOutputService extends BCEntityService {

    /**
     * Create a new output based on the output's index and its value.
     *
     * @param index the index of the output in the transaction
     * @param value the value of the output in satoshi
     * @return a new BCOutput object
     */
    public BCOutput createOutput(int index, long value) {
        BCOutput myOutput = getNewOutput();
        myOutput.setIndex(index);
        myOutput.setValue(value);
        return myOutput;
    }
}
