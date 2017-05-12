package com.admiinx.repo;

import okio.BufferedSource;

/**
 * A disk cache mapping from keys to values.
 * Values are byte sequences as {@link BufferedSource}, accessible as streams.
 * <p>
 * {@inheritDoc}
 */
public interface RepoDiskCache<Key> extends RepoCache<Key, BufferedSource> {
}
