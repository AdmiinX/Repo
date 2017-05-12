package com.admiinx.repo.TowKeys;

import io.reactivex.Maybe;
import okio.BufferedSource;

/**
 * Created by admin-x on 5/6/17.
 */
public interface TowKeysRepoFetcher<PrimaryKey, ForeignKey> extends TowKeysReadable<PrimaryKey, ForeignKey, BufferedSource> {

    @Override
    Maybe<BufferedSource> read(PrimaryKey primaryKey, ForeignKey foreignKey);
}
