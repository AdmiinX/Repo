package com.admiinx.repo.internal;

import okio.ByteString;

public final class Utils {

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex string.
     *
     * @param data String to digest
     * @return MD5 digest as a hex string
     */
    public static String md5(final String data) {
        return ByteString.encodeUtf8(data).md5().hex().toLowerCase();
    }
}
