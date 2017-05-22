package com.admiinx.repo.TwoKeys;

import com.admiinx.repo.internal.TwoKeysCache;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.functions.Action;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 */
public class TwoKeysRepoMemoryCacheImpl<PrimaryKey, ForeignKey, Parsed> implements TwoKeysRepoCache<PrimaryKey, ForeignKey, Parsed> {

    private final TwoKeysCache<PrimaryKey, ForeignKey, Parsed> memCache;

    TwoKeysRepoMemoryCacheImpl(long expireAfter, TimeUnit expireAfterTimeUnit, long maxSize) {
        memCache = new TwoKeysCache<>(expireAfter, expireAfterTimeUnit, maxSize);
    }

    @Override
    public Maybe<Parsed> get(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return Maybe.fromCallable(new Callable<Parsed>() {
            @Override
            public Parsed call() throws Exception {
                return memCache.get(primaryKey, foreignKey);
            }
        }).onErrorComplete();
    }

    @Override
    public Completable put(final PrimaryKey primaryKey, final ForeignKey foreignKey, final Parsed parsed) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.put(primaryKey, foreignKey, parsed);
            }
        });
    }

    @Override
    public Completable invalidate(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.invalidate(primaryKey, foreignKey);
            }
        });
    }

    @Override
    public Completable invalidate(final ForeignKey foreignKey) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.invalidate(foreignKey);
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
