package com.can.appstore.http;

import android.content.Context;
import android.content.SharedPreferences;

import com.can.appstore.AppConstants;
import com.can.appstore.BuildConfig;
import com.can.appstore.entity.TvInfoModel;

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
        String urlStr = request.url().toString();
        // 为应用商城接口统一添加参数
        if (urlStr.startsWith(AppConstants.BASE_URL)) {
            HttpUrl url = request.url()
                    .newBuilder()
                    .addQueryParameter("channelId", TvInfoModel.getInstance().getChannelId())
                    .addQueryParameter("internalModel", TvInfoModel.getInstance().getInternalmodelName())
                    .addQueryParameter("model", TvInfoModel.getInstance().getModelName())
                    .addQueryParameter("mac", NetworkUtils.getMac())
                    .addQueryParameter("versionId", String.valueOf(BuildConfig.VERSION_CODE))
                    .build();
            request = request.newBuilder().url(url)
                    .build();
        }
        return chain.proceed(request);
    }
}
