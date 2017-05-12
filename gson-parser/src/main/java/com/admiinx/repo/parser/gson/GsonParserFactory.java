package com.admiinx.repo.parser.gson;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by admin-x on 5/11/17.
 */
public final class GsonParserFactory {
    private GsonParserFactory() {
    }

    public static <T> GsonParser<T> create(final Type type) {
        return create(new Gson(), type);
    }

    public static <T> GsonParser<T> create(final Gson gson, final Type type) {
        return new GsonParser<>(gson, type);
    }
}
