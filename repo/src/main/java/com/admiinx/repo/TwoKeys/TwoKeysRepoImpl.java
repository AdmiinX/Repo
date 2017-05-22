package com.admiinx.repo.TwoKeys;

import com.admiinx.repo.RepoParser;
import com.admiinx.repo.Result;
import com.admiinx.repo.internal.TeeSource;
import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;

import java.util.concurrent.Callable;

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
                            public Value apply(@NonNull BufferedSource source) throws Exception {
                                Value value = parser.parse(source);
                                source.close();
                                return value;
                            }
                        })
                        .flatMap(new Function<Value, MaybeSource<Value>>() {
                            @Override
                            public MaybeSource<Value> apply(@NonNull final Value value) throws Exception {
                                return memoryCache.put(primaryKey, foreignKey, value)
                                        .andThen(Maybe.fromCallable(new Callable<Value>() {
                                            @Override
                                            public Value call() throws Exception {
                                                return value;
                                            }
                                        }));
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
        return diskCache.edit(primaryKey, foreignKey)
                .map(new Function<Sink, Value>() {
                    @Override
                    public Value apply(@NonNull Sink sink) throws Exception {
                        TeeSource teeSource = new TeeSource(source, sink);
                        Value parsedValue = parser.parse(Okio.buffer(teeSource));
                        teeSource.close();
                        return parsedValue;
                    }
                })
                .switchIfEmpty(Maybe.fromCallable(new Callable<Value>() {
                    @Override
                    public Value call() throws Exception {
                        return parser.parse(source);
                    }
                }))
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
