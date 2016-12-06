package com.can.appstore.api;

import com.can.appstore.AppConstants;
import com.can.appstore.entity.Ad;
import com.can.appstore.entity.AdReportParam;
import com.can.appstore.entity.CommonAdParam;
import com.can.appstore.entity.ClassicResult;
import com.can.appstore.http.CanCall;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface AdService {
    /**
     * @param params 请求参数，通过 {@link CommonAdParam#toMap()} 获取
     * @return
     */
    @GET(AppConstants.AMS_BASE_URL + AppConstants.AD_COMMON_GET_URL_PATH)
    CanCall<ClassicResult<List<Ad>>> getCommonAd(@QueryMap Map<String, String> params);

    /**
     * @param paramEntity 请求参数
     * @return
     */
    @POST(AppConstants.AMS_BASE_URL + AppConstants.AD_REPORT_URL_PATH)
    CanCall<ClassicResult> report(@Body AdReportParam paramEntity);
}
