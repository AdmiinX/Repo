package com.admiinx.repo.TowKeys;

import com.admiinx.repo.internal.TowKeysCache;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.functions.Action;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin-x on 5/6/17.
 */
public class TowKeysRepoMemoryCacheImpl<PrimaryKey, ForeignKey, Parsed> implements TowKeysRepoCache<PrimaryKey, ForeignKey, Parsed> {

    private final TowKeysCache<PrimaryKey, ForeignKey, Parsed> memCache;

    TowKeysRepoMemoryCacheImpl(long expireAfter, TimeUnit expireAfterTimeUnit, long maxSize) {
        memCache = new TowKeysCache<>(expireAfter, expireAfterTimeUnit, maxSize);
    }

    @Override
    public Maybe<Parsed> read(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return Maybe.fromCallable(new Callable<Parsed>() {
            @Override
            public Parsed call() throws Exception {
                return memCache.get(primaryKey, foreignKey);
            }
        }).onErrorComplete();
    }

    @Override
    public Completable write(final PrimaryKey primaryKey, final ForeignKey foreignKey, final Parsed parsed) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.put(primaryKey, foreignKey, parsed);
            }
        });
    }

    @Override
    public Completable clear(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.invalidate(primaryKey, foreignKey);
            }
        });
    }

    @Override
    public Completable clear(final ForeignKey foreignKey) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.invalidate(foreignKey);
            }
        });
    }

    @Override
    public Completable clearAll() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                memCache.invalidateAll();
            }
        });
    }
}
