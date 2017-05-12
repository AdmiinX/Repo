package com.admiinx.repo.TowKeys;

import io.reactivex.Maybe;

/**
 * Created by admin-x on 5/6/17.
 */
public interface TowKeysReadable<PrimaryKey, ForeignKey, Value> {
    Maybe<Value> read(PrimaryKey primaryKey, ForeignKey foreignKey);
}
