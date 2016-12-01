package com.can.appstore;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：定义常量
 * 修订历史：
 * ================================================
 */
public class AppConstants {
    public static final String BASE_URL = BuildConfig.DEBUG ? "http://172.16.11.32:8011/api/" : "http://appstore.can.cibntv.net/api/";
    public final static List<String> PRE_APPS = new ArrayList<String>(){
        {
            add("com.cantv.wechatphoto");
            add("com.cantv.media");
            add("com.tvkou.linker");
            add("com.tvm.suntv.news.client.activity");
            add("com.cantv.market");
        }
    };
}
