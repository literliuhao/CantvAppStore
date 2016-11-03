package com.can.appstore.http;

import com.can.appstore.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // TODO 完善参数
        HttpUrl url = request.url()
                .newBuilder()
                .addQueryParameter("channelId", "1")
                .addQueryParameter("internalModel", "neibu")
                .addQueryParameter("commercialModel", "shangyong")
                .addQueryParameter("mac", "1:2:3")
                .addQueryParameter("versionId", "1.0")
                .build();
        request = request.newBuilder().url(url)
                .build();
        return chain.proceed(request);
    }
}
