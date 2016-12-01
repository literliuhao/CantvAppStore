package com.can.appstore.api;

import com.can.appstore.entity.Activity;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.AppInfoContainer;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.MessageContainer;
import com.can.appstore.entity.Navigation;
import com.can.appstore.entity.PopularWord;
import com.can.appstore.entity.Ranking;
import com.can.appstore.entity.Result;
import com.can.appstore.entity.SpecialTopic;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.TvInfoHolderWrapper;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {

    /**
     * 专题列表
     */
    @GET("special/speciallist")
    CanCall<ListResult<SpecialTopic>> getSpecialTopics(@Query("pageNumber") int page, @Query("pageSize") int pageSize);

    /**
     * 专题详情
     */
    @GET("start/specialcontent")
    CanCall<Result<SpecialTopic>> getSpecialTopic(@Query("specialId") String specialId);

    /**
     * 活动详情
     */
    @GET("start/activedetail")
    CanCall<Result<Activity>> getActivityInfo(@Query("activeId") String activeId);

    /**
     * 排行榜
     */
    @GET("start/rank")
    CanCall<ListResult<Ranking>> getAppsRankingList();

    /**
     * 排行榜列表
     */
    @GET("start/ranklist")
    CanCall<Result<AppInfoContainer>> getAppsRanking(@Query("rankId") String rankingId);

    /**
     * 应用列表
     */
    @GET("topic/topiclist")
    CanCall<Result<AppInfoContainer>> getAppinfos(@Query("topicId") String topicId, @Query("typeId") String typeId, @Query("pageNumber") int page, @Query("pageSize") int pageSize);

    /**
     * 获取应用详情
     *
     * @param appId 应用的id
     */
    @GET("application/appdetail")
    CanCall<Result<AppInfo>> getAppInfo(@Query("appId") String appId, @Query("topicId") String topicId);

    /**
     * 获取搜索页推荐
     */
    @GET("start/hotcontent")
    CanCall<ListResult<AppInfo>> recommend();

    /**
     * 获取搜索页热词
     */
    @GET("application/everyonesearch")
    CanCall<ListResult<PopularWord>> getHotKeywords();

    /**
     * 搜索接口
     *
     * @param key 搜索关键字
     */
    @GET("start/search")
    CanCall<ListResult<AppInfo>> search(@Query("key") String key,@Query("pageNumber") int pageNumber,@Query("pageSize") int pageSize);

    /**
     * 获取隐藏应用列表
     */
    @GET("application/gethideapp")
    CanCall<ListResult<String>> getHiddenApps();

    /**
     * 检查更新
     *
     * @param apps 待检测APP列表
     */
    @POST("application/updateapp")
    CanCall<ListResult<AppInfo>> checkUpdate(@Body List<AppInfo> apps);

    /**
     * 自升级检查
     */
    @GET("application/checkUpdateSelf")
    CanCall<Result<AppInfo>> checkUpdateSelf();

    /**
     * 首页导航
     */
    @GET("homepage/homepage")
    CanCall<ListResult<Navigation>> getNavigations();

    /**
     * 获取消息
     */
    @GET("application/getmessage")
    CanCall<Result<MessageContainer>> getMessages();

    /**
     * 获取机型信息
     * @return
     */
    @GET("http://tms.can.cibntv.net/api/sync/getInfoByMac")
    CanCall<TvInfoHolderWrapper> getTvInfo();
}
