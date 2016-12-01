package com.can.appstore.http;

import java.io.IOException;

import cn.can.tvlib.utils.NetworkUtils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // TODO 完善参数
//        HttpUrl url = request.url()
//                .newBuilder()
//                .addQueryParameter("channelId", "12")
//                .addQueryParameter("internalModel", "neibu")
//                .addQueryParameter("commercialModel", "rrrr")
//                .addQueryParameter("mac", "1:2:3")
//                .addQueryParameter("versionId", "1.0")
//                .build();
//        request = request.newBuilder().url(url)
//                .build();
        HttpUrl url = request.url()
                .newBuilder()
                .addQueryParameter("channelId", "12")
                .addQueryParameter("internalModel", "1")
                .addQueryParameter("commercialModel", "f55")
                .addQueryParameter("mac", NetworkUtils.getMac())
                .addQueryParameter("versionId", "1")
                .build();
        request = request.newBuilder().url(url)
                .build();
        return chain.proceed(request);
    }
}
