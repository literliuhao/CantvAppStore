package com.can.appstore.http;

import com.can.appstore.AppConstants;
import com.can.appstore.entity.TvInfoModel;

import java.io.IOException;

import cn.can.tvlib.utils.MD5Util;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class AdSystemInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();
        String urlStr = url.toString();
        // 不处理非广告API
        if (!urlStr.startsWith(AppConstants.AMS_BASE_URL)) {
            return chain.proceed(request);
        }

        if (urlStr.startsWith(AppConstants.AD_COMMON_GET_URL)) {
            // 重新对参数赋值
            url = url.newBuilder()
                    .setQueryParameter("channel", TvInfoModel.getInstance().getChannelId())
                    .setQueryParameter("model", TvInfoModel.getInstance().getModelName())
                    .build();
            request = request.newBuilder().url(url).build();
        } else if (urlStr.startsWith(AppConstants.AD_REPORT_URL)) { // 为广告上报添加请求头
            String timeSpan = String.valueOf(System.currentTimeMillis() / 1000);
            String checkCode = null;
            try {
                checkCode = MD5Util.encode(timeSpan + "#$%^&YHYJKQCANZ");
            } catch (Exception ignore) {
                // never
            }
            final RequestBody body = request.body();
            request = request.newBuilder()
                    .url(url)
                    .addHeader("timespan", timeSpan)
                    .addHeader("checkcode", checkCode)
                    .post(new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return body.contentType();
                        }

                        @Override
                        public long contentLength() throws IOException {
                            return body.contentLength() + 2;
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            sink.writeByte('[');

                            body.writeTo(sink);

                            sink.writeByte(']');
                        }
                    })
                    .build();
        }

        return chain.proceed(request);
    }
}
