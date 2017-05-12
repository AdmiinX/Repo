package com.admiinx.repo;

/**
 * Represents a basic fetch function to parser object from type to another
 *
 * @param <T> source object type
 * @param <R> target object type
 */
public interface Parser<T, R> {

    /**
     * Parser object from type to another
     *
     * @param value source object
     * @return object of type {@link R}
     */
    R parse(T value);
}
