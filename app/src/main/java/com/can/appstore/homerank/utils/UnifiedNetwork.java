package com.can.appstore.homerank.utils;

import com.can.appstore.MyApp;
import com.can.appstore.api.ApiService;
import com.can.appstore.http.HttpManager;

import cn.can.tvlib.utils.NetworkUtils;

/**
 * 统一处理没有网络的情况
 * Created by yibh on 2016/11/18.
 */

public class UnifiedNetwork {
    public static ApiService getHttp(){
        if(NetworkUtils.isNetworkConnected(MyApp.getContext())){
            return HttpManager.getApiService();
        }else {
            return null;
        }
    }
}
