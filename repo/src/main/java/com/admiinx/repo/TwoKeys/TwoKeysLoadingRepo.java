package com.admiinx.repo.TwoKeys;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface TwoKeysLoadingRepo<PrimaryKey, ForeignKey, Value> extends TwoKeysRepo<PrimaryKey, ForeignKey, Value> {

    Maybe<Value> fetch(PrimaryKey primaryKey, ForeignKey foreignKey);

    Completable refresh(PrimaryKey primaryKey, ForeignKey foreignKey);
}
