package com.admiinx.repo.TowKeys;

import io.reactivex.Completable;

/**
 * Created by admin-x on 5/6/17.
 */
public interface TowKeysWritable<PrimaryKey, ForeignKey, Value> {
    Completable write(PrimaryKey key, ForeignKey foreignKey, Value value);
}
