package com.admiinx.repo.TwoKeys;

import io.reactivex.Completable;
import io.reactivex.Maybe;

/**
 * A semi-persistent mapping from keys to values.
 * get cache values using {@link #get(Object, Object)}
 * Cache values are manually added using {@link #put(Object, Object, Object)},
 * and are stored in the cache until either evicted or manually invalidate using {@link #invalidate(Object, Object)}.
 * or clear the whole cache using {@link #invalidateAll()}
 *
 * @param <PrimaryKey> the PrimaryKey type
 * @param <ForeignKey> the ForeignKey type
 * @param <Value>      the value type
 */
public interface TwoKeysRepoCache<PrimaryKey, ForeignKey, Value> {

    /**
     * Returns {@link Maybe} emit the value associated with {@code key}, or {@link Maybe#empty()} if there is no
     * value for {@code key}.
     *
     * @param primaryKey uses to get the {@code Value}
     * @param foreignKey uses to get the {@code Value}
     * @return {@link Maybe} emit {@code Value} if Present otherwise complete
     */
    Maybe<Value> get(PrimaryKey primaryKey, ForeignKey foreignKey);

    /**
     * Save and Associates {@code value} with {@code primaryKey}{@code foreignKey}. If it's previously contained a
     * value associated with {@code primaryKey},{@code foreignKey}, the old value is replaced by {@code value}.
     *
     * @param primaryKey the primaryKey which {@code value} associated with
     * @param foreignKey the foreignKey which {@code value} associated with
     * @param value      the value which needed to put
     * @return {@link Completable} that complete if put success
     */
    Completable put(PrimaryKey primaryKey, ForeignKey foreignKey, Value value);


    /**
     * Discards the value associates with keys {@code primaryKey}, {@code foreignKey}.
     *
     * @param primaryKey the primaryKey which {@code value} associated with
     * @param foreignKey the foreignKey which {@code value} associated with
     * @return {@link Completable} that complete if invalidate success
     */
    Completable invalidate(PrimaryKey primaryKey, ForeignKey foreignKey);

    /**
     * Discards the value associates with key {@code foreignKey}.
     *
     * @param foreignKey the foreignKey which {@code value} associated with
     * @return {@link Completable} that complete if invalidate success
     */
    Completable invalidate(ForeignKey foreignKey);

    /**
     * Discards all entries.
     *
     * @return {@link Completable} that complete if invalidateAll success
     */
    Completable invalidateAll();

}
