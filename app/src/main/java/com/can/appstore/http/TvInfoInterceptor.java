package com.can.appstore.http;

import com.can.appstore.AppConstants;
import com.can.appstore.entity.ClassicResult;
import com.can.appstore.entity.TvInfoModel;

import java.io.IOException;

import cn.can.tvlib.utils.NetworkUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TvInfoInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        if (!TvInfoModel.getInstance().alreadyInit() &&
                !url.startsWith(AppConstants.TMS_GET_MAC_URL)) {
            retrofit2.Response<ClassicResult<TvInfoModel>> response = HttpManager.getTmsService().getTvInfo(NetworkUtils.getMac()).execute();
            if (response.isSuccessful()) {
                ClassicResult<TvInfoModel> body = response.body();
                if (body != null && body.isSuccessful()) {
                    TvInfoModel.getInstance().copyFrom(body.getData());
                }
            }
        }
        return chain.proceed(request);
    }
}
