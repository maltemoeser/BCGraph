package de.maltemoeser.bcgraph.restrictions;

public interface Restrictor<T> {

    boolean evaluate(T entity);

    Restrictor<T> restrict(Restriction<T> restriction);

}
