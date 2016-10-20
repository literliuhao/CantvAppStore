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
//                .addQueryParameter("channelId", "")
//                .addQueryParameter("internalModel", "")
//                .addQueryParameter("commercialModel", "")
//                .addQueryParameter("mac", "")
                .addQueryParameter("versionId", BuildConfig.VERSION_NAME)
                .build();
        request = request.newBuilder().url(url)
                .build();
        return chain.proceed(request);
    }
}
