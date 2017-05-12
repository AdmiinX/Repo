package com.admiinx.repo;

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
public interface RepoCache<Key, Value> extends Readable<Key, Value>, Writable<Key, Value>, Cleanable<Key> {

}
