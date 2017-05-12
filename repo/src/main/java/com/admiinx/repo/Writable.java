package com.admiinx.repo;

import io.reactivex.Completable;

/**
 * Represents a basic put function to save the {@code Value} associates with {@code Key}
 *
 * @param <Key>   the key type
 * @param <Value> the value type
 */
interface Writable<Key, Value> {

    /**
     * Save and Associates {@code value} with {@code key}. If it's previously contained a
     * value associated with {@code key}, the old value is replaced by {@code value}.
     *
     * @param key   the key which {@code value} associated with
     * @param value the value which needed to put
     * @return {@link Completable} that complete if put success
     */
    Completable put(Key key, Value value);
}
