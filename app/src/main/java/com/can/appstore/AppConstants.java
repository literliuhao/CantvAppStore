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

    public static final String DATAEYE_APPID = "C79A5CBE3FDFBDA429B11ED6E79FDD19A";
    public static final String DATAEYE_CHANNELID = "Can_W55";
    //页面埋点的事件ID
    public static final String RESOURCES_POSITION = "resources_position"; //资源位
    public static final String HOME_PAGE = "homepage"; //首页
    public static final String HOME_CHARTS = "home_charts"; //首页排行
    public static final String NEWS_LIST = "news_list";//消息列表页
    public static final String APP_LIST = "app_list";//应用列表页
    public static final String APP_DETAIL = "app_detail";//应用详情页
    public static final String CHARTS = "charts";//排行榜列表页
    public static final String RESEARCH_PAGE = "research_page";//搜索页面
    public static final String TOPIC_LIST = "topic_list";//专题列表页
    public static final String TOPIC_DETAIL = "topic_detail";//专题详情页
    public static final String ACTIVITY_DETAIL = "activity_detail";//活动详情页
    public static final String DOWNLOAD_MANAGE = "download_manage";//下载管理
    public static final String UPDATE_MANAGE = "update_manage";//更新管理
    public static final String UNINSTALL_MANAGE = "uninstall_manage";//卸载管理
    public static final String PACKAGE_MANAGE = "package_manage";//安装包管理
    public static final String AD_PAGE = "ad_page";//广告页面

}
