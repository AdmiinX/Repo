package com.admiinx.repo;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class RepoBuilder<Key, Value> {
    private RepoCache<Key, Value> memoryCache;
    private RepoDiskCache<Key> diskCache;
    private RepoParser<Value> parser;

    public RepoBuilder<Key, Value> memoryCache(long expireAfter, TimeUnit expireAfterTimeUnit, long maxSize) {
        this.memoryCache = new RepoMemoryCacheImpl<>(expireAfter, expireAfterTimeUnit, maxSize);
        return this;
    }

    public RepoBuilder<Key, Value> diskCache(File cacheDir, String cacheName, int cacheVersion, long maxSize) {
        this.diskCache = new RepoDiskCacheImpl<>(cacheDir, cacheName, cacheVersion, maxSize);
        return this;
    }

    public RepoBuilder<Key, Value> setParser(RepoParser<Value> parser) {
        this.parser = parser;
        return this;
    }

    public LoadingRepo<Key, Value> build(RepoFetcher<Key> fetcher) {
        if (fetcher == null)
            throw new IllegalArgumentException("fetcher == null");
        if (memoryCache == null)
            throw new IllegalArgumentException("LoadingRepo requires set memoryCache configuration");
        if (diskCache == null)
            throw new IllegalArgumentException("LoadingRepo requires set diskCache configuration");
        if (parser == null)
            throw new IllegalArgumentException("parser == null");

        return new LoadingRepoImpl<>(fetcher, memoryCache, diskCache, parser);
    }

    public Repo<Key, Value> build() {
        if (memoryCache == null)
            throw new IllegalArgumentException("Repo requires set memoryCache configuration");
        if (diskCache == null)
            throw new IllegalArgumentException("Repo requires set diskCache configuration");
        if (parser == null)
            throw new IllegalArgumentException("parser == null");

        return new RepoImpl<>(memoryCache, diskCache, parser);
    }
}