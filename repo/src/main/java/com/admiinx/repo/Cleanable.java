package com.admiinx.repo;

import io.reactivex.Completable;

/**
 * Represents a basic Cleanable with invalidate function to clear the {@code Value} associates with {@code Key}
 * or invalidateAll to clear all entries
 *
 * @param <Key> the key type
 */
interface Cleanable<Key> {

    /**
     * Discards the value associates with key {@code key}.
     *
     * @param key the key which {@code value} associated with
     * @return {@link Completable} that complete if invalidate success
     */
    Completable invalidate(Key key);

    /**
     * Discards all entries.
     *
     * @return {@link Completable} that complete if invalidateAll success
     */
    Completable invalidateAll();
}
