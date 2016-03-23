package de.maltemoeser.bcgraph.coinjoin;

import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.entities.BCTransaction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The SubSetSolver attempts to compute matching subsets in a strict CoinJoin transaction,
 * where all spending outputs have the exact same value and there is at most one change output per output subset.
 * Subsets are allowed to differ in value up to the feeVariance.
 */
public class SubSetSolver {

    protected BCTransaction transaction;
    protected long mostFrequentOutputValue;
    protected int numberOfParticipants;
    protected double feeVariance;
    protected int maximumSubSetSize;

    protected List<OutputSubSet> outputSubSets = new ArrayList<>();
    protected List<Long> outputSubSetValues;
    protected long valueOfLargestOutputSubSet;

    protected HashSet<InputSubSet> inputSubSets;
    protected HashSet<InputFullSet> inputFullSets;

    public SubSetSolver(BCTransaction transaction) {
        this.transaction = transaction;
    }

    // Getters

    public long getMostFrequentOutputValue() {
        return mostFrequentOutputValue;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public long getValueOfLargestOutputSubSet() {
        return valueOfLargestOutputSubSet;
    }

    public double getFeeVariance() {
        return feeVariance;
    }

    public HashSet<InputSubSet> getInputSubSets() {
        return inputSubSets;
    }

    public HashSet<InputFullSet> getInputFullSets() {
        return inputFullSets;
    }

    /**
     * Performs all steps necessary to solve a CoinJoin puzzle.
     *
     * @return true if we were able to compute a result, false if not
     */
    public boolean solve() {
        extractMostFrequentOutputValue();
        computeOutputSubSets();
        computeAllowedFeeVariance();
        if (hasOutputValuesCloseToEachOther()) {
            return false;
        }
        computeMaximumSubSetSize();
        // number of possible combinations is 2^n, we therefore avoid too large n
        if(maximumSubSetSize > 20) {
            return false;
        }
        computeInputSubSets();
        if(inputSubSets.size() > 1000) {
            return false;
        }
        computePossibleInputFullSets();

        return true;
    }

    /**
     * Extract the most frequent output value and its frequency.
     */
    protected void extractMostFrequentOutputValue() {
        Map<Long, Integer> values = new HashMap<>();

        // Extract output values and store their frequency in a HashMap
        for (BCOutput output : transaction.getOutputs()) {
            long key = output.getValue();
            if (values.containsKey(key)) {
                values.put(key, values.get(key) + 1);
            } else {
                values.put(key, 1);
            }
        }

        for (Map.Entry<Long, Integer> entry : values.entrySet()) {
            int numberOfOccurrences = entry.getValue();
            if (numberOfOccurrences > numberOfParticipants) {
                numberOfParticipants = numberOfOccurrences;
                mostFrequentOutputValue = entry.getKey();
            }
        }
    }

    /**
     * Combine each spending output with one of the change outputs.
     * If one input subset is fully spent, we have one OutputSubSet without a change output.
     */
    protected void computeOutputSubSets() {
        List<BCOutput> changeOutputs = transaction.getOutputs().stream().filter(
                e -> e.getValue() != mostFrequentOutputValue
        ).collect(Collectors.toList());

        for (BCOutput output : changeOutputs) {
            outputSubSets.add(new OutputSubSet(output, mostFrequentOutputValue));
        }

        if (changeOutputs.size() != numberOfParticipants) {
            outputSubSets.add(new OutputSubSet(null, mostFrequentOutputValue));
        }
        outputSubSetValues = outputSubSets.stream().map(OutputSubSet::getValue).collect(Collectors.toList());
        valueOfLargestOutputSubSet = outputSubSets.stream().mapToLong(OutputSubSet::getValue).max().getAsLong();
    }

    /**
     * Input and output subsets can vary due to the transaction fee.
     */
    protected void computeAllowedFeeVariance() {
        feeVariance = transaction.getFee();
    }

    /**
     * Check whether the cumulative value of our output subsets are closer to each other than the fee variance
     * In this case we may produce false results.
     *
     * @return true if output values are close to each other, false if not
     */
    protected boolean hasOutputValuesCloseToEachOther() {
        List<Long> outputSubsetValues = outputSubSets.stream().map(OutputSubSet::getValue).collect(Collectors.toList());
        Collections.sort(outputSubsetValues);

        // pairwise comparison of output subset values
        for (int i = 1; i < outputSubsetValues.size(); i++) {
            if (outputSubsetValues.get(i) - outputSubsetValues.get(i - 1) < feeVariance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compute all meaningful input subsets (ie. subsets that in value match an OutputSubSet).
     */
    protected void computeInputSubSets() {
        inputSubSets = getInputSubsets(new ArrayList<>(transaction.getInputs()), maximumSubSetSize);
    }

    /**
     * Computes an upper limit on the number inputs in a subset.
     * With x inputs and y participants, we can have subsets of size x - (y - 1)
     */
    protected void computeMaximumSubSetSize() {
        maximumSubSetSize = transaction.getNumberOfInputs() - outputSubSets.size() + 1;
    }

    /**
     * Recursively build possible input subsets.
     * Solution is inspired by the following code snippet http://stackoverflow.com/a/12548381 by user amit https://stackoverflow.com/users/572670/amit
     */
    private HashSet<InputSubSet> getInputSubsets(List<BCOutput> superSet, int maximumSubSetSize) {
        HashSet<InputSubSet> solutions = new HashSet<>();
        getInputSubsets(superSet, maximumSubSetSize, 0, new InputSubSet(), solutions);
        return solutions;
    }

    private void getInputSubsets(List<BCOutput> superSet, int maxSize, int currentIndex, InputSubSet current, HashSet<InputSubSet> solutions) {
        int currentSize = current.size();

        // add current set of inputs to our solutions
        if (currentSize > 0 && currentSize <= maxSize) {

            // only store solution if there is an output subset that roughly matches in cumulative value
            if (outputSubSetValues.stream().anyMatch(e -> Math.abs(e - current.getCumulativeValue()) <= feeVariance)) {
                solutions.add(current);
            }

            // stop once we reach the maximum size
            if (currentSize == maxSize) {
                return;
            }
        }

        // stop if value of subset becomes too large
        if (currentSize > 0) {
            if (current.getCumulativeValue() > feeVariance + valueOfLargestOutputSubSet) {
                return;
            }
        }

        // we reached the end
        if (currentIndex == superSet.size()) {
            return;
        }

        // call recursively without next element
        getInputSubsets(superSet, maxSize, currentIndex + 1, current.getCopy(), solutions);

        // call recursively with next element
        InputSubSet next = current.getCopy();
        BCOutput i = superSet.get(currentIndex);
        next.addInput(i);
        getInputSubsets(superSet, maxSize, currentIndex + 1, next, solutions);
    }


    /**
     * Compute all combinations of input subsets that match the set of output subsets in value.
     */
    protected void computePossibleInputFullSets() {
        inputFullSets = getInputFullSets(new ArrayList<>(inputSubSets));
    }

    /**
     * Recursively build possible full sets.
     * Solution is inspired by the following code snippet http://stackoverflow.com/a/12548381 by user amit https://stackoverflow.com/users/572670/amit
     */
    private HashSet<InputFullSet> getInputFullSets(List<InputSubSet> superSet) {
        HashSet<InputFullSet> solutions = new HashSet<>();
        getInputFullSets(superSet, numberOfParticipants, 0, new InputFullSet(), solutions);
        return solutions;
    }

    private void getInputFullSets(List<InputSubSet> superSet, int maxSize, int currentIndex, InputFullSet currentFullSet, HashSet<InputFullSet> solutions) {

        // if two subsets contain the same input the solution is invalid
        if (currentFullSet.containsDuplicateInputs()) {
            return;
        }

        // no duplicate inputs, and all inputs are used: valid solution
        if (currentFullSet.getNumberOfInputs() == transaction.getNumberOfInputs()) {
            solutions.add(currentFullSet);
        }

        // reached maximum number of input subsets
        if (currentFullSet.size() == maxSize) {
            return;
        }

        // reached end of list
        if (currentIndex == superSet.size()) {
            return;
        }

        // call recursively without next element
        getInputFullSets(superSet, maxSize, currentIndex + 1, currentFullSet.getCopy(), solutions);

        // call recursively with next element
        InputFullSet next = currentFullSet.getCopy();
        InputSubSet i = superSet.get(currentIndex);
        next.addSubSet(i);
        getInputFullSets(superSet, maxSize, currentIndex + 1, next, solutions);
    }

    /**
     * We have a single solution if there is exactly one inputFullSet
     *
     * @return true if there is a single solution, false if not
     */
    public boolean hasSingleSolution() {
        return inputFullSets.size() == 1;
    }
}
