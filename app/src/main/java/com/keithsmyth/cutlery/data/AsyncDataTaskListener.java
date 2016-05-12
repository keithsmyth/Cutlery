package com.keithsmyth.cutlery.data;

public interface AsyncDataTaskListener<T> {

    void onSuccess(T model);

    void onError(Exception e);
}
