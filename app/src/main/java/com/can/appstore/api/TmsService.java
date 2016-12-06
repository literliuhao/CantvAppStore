package com.can.appstore.api;

import com.can.appstore.AppConstants;
import com.can.appstore.entity.ClassicResult;
import com.can.appstore.entity.TvInfoModel;
import com.can.appstore.http.CanCall;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmsService {
    /**
     * 获取机型信息
     *
     * @return
     */
    @GET(AppConstants.TMS_GET_MAC_URL)
    CanCall<ClassicResult<TvInfoModel>> getTvInfo(@Query("mac") String mac);
}
