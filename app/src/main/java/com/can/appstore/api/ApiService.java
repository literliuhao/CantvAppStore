package com.can.appstore.api;

import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.AppInfoContainer;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.MessageContainer;
import com.can.appstore.entity.Navigation;
import com.can.appstore.entity.PopularWord;
import com.can.appstore.entity.Ranking;
import com.can.appstore.entity.Result;
import com.can.appstore.entity.SpecialTopic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {

    /**
     * 专题列表
     */
    @GET
    Call<ListResult<SpecialTopic>> getSpecialTopics();

    /**
     * 专题详情
     */
    // TODO: 添加参数
    @GET
    Call<Result<SpecialTopic>> getSpecialTopic(@Query("specialId") String specialId);

    // 活动接口

    /**
     * 排行榜
     */
    @GET
    Call<ListResult<Ranking>> getAppsRankingList();

    /**
     * 排行榜列表
     */
    @GET
    Call<Result<AppInfoContainer>> getAppsRanking(String rankingId, int page, int pageSize);

    /**
     * 应用列表
     */
    // TODO: 参数
    @GET
    Call<Result<AppInfoContainer>> getAppinfos(String topicId, int page, int pageSize);

    /**
     * 获取应用详情
     *
     * @param appId 应用的id
     */
    @GET
    Call<Result<AppInfo>> getAppInfo(@Query("appid") String appId);

    // TODO: 搜索页热门推荐,参数
    Call<ListResult<AppInfo>> recommend();

    /**
     * 获取搜索页热词
     */
    @GET
    Call<ListResult<PopularWord>> getHotKeywords();

    /**
     * 搜索接口
     *
     * @param key 搜索关键字
     */
    @GET
    Call<ListResult<AppInfo>> search(@Query("key") String key);

    /**
     * 获取隐藏应用列表
     */
    @GET
    Call<ListResult<String>> getHiddenApps();

    /**
     * 检查更新
     *
     * @param apps 待检测APP列表
     */
    @POST
    Call<ListResult<AppInfo>> checkUpdate(@Body List<AppInfo> apps);

    /**
     * 自升级检查
     */
    @GET
    Call<Result<AppInfo>> checkUpdateSelf();

    /**
     * 首页导航
     */
    @GET
    Call<Result<Navigation>> getNavigations();

    /**
     * 获取消息
     */
    @GET
    Call<Result<MessageContainer>> getMessages();
}
