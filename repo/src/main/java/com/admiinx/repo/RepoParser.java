package com.admiinx.repo;

import okio.BufferedSource;

/**
 * Represents a basic fetch function to parser {@link BufferedSource} to object of type {@link T}
 *
 * @param <T> target object type
 */
public interface RepoParser<T> extends Parser<BufferedSource, T> {

    /**
     * parser {@link BufferedSource} to object of type {@link T}
     *
     * @param value source BufferedSource
     * @return object of type {@link T}
     */
    @Override
    T parse(BufferedSource value);
}
