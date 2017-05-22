package com.admiinx.repo.TwoKeys;

import com.admiinx.repo.Result;
import io.reactivex.Completable;
import io.reactivex.Observable;
import okio.BufferedSource;

public interface TwoKeysRepo<PrimaryKey, ForeignKey, Value> {

    Observable<Result<Value>> get(PrimaryKey primaryKey, ForeignKey foreignKey);

    Completable put(PrimaryKey primaryKey, ForeignKey foreignKey, BufferedSource source);

    Completable invalidate(PrimaryKey primaryKey, ForeignKey foreignKey);

    Completable invalidate(ForeignKey foreignKey);

    Completable invalidateAll();
}
