package com.can.appstore.http;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import cn.can.tvlib.utils.NetworkUtils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestParamsInterceptor implements Interceptor {
    public static final String SP_NAME = "tvinfo";
    public static String KEY_CHANNEL_ID = "channelId";
    public static String KEY_INTERNAL_MODEL = "internalModel";
    public static String KEY_COMMERCIAL_MODEL = "commercialModel";
    static String CHANNEL_ID;
    static String INTERNAL_MODEL;
    static String COMMERCIAL_MODEL;
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
                .addQueryParameter("channelId", CHANNEL_ID)
                .addQueryParameter("internalModel", INTERNAL_MODEL)
                .addQueryParameter("commercialModel",COMMERCIAL_MODEL)
                .addQueryParameter("mac", NetworkUtils.getMac())
                .addQueryParameter("versionId", "1")
                .build();
        request = request.newBuilder().url(url)
                .build();
        return chain.proceed(request);
    }
    static boolean alreadyInit() {
        return CHANNEL_ID != null && INTERNAL_MODEL != null && COMMERCIAL_MODEL != null;
    }

    static boolean initWithSharedPreferences(Context context) {
        if (alreadyInit()) {
            return true;
        }

        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        RequestParamsInterceptor.CHANNEL_ID = sharedPreferences.getString(RequestParamsInterceptor.KEY_CHANNEL_ID, null);
        RequestParamsInterceptor.COMMERCIAL_MODEL = sharedPreferences.getString(RequestParamsInterceptor.KEY_COMMERCIAL_MODEL, null);
        RequestParamsInterceptor.INTERNAL_MODEL = sharedPreferences.getString(RequestParamsInterceptor.KEY_INTERNAL_MODEL, null);
        return alreadyInit();
    }

    static void initWithTvInfoHolder(Context context, TvInfoHolderWrapper.TvInfoHolder tvInfoHolder) {
        RequestParamsInterceptor.CHANNEL_ID = tvInfoHolder.getChannelId();
        RequestParamsInterceptor.COMMERCIAL_MODEL = tvInfoHolder.getModelId();
        RequestParamsInterceptor.INTERNAL_MODEL = tvInfoHolder.getInternalmodelId();

        context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                .putString(RequestParamsInterceptor.KEY_CHANNEL_ID, tvInfoHolder.getChannelId())
                .putString(RequestParamsInterceptor.KEY_COMMERCIAL_MODEL, tvInfoHolder.getModelId())
                .putString(RequestParamsInterceptor.KEY_INTERNAL_MODEL, tvInfoHolder.getInternalmodelId())
                .apply();
    }
}
