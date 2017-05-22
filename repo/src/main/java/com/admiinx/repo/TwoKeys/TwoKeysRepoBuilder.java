package com.admiinx.repo.TwoKeys;

import com.admiinx.repo.RepoParser;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TwoKeysRepoBuilder<PrimaryKey, ForeignKey, Value> {

    private TwoKeysRepoCache<PrimaryKey, ForeignKey, Value> memoryCache;
    private TwoKeysRepoDiskCache<PrimaryKey, ForeignKey> diskCache;
    private RepoParser<Value> parser;

    public TwoKeysRepoBuilder<PrimaryKey, ForeignKey, Value> memoryCache(long expireAfter, TimeUnit expireAfterTimeUnit, long maxSize) {
        this.memoryCache = new TwoKeysRepoMemoryCacheImpl<>(expireAfter, expireAfterTimeUnit, maxSize);
        return this;
    }

    public TwoKeysRepoBuilder<PrimaryKey, ForeignKey, Value> diskCache(File cacheDir, String cacheName, int cacheVersion, long maxSize) {
        this.diskCache = new TwoKeysRepoCacheDiskImpl<>(cacheDir, cacheName, cacheVersion, maxSize);
        return this;
    }

    public TwoKeysRepoBuilder<PrimaryKey, ForeignKey, Value> setParser(RepoParser<Value> parser) {
        this.parser = parser;
        return this;
    }

    public TwoKeysRepo<PrimaryKey, ForeignKey, Value> build() {
        if (memoryCache == null)
            throw new IllegalArgumentException("Repo requires set memoryCache configuration");
        if (diskCache == null)
            throw new IllegalArgumentException("Repo requires set diskCache configuration");
        if (parser == null)
            throw new IllegalArgumentException("parser == null");

        return new TwoKeysRepoImpl<>(memoryCache, diskCache, parser);
    }

    public TwoKeysLoadingRepo<PrimaryKey, ForeignKey, Value> build(TwoKeysRepoFetcher<PrimaryKey, ForeignKey> fetcher) {
        if (fetcher == null)
            throw new IllegalArgumentException("fetcher == null");
        if (memoryCache == null)
            throw new IllegalArgumentException("LoadingRepo requires set memoryCache configuration");
        if (diskCache == null)
            throw new IllegalArgumentException("LoadingRepo requires set diskCache configuration");
        if (parser == null)
            throw new IllegalArgumentException("parser == null");

        return new TwoKeysLoadingRepoImpl<>(fetcher, memoryCache, diskCache, parser);
    }
}