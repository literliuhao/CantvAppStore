package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 非应用商城接口的响应
 *
 * @param <T>
 * @author 陈建
 */
public class ClassicResult<T> implements ResultWrapper<T> {

    /**
     * status : 200
     * info : success
     */
    @SerializedName("status")
    private int status;
    @SerializedName("info")
    private String info;
    @SerializedName("data")
    private T data;

    @Override
    public boolean isSuccessful() {
        return status == 200;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return info;
    }

    @Override
    public T getData() {
        return data;
    }
}
