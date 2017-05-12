package com.admiinx.repo.TowKeys;

import okio.BufferedSource;

/**
 * Created by admin-x on 5/7/17.
 */
public interface TowKeysRepoDiskCache<PrimaryKey, ForeignKey> extends TowKeysRepoCache<PrimaryKey, ForeignKey, BufferedSource> {
}
