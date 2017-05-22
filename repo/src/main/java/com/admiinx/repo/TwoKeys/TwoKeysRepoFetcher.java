package com.admiinx.repo.TwoKeys;

import io.reactivex.Maybe;
import okio.BufferedSource;

import java.net.ConnectException;

/**
 * Represents a basic fetch function to get the {@code Value} associates with {@code Key} as {@link BufferedSource}
 *
 * @param <PrimaryKey> the PrimaryKey type
 * @param <ForeignKey> the ForeignKey type
 */
public interface TwoKeysRepoFetcher<PrimaryKey, ForeignKey> {

    /**
     * Returns {@link Maybe} emit the fetched {@code Value} as {@link BufferedSource},
     * or {@link Maybe#empty()} if there is no {@code Value} for {@code key}.
     * if any connection error occurred return {@link Maybe#error(Throwable)} of {@link ConnectException}
     *
     * @param primaryKey uses to fetch the {@code Value}
     * @param foreignKey uses to fetch the {@code Value}
     * @return {@link Maybe} emit {@link BufferedSource} if {@code Value} Present otherwise complete
     */
    Maybe<BufferedSource> fetch(PrimaryKey primaryKey, ForeignKey foreignKey);
}
