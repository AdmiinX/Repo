package com.admiinx.repo;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okio.BufferedSource;

/**
 * {@inheritDoc}
 */
class RepoImpl<Key, Value> implements Repo<Key, Value> {

    private final RepoCache<Key, Value> memoryCache;
    private final RepoDiskCache<Key> diskCache;
    private final RepoParser<Value> parser;

    RepoImpl(RepoCache<Key, Value> memoryCache, RepoDiskCache<Key> diskCache, RepoParser<Value> parser) {
        this.memoryCache = memoryCache;
        this.diskCache = diskCache;
        this.parser = parser;
    }

    @Override
    public Observable<Result<Value>> get(final Key key) {
        return memoryCache.get(key)
                .map(new Function<Value, Result<Value>>() {
                    @Override
                    public Result<Value> apply(@NonNull Value value) throws Exception {
                        return new Result<>(value);
                    }
                })
                .toObservable()
                .switchIfEmpty(diskCache.get(key)
                        .map(new Function<BufferedSource, Value>() {
                            @Override
                            public Value apply(@NonNull BufferedSource source) throws Exception {
                                return parser.parse(source);
                            }
                        })
                        .flatMap(new Function<Value, MaybeSource<Value>>() {
                            @Override
                            public MaybeSource<Value> apply(@NonNull Value value) throws Exception {
                                return memoryCache.put(key, value).andThen(memoryCache.get(key));
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
    public Completable put(final Key key, BufferedSource source) {
        return diskCache.put(key, source).andThen(diskCache.get(key))
                .map(new Function<BufferedSource, Value>() {
                    @Override
                    public Value apply(@NonNull BufferedSource t) throws Exception {
                        return parser.parse(t);
                    }
                })
                .flatMapCompletable(new Function<Value, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull Value value) throws Exception {
                        return memoryCache.put(key, value);
                    }
                });
    }

    @Override
    public Completable invalidate(final Key key) {
        return memoryCache.invalidate(key).andThen(diskCache.invalidate(key));
    }

    @Override
    public Completable invalidateAll() {
        return memoryCache.invalidateAll().andThen(diskCache.invalidateAll());
    }
}
