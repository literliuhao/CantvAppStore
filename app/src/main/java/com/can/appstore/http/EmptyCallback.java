package com.can.appstore.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 空实现的回调接口，当不需要知道请求成功与否时使用
 *
 * @author 陈建
 */
public class EmptyCallback implements Callback<ResponseBody>, CanCallback<ResponseBody> {
    @Override
    public void onResponse(Call call, Response response) {
        //啥也不干
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        //啥也不干
    }

    @Override
    public void onResponse(CanCall<ResponseBody> call, Response<ResponseBody> response) throws Exception {
        //啥也不干
    }

    @Override
    public void onFailure(CanCall<ResponseBody> call, CanErrorWrapper errorWrapper) {
        //啥也不干
    }
}
