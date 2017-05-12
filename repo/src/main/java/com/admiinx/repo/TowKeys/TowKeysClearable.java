package com.admiinx.repo.TowKeys;

import io.reactivex.Completable;

/**
 * Created by admin-x on 5/6/17.
 */
public interface TowKeysClearable<PrimaryKey, ForeignKey> {

    Completable clear(PrimaryKey primaryKey, ForeignKey foreignKey);

    Completable clear(ForeignKey foreignKey);

    Completable clearAll();
}
