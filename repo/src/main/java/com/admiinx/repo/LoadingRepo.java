package com.admiinx.repo;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by admin-x on 5/7/17.
 */
public interface LoadingRepo<Key, Value> extends Repo<Key, Value> {

    Observable<Result<Value>> get(Key key);

    Maybe<Value> fetch(Key key);

    Completable refresh(Key key);
}
