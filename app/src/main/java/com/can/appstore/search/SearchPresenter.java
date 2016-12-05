package com.can.appstore.search;

import android.content.Context;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.PopularWord;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.dataeye.sdk.api.app.channel.DCResource;
import com.dataeye.sdk.api.app.channel.DCResourceLocation;
import com.dataeye.sdk.api.app.channel.DCResourcePair;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.ToastUtils;
import retrofit2.Response;

/**
 * Created by yibh on 2016/10/12 14:39 .
 */

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View mView;
    private List<AppInfo> mAppInfoList;
    private List<AppInfo> mHotRecommendList;
    private ArrayList<DCResourcePair> mPairs = new ArrayList<>();
    private Context mContext;
    private String mSearchRecommend;

    public SearchPresenter(SearchContract.View view) {
        mView = view;
        mContext = (Context) view;
        mSearchRecommend = mContext.getString(R.string.search_recommend);
        mAppInfoList = new ArrayList<>();
    }

    /**
     * @param searCon   搜索首字母
     * @param pageIndex 第几页
     */
    @Override
    public void getSearchList(final String searCon, final int pageIndex) {
        if (!NetworkUtils.isNetworkConnected(MyApp.getContext())) {
            mView.noNetWork();
            return;
        }
        if (pageIndex == 1) {   //加载更多就不用再显示"开始搜索"
            mView.startSearch();
        }

        HttpManager.getApiService().search(searCon, pageIndex, 18).enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                ListResult<AppInfo> body = response.body();
                List<AppInfo> data = body.getData();
                //说明是刚搜索,有内容就清空
                if (pageIndex == 1 && mAppInfoList.size() > 0) {
                    mAppInfoList.clear();
                }
                if (!(data.size() > 0) && pageIndex != 1) {
                    //                    ToastUtil.toastShortTimeLimit("没有更多数据!", 5000);
                    ToastUtils.showMessageLong(MyApp.getContext(), "没有更多数据!");
                } else {
                    mAppInfoList.addAll(data);
                    mView.getAppList(mAppInfoList, pageIndex == 1 ? true : false);
                }

                if (pageIndex == 1) {
                    //统计资源搜索
                    DCResource.onSearch(searCon);
                }
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                //                String reason = errorWrapper.getReason();
                //                ToastUtil.toastShort("加载数据失败,请稍后再试!");
                mView.getAppList(null);
            }
        });

    }

    @Override
    public void getDefaultList() {

        if (!NetworkUtils.isNetworkConnected(MyApp.getContext())) {
            mView.hideLoading();
            mView.noNetWork();
            return;
        }

        //热门推荐
        HttpManager.getApiService().recommend().enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                ListResult<AppInfo> body = response.body();
                mHotRecommendList = body.getData();
                mView.getHotRecomAppList(mHotRecommendList);
                //                ToastUtil.toastShort("加载数据成功!" + body.getMessage());
                mView.hideLoading();
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                //                ToastUtil.toastShort("加载数据失败,请稍后再试!");
                ToastUtils.showMessageLong(MyApp.getContext(), "加载数据失败,请稍后再试!");
                mView.hideLoading();
            }
        });

        //大家都在搜
        HttpManager.getApiService().getHotKeywords().enqueue(new CanCallback<ListResult<PopularWord>>() {
            @Override
            public void onResponse(CanCall<ListResult<PopularWord>> call, Response<ListResult<PopularWord>> response) throws Exception {
                ListResult<PopularWord> body = response.body();
                List<PopularWord> popularWordList = body.getData();
                mView.getHotKeyList(popularWordList);
                mView.hideLoading();
            }

            @Override
            public void onFailure(CanCall<ListResult<PopularWord>> call, CanErrorWrapper errorWrapper) {
                //                ToastUtil.toastShort("加载数据失败,请稍后再试!");
                ToastUtils.showMessageLong(MyApp.getContext(), "加载数据失败,请稍后再试!");
                mView.hideLoading();
            }
        });

    }

    /**
     * 统计搜索热门推荐的曝光次数
     */
    public void statisticsExposure() {
        if (mPairs.size() == 0 && mHotRecommendList != null) {
            for (int i = 0; i < mHotRecommendList.size(); i++) {
                DCResourcePair pair = DCResourcePair.newBuilder().setResourceLocationId(mSearchRecommend + (i + 1)).
                        setResourceId(mHotRecommendList.get(i).getName()).build();
                mPairs.add(pair);
            }
        }
        DCResourceLocation.onBatchShow(mPairs);
    }

}
