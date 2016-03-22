package de.maltemoeser.bcgraph.coinjoin;

import de.maltemoeser.bcgraph.entities.BCOutput;

import java.util.HashSet;
import java.util.Set;


public class InputSubSet {

    private Set<BCOutput> inputs = new HashSet<>();

    public int size() {
        return inputs.size();
    }

    public Set<BCOutput> getInputs() {
        return inputs;
    }

    public void setInputs(Set<BCOutput> inputs) {
        this.inputs = inputs;
    }

    public boolean addInput(BCOutput input) {
        return inputs.add(input);
    }

    public boolean removeInput(BCOutput input) {
        return inputs.remove(input);
    }

    public long getCumulativeValue() {
        return inputs.stream().mapToLong(BCOutput::getValue).sum();
    }

    public InputSubSet getCopy() {
        InputSubSet copy = new InputSubSet();
        copy.setInputs(new HashSet<>(inputs));
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputSubSet that = (InputSubSet) o;

        return inputs != null ? inputs.equals(that.inputs) : that.inputs == null;
    }

    @Override
    public int hashCode() {
        return inputs != null ? inputs.hashCode() : 0;
    }
}
