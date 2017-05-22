package com.admiinx.repo.TwoKeys;

import com.admiinx.repo.RepoParser;
import com.admiinx.repo.Result;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okio.BufferedSource;

class TwoKeysRepoImpl<PrimaryKey, ForeignKey, Value> implements TwoKeysRepo<PrimaryKey, ForeignKey, Value> {
    private final TwoKeysRepoCache<PrimaryKey, ForeignKey, Value> memoryCache;
    private final TwoKeysRepoDiskCache<PrimaryKey, ForeignKey> diskCache;
    private final RepoParser<Value> parser;

    TwoKeysRepoImpl(TwoKeysRepoCache<PrimaryKey, ForeignKey, Value> memoryCache,
                    TwoKeysRepoDiskCache<PrimaryKey, ForeignKey> diskCache, RepoParser<Value> parser) {
        this.memoryCache = memoryCache;
        this.diskCache = diskCache;
        this.parser = parser;
    }

    @Override
    public Observable<Result<Value>> get(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return memoryCache.get(primaryKey, foreignKey)
                .map(new Function<Value, Result<Value>>() {
                    @Override
                    public Result<Value> apply(@NonNull Value data) throws Exception {
                        return new Result<>(data);
                    }
                })
                .toObservable()
                .switchIfEmpty(diskCache.get(primaryKey, foreignKey)
                        .map(new Function<BufferedSource, Value>() {
                            @Override
                            public Value apply(@NonNull BufferedSource t) throws Exception {
                                return parser.parse(t);
                            }
                        })
                        .flatMap(new Function<Value, MaybeSource<Value>>() {
                            @Override
                            public MaybeSource<Value> apply(@NonNull Value value) throws Exception {
                                return memoryCache.put(primaryKey, foreignKey, value)
                                        .andThen(memoryCache.get(primaryKey, foreignKey));
                            }
                        })
                        .map(new Function<Value, Result<Value>>() {
                            @Override
                            public Result<Value> apply(@NonNull Value value) throws Exception {
                                return new Result<>(value);
                            }
                        })
                        .toObservable()
                );
    }

    @Override
    public Completable put(final PrimaryKey primaryKey, final ForeignKey foreignKey, final BufferedSource source) {
        return diskCache.put(primaryKey, foreignKey, source).andThen(diskCache.get(primaryKey, foreignKey))
                .map(new Function<BufferedSource, Value>() {
                    @Override
                    public Value apply(@NonNull BufferedSource t) throws Exception {
                        return parser.parse(t);
                    }
                })
                .flatMapCompletable(new Function<Value, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull Value value) throws Exception {
                        return memoryCache.put(primaryKey, foreignKey, value);
                    }
                });
    }

    @Override
    public Completable invalidate(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return memoryCache.invalidate(primaryKey, foreignKey).andThen(diskCache.invalidate(primaryKey, foreignKey));
    }

    @Override
    public Completable invalidate(final ForeignKey foreignKey) {
        return memoryCache.invalidate(foreignKey).andThen(diskCache.invalidate(foreignKey));
    }

    @Override
    public Completable invalidateAll() {
        return memoryCache.invalidateAll().andThen(diskCache.invalidateAll());
    }
}
