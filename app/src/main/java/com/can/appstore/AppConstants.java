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

    public static final String BASE_URL = "http://appstore.can.cibntv.net/api/";
    public static final String AMS_BASE_URL = "http://ams.can.cibntv.net";
//    public static final String AMS_BASE_URL = "http://172.16.11.32:7006";//广告测试，正式发布删除此行
    public static final String AD_REPORT_URL = AMS_BASE_URL + "/api/ad/addadreport";
    public static final String AD_COMMON_GET_URL = AMS_BASE_URL + "/api/ad/getad";
    public static final String TMS_GET_MAC_URL = "http://tms.can.cibntv.net/api/sync/getInfoByMac";


    public static final String SYSTEM_PROVIDER_KEY_CHANNELID = "setting_channelid_key";
    public static final String SYSTEM_PROVIDER_KEY_MODEL = "setting_model";

    public final static List<String> PRE_APPS = new ArrayList<String>() {
        {
            add("com.cantv.wechatphoto");
            add("com.cantv.media");
            add("com.tvkou.linker");
            add("com.tvm.suntv.news.client.activity");
            add("com.cantv.market");
        }
    };
    //正式发包的appid : C6D2811D3790106B15A89CE9B5C477B2B
    public static final String DATAEYE_APPID = "C6D2811D3790106B15A89CE9B5C477B2B";//正式appid  发布正式上线包时需要将此打开
   // public static final String DATAEYE_APPID = "C79A5CBE3FDFBDA429B11ED6E79FDD19A";//测试appid
    public static final String DATAEYE_DEFAULT_CHANNEL = "C42S-10002";
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
