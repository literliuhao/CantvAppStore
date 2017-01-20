package com.can.appstore.http;

import com.can.appstore.AppConstants;
import com.can.appstore.MyApp;
import com.can.appstore.api.AdService;
import com.can.appstore.api.ApiService;
import com.can.appstore.api.TmsService;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Navigation;
import com.can.appstore.index.model.DataUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManager {
    private final ApiService apiService;
    private final AdService adService;
    private final TmsService tmsService;

    private HttpManager() {
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new TvInfoInterceptor())
                .addInterceptor(new AdSystemInterceptor())
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(2,TimeUnit.SECONDS)
                .addInterceptor(new RequestParamsInterceptor())
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
                        try {
                            ListResult<Navigation> listResult = new Gson().fromJson(new String(data), new TypeToken<ListResult<Navigation>>() {
                            }.getType());
                            if (listResult.getStatus() == 0 && !listResult.getData().isEmpty()) {
                                DataUtils.getInstance(MyApp.getContext()).setCache(new String(data));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
//                .addInterceptor(httpLoggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CanCallAdapterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        adService = retrofit.create(AdService.class);
        tmsService = retrofit.create(TmsService.class);
    }

    public static ApiService getApiService() {
        return HttpManagerHolder.HTTP_MANAGER.apiService;
    }

    public static AdService getAdService() {
        return HttpManagerHolder.HTTP_MANAGER.adService;
    }

    public static TmsService getTmsService() {
        return HttpManagerHolder.HTTP_MANAGER.tmsService;
    }

    private static class HttpManagerHolder {
        static final HttpManager HTTP_MANAGER = new HttpManager();
    }
}
