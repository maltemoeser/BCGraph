package de.maltemoeser.bcgraph.restrictions;

public interface Restriction<T> {

    boolean evaluate(T entity);

}