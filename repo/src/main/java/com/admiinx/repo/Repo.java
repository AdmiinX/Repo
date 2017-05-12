package com.admiinx.repo;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okio.BufferedSource;

public interface Repo<Key, Value> {

    Observable<Result<Value>> get(Key key);

    Completable put(Key key, BufferedSource source);

    Completable invalidate(Key key);

    Completable invalidateAll();
}
