package com.admiinx.repo.TwoKeys;

import okio.BufferedSource;

public interface TwoKeysRepoDiskCache<PrimaryKey, ForeignKey> extends TwoKeysRepoCache<PrimaryKey, ForeignKey, BufferedSource> {
}
