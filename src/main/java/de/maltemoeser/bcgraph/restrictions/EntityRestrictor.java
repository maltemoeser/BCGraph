package de.maltemoeser.bcgraph.restrictions;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Takes multiple restrictions and evaluates whether they all apply.
 * @param <T> specifies the type of the underlying entity, e.g., BCTransaction or Transaction
 */
public class EntityRestrictor<T> implements Restrictor<T> {

    protected Collection<Restriction<T>> restrictions = new ArrayList<>();

    @Override
    public Restrictor<T> restrict(Restriction<T> restriction) {
        restrictions.add(restriction);
        return this;
    }

    @Override
    public boolean evaluate(T entity) {
        for(Restriction<T> r : restrictions) {
            if(!r.evaluate(entity)) {
                return false;
            }
        }
        return true;
    }

}
