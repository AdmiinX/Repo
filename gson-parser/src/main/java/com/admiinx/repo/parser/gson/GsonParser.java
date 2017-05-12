package com.admiinx.repo.parser.gson;

import com.admiinx.repo.RepoParser;
import com.google.gson.Gson;
import okio.BufferedSource;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by admin-x on 5/7/17.
 */
public class GsonParser<T> implements RepoParser<T> {
    private final Gson gson;
    private final Type type;

    GsonParser(final Gson gson, final Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T parse(BufferedSource source) {
        return gson.fromJson(new InputStreamReader(source.inputStream(), Charset.forName("UTF-8")), type);
    }
}
