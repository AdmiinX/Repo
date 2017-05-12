package com.admiinx.repo;

import io.reactivex.Maybe;

/**
 * Represents a basic get function to get the {@code Value} by {@code Key}
 *
 * @param <Key>   the key type
 * @param <Value> the value type
 */
interface Readable<Key, Value> {

    /**
     * Returns {@link Maybe} emit the value associated with {@code key}, or {@link Maybe#empty()} if there is no
     * value for {@code key}.
     *
     * @param key uses to get the {@code Value}
     * @return {@link Maybe} emit {@code Value} if Present otherwise complete
     */
    Maybe<Value> get(Key key);
}
