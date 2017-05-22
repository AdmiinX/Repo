package com.admiinx.repo.TwoKeys;

import com.admiinx.repo.internal.DiskCache.DiskLruCache;
import com.admiinx.repo.internal.DiskCache.FileSystem;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import okio.*;

import java.io.File;
import java.io.IOException;
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
                    return null;
                return Okio.buffer(snapshot.getSource(SNAPSHOT_INDEX));
            }
        }).onErrorComplete();
    }

    @Override
    public Completable put(final PrimaryKey primaryKey, final ForeignKey foreignKey, final BufferedSource source) {
        return edit(primaryKey, foreignKey)
                .flatMapCompletable(new Function<Sink, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull Sink sink) throws Exception {
                        BufferedSink bufferedSink = Okio.buffer(sink);
                        bufferedSink.writeAll(source);
                        bufferedSink.close();
                        source.close();
                        return Completable.complete();
                    }
                }).onErrorComplete();
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


    @Override
    public Maybe<Sink> edit(final PrimaryKey primaryKey, final ForeignKey foreignKey) {
        return Maybe.fromCallable(new Callable<Sink>() {
            @Override
            public Sink call() throws Exception {
                final DiskLruCache.Editor editor = mDiskCache.edit(md5(primaryKey.toString()), md5(foreignKey.toString()));
                if (editor == null) {
                    return null;
                }
                return new ForwardingSink(editor.newSink(SNAPSHOT_INDEX)) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        editor.commit();
                    }
                };
            }
        }).onErrorComplete();
    }
}
