package com.can.appstore.http;

import com.can.appstore.AppConstants;
import com.can.appstore.api.ApiService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManager {
    private final ApiService apiService;

    private HttpManager() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestParamsInterceptor())
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
