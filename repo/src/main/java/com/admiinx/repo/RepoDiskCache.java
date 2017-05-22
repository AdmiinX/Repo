package com.admiinx.repo;

import io.reactivex.Maybe;
import okio.BufferedSource;
import okio.Sink;

/**
 * A disk cache mapping from keys to values.
 * Values are byte sequences as {@link BufferedSource}, accessible as streams.
 * <p>
 * {@inheritDoc}
 */
public interface RepoDiskCache<Key> extends RepoCache<Key, BufferedSource> {

    /**
     * Returns {@link Maybe} emit the {@linkplain Sink} associated with {@code key},
     * or {@link Maybe#empty()} if there is not able to provide Sink for {@code key}.
     *
     * @param key uses to get the {@code Sink}
     * @return {@link Maybe} emit {@code Sink} if able to provide otherwise complete
     */
    Maybe<Sink> edit(Key key);
}
