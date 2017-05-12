package com.admiinx.repo;

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
class RepoDiskCacheImpl<Key> implements RepoDiskCache<Key> {
    private static final int SNAPSHOT_INDEX = 0;

    private final DiskLruCache mDiskCache;

    RepoDiskCacheImpl(File parentCacheDir, String cacheName, int cacheVersion, long maxSize) {
        File cacheDir = new File(parentCacheDir, cacheName);
        this.mDiskCache = DiskLruCache.create(FileSystem.SYSTEM, cacheDir, cacheVersion, 1, maxSize);
    }

    @Override
    public Maybe<BufferedSource> get(final Key key) {
        return Maybe.fromCallable(new Callable<BufferedSource>() {
            @Override
            public BufferedSource call() throws Exception {
                DiskLruCache.Snapshot snapshot = mDiskCache.get(md5(key.toString()));
                if (snapshot == null)
                    throw new NoSuchElementException();
                return Okio.buffer(snapshot.getSource(SNAPSHOT_INDEX));
            }
        }).onErrorComplete();
    }

    @Override
    public Completable put(final Key key, final BufferedSource source) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                DiskLruCache.Editor editor = mDiskCache.edit(md5(key.toString()));
                if (editor != null) {
                    BufferedSink sink = Okio.buffer(editor.newSink(SNAPSHOT_INDEX));
                    sink.writeAll(source);
                    sink.close();
                    editor.commit();
                }

            }
        });
    }

    @Override
    public Completable invalidate(final Key key) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                mDiskCache.remove(md5(key.toString()));
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
