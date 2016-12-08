package com.can.appstore.http;

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
                    // Test -->
//                    .addQueryParameter("channelId", "12")
//                    .addQueryParameter("internalModel", "neibu")
//                    .addQueryParameter("model", "f66")
//                    .addQueryParameter("versionId", "1")
                    // <-- Test
                    .addQueryParameter("channelId", TvInfoModel.getInstance().getChannelId())
                    .addQueryParameter("internalModel", TvInfoModel.getInstance().getInternalmodelName())
                    .addQueryParameter("model", TvInfoModel.getInstance().getModelName())
                    .addQueryParameter("versionId", String.valueOf(BuildConfig.VERSION_CODE))
                    .addQueryParameter("mac", NetworkUtils.getMac())
                    .build();
            request = request.newBuilder().url(url)
                    .build();
        }
        return chain.proceed(request);
    }
}
