package com.admiinx.repo.internal;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by admin-x on 5/8/17.
 */
public final class Utils {

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex string.
     *
     * @param data Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5(final String data) {
        try {
            return DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(data.getBytes("UTF-8"))).toLowerCase();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }
        return null;
    }
}
