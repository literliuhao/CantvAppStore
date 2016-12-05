package com.can.appstore.http;

import android.content.Context;
import android.content.SharedPreferences;

import com.can.appstore.AppConstants;
import com.can.appstore.MyApp;

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
        String urlStr = request.url().toString();
        // 在请求之前先获取机型信息参数
        if (urlStr.startsWith(AppConstants.BASE_URL)) {
            if (!alreadyInit() || !initWithSharedPreferences(MyApp.getContext())) {
                retrofit2.Response<TvInfoHolderWrapper> response = HttpManager.getApiService().getTvInfo().execute();
                if (response.isSuccessful()) {
                    TvInfoHolderWrapper body = response.body();
                    if (body.getStatus() == 200) {
                        TvInfoHolderWrapper.TvInfoHolder data = body.getData();
                        RequestParamsInterceptor.initWithTvInfoHolder(MyApp.getContext(), data);
                    }
                }
            }
        }

        HttpUrl url = request.url()
                .newBuilder()
                .addQueryParameter("channelId", CHANNEL_ID)
                .addQueryParameter("internalModel", INTERNAL_MODEL)
                .addQueryParameter("commercialModel", COMMERCIAL_MODEL)
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

        if (context == null) return false;

        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        RequestParamsInterceptor.CHANNEL_ID = sharedPreferences.getString(RequestParamsInterceptor.KEY_CHANNEL_ID, null);
        RequestParamsInterceptor.COMMERCIAL_MODEL = sharedPreferences.getString(RequestParamsInterceptor.KEY_COMMERCIAL_MODEL, null);
        RequestParamsInterceptor.INTERNAL_MODEL = sharedPreferences.getString(RequestParamsInterceptor.KEY_INTERNAL_MODEL, null);
        return alreadyInit();
    }

    static void initWithTvInfoHolder(Context context, TvInfoHolderWrapper.TvInfoHolder tvInfoHolder) {
        if (context == null) return;

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
