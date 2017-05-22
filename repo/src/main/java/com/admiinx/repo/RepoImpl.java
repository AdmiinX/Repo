package com.admiinx.repo;

import com.admiinx.repo.internal.TeeSource;
import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;

import java.util.concurrent.Callable;

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
                                Value value = parser.parse(source);
                                source.close();
                                return value;
                            }
                        })
                        .flatMap(new Function<Value, MaybeSource<Value>>() {
                            @Override
                            public MaybeSource<Value> apply(@NonNull final Value value) throws Exception {
                                return memoryCache.put(key, value)
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
    public Completable put(final Key key, final BufferedSource source) {
        return diskCache.edit(key)
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
