package com.can.appstore.http;

import android.util.Log;

import com.can.appstore.AppConstants;
import com.can.appstore.MyApp;
import com.can.appstore.api.ApiService;
import com.can.appstore.index.model.DataUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManager {
    private final ApiService apiService;

    private HttpManager() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestParamsInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new CanCacheInterceptor() {
                    @Override
                    protected boolean intercept(Request request) {
                        String url = request.url().toString();
                        if (url.startsWith("homepage", AppConstants.BASE_URL.length())) {
                            return true;
                        }
                        return false;
                    }
                    @Override
                    protected void cache(byte[] data) throws IOException {
                        Log.i("HttpManager","data " + new String(data));
                        DataUtils.getInstance(MyApp.getContext()).setCache(new String(data));
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CanCallAdapterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getApiService() {
        return HttpManagerHolder.HTTP_MANAGER.apiService;
    }


    private static class HttpManagerHolder {
        static final HttpManager HTTP_MANAGER = new HttpManager();
    }
}
