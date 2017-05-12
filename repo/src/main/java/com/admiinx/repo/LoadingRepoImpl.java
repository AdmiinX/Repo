package com.admiinx.repo;

import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okio.BufferedSource;

import java.net.ConnectException;
import java.util.concurrent.Callable;


/**
 * {@inheritDoc}
 */
class LoadingRepoImpl<Key, Value> implements LoadingRepo<Key, Value> {

    private final RepoFetcher<Key> fetcher;
    private final RepoCache<Key, Value> memoryCache;
    private final RepoDiskCache<Key> diskCache;
    private final RepoParser<Value> parser;

    LoadingRepoImpl(RepoFetcher<Key> fetcher, RepoCache<Key, Value> memoryCache, RepoDiskCache<Key> diskCache, RepoParser<Value> parser) {
        this.fetcher = fetcher;
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
                .switchIfEmpty(fetcher.fetch(key)
                        .switchIfEmpty(invalidate(key).toMaybe().cast(BufferedSource.class))
                        .flatMap(new Function<BufferedSource, MaybeSource<BufferedSource>>() {
                            @Override
                            public MaybeSource<BufferedSource> apply(@NonNull BufferedSource source) throws Exception {
                                return diskCache.put(key, source).andThen(diskCache.get(key));
                            }
                        })
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
                        .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Result<Value>>>() {
                            @Override
                            public ObservableSource<? extends Result<Value>> apply(@NonNull final Throwable throwable) throws Exception {
                                Observable<Result<Value>> error = Observable.fromCallable(new Callable<Result<Value>>() {
                                    @Override
                                    public Result<Value> call() throws Exception {
                                        return new Result<>(throwable);
                                    }
                                });
                                if (throwable instanceof ConnectException)
                                    error.concatWith(diskCache.get(key)
                                            .map(new Function<BufferedSource, Value>() {
                                                @Override
                                                public Value apply(@NonNull BufferedSource source) throws Exception {
                                                    return parser.parse(source);
                                                }
                                            })
                                            .map(new Function<Value, Result<Value>>() {
                                                @Override
                                                public Result<Value> apply(@NonNull Value value) throws Exception {
                                                    return new Result<>(value);
                                                }
                                            })
                                            .toObservable());
                                return error;
                            }
                        })
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
    public Maybe<Value> fetch(final Key key) {
        return fetcher.fetch(key)
                .switchIfEmpty(invalidate(key).toMaybe().cast(BufferedSource.class))
                .flatMap(new Function<BufferedSource, MaybeSource<BufferedSource>>() {
                    @Override
                    public MaybeSource<BufferedSource> apply(@NonNull BufferedSource source) throws Exception {
                        return diskCache.put(key, source).andThen(diskCache.get(key));
                    }
                })
                .map(new Function<BufferedSource, Value>() {
                    @Override
                    public Value apply(@NonNull BufferedSource t) throws Exception {
                        return parser.parse(t);
                    }
                })
                .flatMap(new Function<Value, MaybeSource<? extends Value>>() {
                    @Override
                    public MaybeSource<? extends Value> apply(@NonNull Value value) throws Exception {
                        return memoryCache.put(key, value).andThen(memoryCache.get(key));
                    }
                });
    }

    @Override
    public Completable refresh(final Key key) {
        return fetcher.fetch(key)
                .switchIfEmpty(invalidate(key).toMaybe().cast(BufferedSource.class))
                .flatMap(new Function<BufferedSource, MaybeSource<BufferedSource>>() {
                    @Override
                    public MaybeSource<BufferedSource> apply(@NonNull BufferedSource source) throws Exception {
                        return diskCache.put(key, source).andThen(diskCache.get(key));
                    }
                })
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
    public Completable invalidate(Key key) {
        return memoryCache.invalidate(key).andThen(diskCache.invalidate(key));
    }

    @Override
    public Completable invalidateAll() {
        return memoryCache.invalidateAll().andThen(diskCache.invalidateAll());
    }
}
