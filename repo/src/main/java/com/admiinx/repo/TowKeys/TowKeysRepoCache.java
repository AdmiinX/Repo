package com.admiinx.repo.TowKeys;

/**
 * Created by admin-x on 5/6/17.
 */
public interface TowKeysRepoCache<PrimaryKey, ForeignKey, Value> extends TowKeysReadable<PrimaryKey, ForeignKey, Value>,
        TowKeysWritable<PrimaryKey, ForeignKey, Value>, TowKeysClearable<PrimaryKey, ForeignKey> {
}
