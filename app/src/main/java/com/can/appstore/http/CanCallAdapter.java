package com.can.appstore.http;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;

public class CanCallAdapter implements CallAdapter<CanCall<?>> {
    private final Type responseType;

    public CanCallAdapter(Type responseType) {
        this.responseType = responseType;
    }


    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public <R> CanCall<R> adapt(Call<R> call) {
        return new CanCall<>(call);
    }
}
