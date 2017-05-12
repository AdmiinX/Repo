package com.admiinx.repo.TowKeys;

import com.admiinx.repo.RepoParser;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TowKeysRepoBuilder<PrimaryKey, ForeignKey, Value> {

    private TowKeysRepoFetcher<PrimaryKey, ForeignKey> fetcher;
    private TowKeysRepoCache<PrimaryKey, ForeignKey, Value> memoryCache;
    private TowKeysRepoDiskCache<PrimaryKey, ForeignKey> diskCache;
    private RepoParser<Value> parser;

    public TowKeysRepoBuilder<PrimaryKey, ForeignKey, Value> fetcher(TowKeysRepoFetcher<PrimaryKey, ForeignKey> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public TowKeysRepoBuilder<PrimaryKey, ForeignKey, Value> memoryCache(long expireAfter, TimeUnit expireAfterTimeUnit, long maxSize) {
        this.memoryCache = new TowKeysRepoMemoryCacheImpl<>(expireAfter, expireAfterTimeUnit, maxSize);
        return this;
    }

    public TowKeysRepoBuilder<PrimaryKey, ForeignKey, Value> diskCache(File cacheDir, String cacheName, int cacheVersion, long maxSize) {
        this.diskCache = new TowKeysRepoCacheDiskImpl<>(cacheDir, cacheName, cacheVersion, maxSize);
        return this;
    }

    public TowKeysRepoBuilder<PrimaryKey, ForeignKey, Value> setParser(RepoParser<Value> parser) {
        this.parser = parser;
        return this;
    }

    public TowKeysRepo<PrimaryKey, ForeignKey, Value> build() {
        if (fetcher == null)
            throw new IllegalArgumentException("fetcher == null");
        if (memoryCache == null)
            throw new IllegalArgumentException("LoadingRepo requires set memoryCache configuration");
        if (diskCache == null)
            throw new IllegalArgumentException("LoadingRepo requires set diskCache configuration");
        if (parser == null)
            throw new IllegalArgumentException("parser == null");

        return new TowKeysRepoImpl<>(fetcher, memoryCache, diskCache, parser);
    }
}