package com.can.appstore.search;

import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.PopularWord;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.PromptUtils;
import retrofit2.Response;

/**
 * Created by yibh on 2016/10/12 14:39 .
 */

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View mView;
    private List<AppInfo> mAppInfoList;

    public SearchPresenter(SearchContract.View view) {
        mView = view;
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

        HttpManager.getApiService().search(searCon, pageIndex, 20).enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                ListResult<AppInfo> body = response.body();
                List<AppInfo> data = body.getData();
                //说明是刚搜索,有内容就清空
                if (pageIndex == 1 && mAppInfoList.size() > 0) {
                    mAppInfoList.clear();
                }
                if (!(data.size() > 0) && pageIndex != 1) {
                    PromptUtils.toast(MyApp.getContext(), "没有更多数据!", Toast.LENGTH_LONG);
                } else {
                    mAppInfoList.addAll(data);
                    mView.getAppList(mAppInfoList, body.getTotal(), pageIndex == 1 ? true : false);
                }
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
//                String reason = errorWrapper.getReason();
                mView.getAppList(null, 0);
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
                List<AppInfo> appInfoList = body.getData();
                //限制数量最大是8
                if (appInfoList.size() > 8) {
                    ArrayList<AppInfo> appInfos = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        appInfos.add(appInfoList.get(i));
                    }
                    mView.getHotRecomAppList(appInfos);
                } else {
                    mView.getHotRecomAppList(appInfoList);
                }
                mView.hideLoading();
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                PromptUtils.toast(MyApp.getContext(), "加载数据失败,请稍后再试!");
                mView.hideLoading();
            }
        });

        //大家都在搜
        HttpManager.getApiService().getHotKeywords().enqueue(new CanCallback<ListResult<PopularWord>>() {
            @Override
            public void onResponse(CanCall<ListResult<PopularWord>> call, Response<ListResult<PopularWord>> response) throws Exception {
                ListResult<PopularWord> body = response.body();
                List<PopularWord> popularWordList = body.getData();
                //限制数量最大是8
                if (popularWordList.size() > 8) {
                    ArrayList<PopularWord> words = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        words.add(popularWordList.get(i));
                    }
                    mView.getHotKeyList(words);
                } else {
                    mView.getHotKeyList(popularWordList);
                }
                mView.hideLoading();
            }

            @Override
            public void onFailure(CanCall<ListResult<PopularWord>> call, CanErrorWrapper errorWrapper) {
                PromptUtils.toast(MyApp.getContext(), "加载数据失败,请稍后再试!");
                mView.getHotKeyList(null);
                mView.hideLoading();
            }
        });

    }

}
