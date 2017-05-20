package com.admiinx.repo;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * A semi-persistent mapping from keys to values.
 * get cache values using {@link #get(Object)}
 * Cache values are manually added using {@link #put(Object, Object)},
 * and are stored in the cache until either evicted or manually invalidate using {@link #invalidate(Object)}.
 * or clear the whole cache using {@link #invalidateAll()}
 *
 * @param <Key>   the key type
 * @param <Value> the value type
 */
public interface RepoCache<Key, Value> {

    /**
     * Returns {@link Maybe} emit the value associated with {@code key}, or {@link Maybe#empty()} if there is no
     * value for {@code key}.
     *
     * @param key uses to get the {@code Value}
     * @return {@link Maybe} emit {@code Value} if Present otherwise complete
     */
    Maybe<Value> get(Key key);

    /**
     * Save and Associates {@code value} with {@code key}. If it's previously contained a
     * value associated with {@code key}, the old value is replaced by {@code value}.
     *
     * @param key   the key which {@code value} associated with
     * @param value the value which needed to put
     * @return {@link Completable} that complete if put success
     */
    Completable put(Key key, Value value);


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
