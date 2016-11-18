package com.can.appstore.search;

import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.PopularWord;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.List;

import retrofit2.Response;

/**
 * Created by yibh on 2016/10/12 14:39 .
 */

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View mView;

    public SearchPresenter(SearchContract.View view) {
        mView = view;
    }

    @Override
    public void getSearchList(final String searCon) {
        mView.startSearch();

        HttpManager.getApiService().search(searCon).enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                ListResult<AppInfo> body = response.body();
                List<AppInfo> data = body.getData();
                mView.getAppList(data);
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                String reason = errorWrapper.getReason();
                ToastUtil.toastShort("加载数据失败,请稍后再试!" + reason);
                mView.getAppList(null);
            }
        });

    }

    @Override
    public void getDefaultList() {


        //热门推荐
        HttpManager.getApiService().recommend().enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                ListResult<AppInfo> body = response.body();
                List<AppInfo> appInfoList = body.getData();
                mView.getHotRecomAppList(appInfoList);
                ToastUtil.toastShort("加载数据成功!" + body.getMessage());
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                ToastUtil.toastShort("加载数据失败,请稍后再试!");
            }
        });

        //大家都在搜
        HttpManager.getApiService().getHotKeywords().enqueue(new CanCallback<ListResult<PopularWord>>() {
            @Override
            public void onResponse(CanCall<ListResult<PopularWord>> call, Response<ListResult<PopularWord>> response) throws Exception {
                ListResult<PopularWord> body = response.body();
                List<PopularWord> popularWordList = body.getData();
                mView.getHotKeyList(popularWordList);
            }

            @Override
            public void onFailure(CanCall<ListResult<PopularWord>> call, CanErrorWrapper errorWrapper) {
                ToastUtil.toastShort("加载数据失败,请稍后再试!");
            }
        });

    }

}
