package com.admiinx.repo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.functions.Action;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 */
class RepoMemoryCacheImpl<Key, Parsed> implements RepoCache<Key, Parsed> {

    private final Cache<Key, Parsed> memCache;

    RepoMemoryCacheImpl(long expireAfter, TimeUnit expireAfterTimeUnit, long maxSize) {
        memCache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfter, expireAfterTimeUnit)
                .build();
    }

    @Override
    public Maybe<Parsed> get(final Key key) {
        return Maybe.fromCallable(new Callable<Parsed>() {
            @Override
            public Parsed call() throws Exception {
                return memCache.get(key, new Callable<Parsed>() {
                    @Override
                    public Parsed call() throws Exception {
                        throw new NoSuchElementException();
                    }
                });
            }
        }).onErrorComplete();
    }

    @Override
    public Completable put(final Key key, final Parsed parsed) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.put(key, parsed);
            }
        });
    }

    @Override
    public Completable invalidate(final Key key) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.invalidate(key);
            }
        });
    }

    @Override
    public Completable invalidateAll() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.invalidateAll();
            }
        });
    }
}
