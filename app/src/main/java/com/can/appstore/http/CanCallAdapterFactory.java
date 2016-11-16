package com.can.appstore.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class CanCallAdapterFactory extends CallAdapter.Factory {
    private CanCallAdapterFactory() {

    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        // 返回值必须是CanCall并且带有泛型
        if (rawType == CanCall.class && returnType instanceof ParameterizedType) {
            Type callReturnType = getParameterUpperBound(0, (ParameterizedType) returnType);
            return new CanCallAdapter(callReturnType);
        }
        return null;
    }

    public static CanCallAdapterFactory create() {
        return new CanCallAdapterFactory();
    }
}
