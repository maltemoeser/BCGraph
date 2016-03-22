package de.maltemoeser.bcgraph.coinjoin;

import de.maltemoeser.bcgraph.entities.BCOutput;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Wrapper class to group multiple InputSubSet objects into a full input set.
 */
public class InputFullSet {

    private Set<InputSubSet> subSets = new HashSet<>();

    public Set<InputSubSet> getSubSets() {
        return subSets;
    }

    public void setSubSets(Set<InputSubSet> subSets) {
        this.subSets = subSets;
    }

    public boolean addSubSet(InputSubSet subSet) {
        return subSets.add(subSet);
    }

    public boolean removeSubSet(InputSubSet subSet) {
        return subSets.remove(subSet);
    }

    public int size() {
        return subSets.size();
    }

    public int getNumberOfInputs() {
        return subSets.stream().mapToInt(InputSubSet::size).sum();
    }

    public boolean containsDuplicateInputs() {
        Set<BCOutput> s = new HashSet<>();
        for(InputSubSet iss : subSets) {
            for(BCOutput input : iss.getInputs()) {
                if(!s.add(input)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return a copy of this object that retains the references to the underlying subsets.
     * @return a new InputFullSet initialized with the same subsets.
     */
    public InputFullSet getCopy() {
        InputFullSet copy = new InputFullSet();
        copy.setSubSets(new HashSet<>(subSets));
        return copy;
    }

    /**
     * Returns the cumulative values of all subsets in this full set.
     * @return a list of values of the underlying subsets.
     */
    public List<Long> getInputSubSetValues() {
        return subSets.stream().map(InputSubSet::getCumulativeValue).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputFullSet that = (InputFullSet) o;

        return subSets != null ? subSets.equals(that.subSets) : that.subSets == null;
    }

    @Override
    public int hashCode() {
        return subSets != null ? subSets.hashCode() : 0;
    }
}
