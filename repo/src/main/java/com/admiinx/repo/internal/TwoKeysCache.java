package com.admiinx.repo.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by admin-x on 5/4/17.
 */
public class TwoKeysCache<PrimaryKey, ForeignKey, Value> {

    private LoadingCache<Pair<PrimaryKey, ForeignKey>, Value> loadingCache;
    private ConcurrentMap<ForeignKey, Set<PrimaryKey>> keysMap;


    public TwoKeysCache(long expireAfter, TimeUnit expireAfterTimeUnit, long maxSize) {
        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfter, expireAfterTimeUnit)
                .build(new CacheLoader<Pair<PrimaryKey, ForeignKey>, Value>() {
                    @Override
                    public Value load(Pair<PrimaryKey, ForeignKey> key) throws Exception {
                        throw new NoSuchFieldException();
                    }
                });
        keysMap = new ConcurrentHashMap<>();
    }

    public TwoKeysCache(CacheBuilder<Pair<PrimaryKey, ForeignKey>, Value> cacheBuilder) {
        loadingCache = cacheBuilder.build(new CacheLoader<Pair<PrimaryKey, ForeignKey>, Value>() {
            @Override
            public Value load(Pair<PrimaryKey, ForeignKey> key) throws Exception {
                throw new NoSuchFieldException();
            }
        });
        keysMap = new ConcurrentHashMap<>();
    }

    public Value get(PrimaryKey primaryKey, ForeignKey foreignKey) throws ExecutionException {
        return loadingCache.get(new Pair<>(primaryKey, foreignKey));
    }

    public Value getUnchecked(PrimaryKey primaryKey, ForeignKey foreignKey) {
        return loadingCache.getUnchecked(new Pair<>(primaryKey, foreignKey));
    }

    public Map<Pair<PrimaryKey, ForeignKey>, Value> getAll(Iterable<? extends Pair<PrimaryKey, ForeignKey>> keys) throws ExecutionException {
        return loadingCache.getAll(keys);
    }

    public Value getIfPresent(Object PrimaryKey, Object foreignKey) {
        return loadingCache.getIfPresent(new Pair<>(PrimaryKey, foreignKey));
    }

    public Value get(PrimaryKey primaryKey, ForeignKey foreignKey, Callable<? extends Value> loader) throws ExecutionException {
        return loadingCache.get(new Pair<>(primaryKey, foreignKey), loader);
    }

    public void put(PrimaryKey primaryKey, ForeignKey foreignKey, Value value) {
        putKeys(primaryKey, foreignKey);
        loadingCache.put(new Pair<>(primaryKey, foreignKey), value);
    }

    private void putKeys(PrimaryKey primaryKey, ForeignKey foreignKey) {
        Set<PrimaryKey> primaryKeys = keysMap.get(foreignKey);
        if (primaryKeys == null)
            primaryKeys = new HashSet<>();
        primaryKeys.add(primaryKey);
        keysMap.put(foreignKey, primaryKeys);
    }

    public void invalidate(PrimaryKey primaryKey, ForeignKey foreignKey) {
        if (keysMap.containsKey(foreignKey)) {
            Set<PrimaryKey> primaryKeys = keysMap.get(foreignKey);
            if (primaryKeys != null) {
                primaryKeys.remove(primaryKey);
                keysMap.put(foreignKey, primaryKeys);
            }
        }
        loadingCache.invalidate(new Pair<>(primaryKey, foreignKey));
    }

    public void invalidate(ForeignKey foreignKey) {
        for (PrimaryKey primaryKey : keysMap.get(foreignKey)) {
            loadingCache.invalidate(new Pair<>(primaryKey, foreignKey));
        }
        keysMap.remove(foreignKey);
    }

    public void invalidateAll() {
        loadingCache.invalidateAll();
        keysMap.clear();
    }

    public long size() {
        return loadingCache.size();
    }
}
