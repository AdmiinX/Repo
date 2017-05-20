package com.admiinx.repo;

/**
 * Project MVP
 * Created by admin-x on 4/30/17.
 */

public class Result<T> {
    private T data;
    private Throwable throwable;

    public Result() {
    }

    public Result(T data, Throwable throwable) {
        this.data = data;
        this.throwable = throwable;
    }

    public Result(T data) {
        this(data, null);
    }

    public Result(Throwable throwable) {
        this(null, throwable);
    }

    public boolean isSuccess() {
        return throwable == null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result<?> result = (Result<?>) o;

        if (data != null ? !data.equals(result.data) : result.data != null) return false;
        return throwable != null ? throwable.equals(result.throwable) : result.throwable == null;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "data=" + data +
                ", throwable=" + throwable +
                '}';
    }
}
