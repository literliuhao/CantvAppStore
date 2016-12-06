package com.can.appstore.entity;

public interface ResultWrapper<T> {
    boolean isSuccessful();

    int getStatus();

    String getMessage();

    T getData();
}
