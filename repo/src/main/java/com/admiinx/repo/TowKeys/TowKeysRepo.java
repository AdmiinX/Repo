package com.admiinx.repo.TowKeys;

import com.admiinx.repo.Result;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import okio.BufferedSource;

/**
 * Created by admin-x on 5/7/17.
 */
public interface TowKeysRepo<PrimaryKey, ForeignKey, Value> {

    Observable<Result<Value>> get(PrimaryKey primaryKey, ForeignKey foreignKey);

    Completable put(PrimaryKey primaryKey, ForeignKey foreignKey, BufferedSource source);

    Maybe<Value> fetch(PrimaryKey primaryKey, ForeignKey foreignKey);

    Completable refresh(PrimaryKey primaryKey, ForeignKey foreignKey);

    Completable invalidate(PrimaryKey primaryKey, ForeignKey foreignKey);

    Completable invalidate(ForeignKey foreignKey);

    Completable invalidateAll();
}
