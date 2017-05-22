package com.admiinx.repo.TwoKeys;

import com.admiinx.repo.internal.DiskCache.DiskLruCache;
import com.admiinx.repo.internal.DiskCache.FileSystem;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.functions.Action;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import static com.admiinx.repo.internal.Utils.md5;

/**
 * {@inheritDoc}
 */
public class TwoKeysRepoCacheDiskImpl<PrimaryKey, ForeignKey> implements TwoKeysRepoDiskCache<PrimaryKey, ForeignKey> {
    private static final int SNAPSHOT_INDEX = 0;

    private final DiskLruCache mDiskCache;

    TwoKeysRepoCacheDiskImpl(File parentCacheDir, String cacheName, int cacheVersion, long maxSize) {
        File cacheDir = new File(parentCacheDir, cacheName);
        this.mDiskCache = DiskLruCache.create(FileSystem.SYSTEM, cacheDir, cacheVersion, 1, maxSize);
    }

    @Override
    public Maybe<BufferedSource> get(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return Maybe.fromCallable(new Callable<BufferedSource>() {
            @Override
            public BufferedSource call() throws Exception {
                DiskLruCache.Snapshot snapshot = mDiskCache.get(md5(primaryKey.toString()), md5(foreignKey.toString()));
                if (snapshot == null)
                    throw new NoSuchElementException();
                return Okio.buffer(snapshot.getSource(SNAPSHOT_INDEX));
            }
        }).onErrorComplete();
    }

    @Override
    public Completable put(final PrimaryKey primaryKey, final ForeignKey foreignKey, final BufferedSource source) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                DiskLruCache.Editor editor = mDiskCache.edit(md5(primaryKey.toString()), md5(foreignKey.toString()));
                BufferedSink sink = Okio.buffer(editor.newSink(SNAPSHOT_INDEX));
                sink.writeAll(source);
                sink.close();
                editor.commit();
            }
        });
    }

    @Override
    public Completable invalidate(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDiskCache.remove(md5(primaryKey.toString()), md5(foreignKey.toString()));
            }
        });
    }

    @Override
    public Completable invalidate(final ForeignKey foreignKey) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDiskCache.removeWithForeignKey(md5(foreignKey.toString()));
            }
        });
    }

    @Override
    public Completable invalidateAll() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDiskCache.delete();
            }
        });
    }


}
