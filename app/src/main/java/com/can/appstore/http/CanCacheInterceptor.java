package com.can.appstore.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 自定义缓存处理拦截器，该类会读取整个响应数据，不适用于大量数据情况。
 *
 * @author 陈建
 */
public abstract class CanCacheInterceptor implements Interceptor {
    @Override
    public final Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        okhttp3.Response proceed = chain.proceed(request);
        if (proceed.code() == 200 && intercept(request)) {
            ResponseBody rawBody = proceed.body();
            byte[] data = rawBody.bytes();
            ResponseBody responseBody = ResponseBody.create(rawBody.contentType(), data);
            proceed = proceed.newBuilder().body(responseBody).build();
            cache(data);
        }
        return proceed;
    }

    /**
     * 是否处理缓存响应数据，如果需要缓存，则返回 <code>true</code>
     *
     * @param request
     * @return 返回<code>true</code>，如果需要缓存数据
     */
    protected abstract boolean intercept(Request request);

    /**
     * 缓存响应数据
     *
     * @param data 响应体
     */
    protected abstract void cache(byte[] data) throws IOException;
}
