package com.can.appstore.homerank;

import com.can.appstore.MyApp;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Ranking;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.search.ToastUtil;

import java.util.List;

import cn.can.tvlib.utils.NetworkUtils;
import retrofit2.Response;

/**
 * Created by yibh on 2016/10/17 10:41 .
 */

public class HomeRankPresenter implements HomeRankContract.Presenter {
    private HomeRankContract.View mView;

    public HomeRankPresenter(HomeRankContract.View view) {
        this.mView = view;
    }

    @Override
    public void loadingData() {
        if (!NetworkUtils.isNetworkConnected(MyApp.getContext())) {
            mView.noNetWork();
            return;
        }
        mView.startLoading();
        HttpManager.getApiService().getAppsRankingList().enqueue(new CanCallback<ListResult<Ranking>>() {
            @Override
            public void onResponse(CanCall<ListResult<Ranking>> call, Response<ListResult<Ranking>> response) throws Exception {
                ListResult<Ranking> body = response.body();
                List<Ranking> data = body.getData();
                mView.getData(data);
            }

            @Override
            public void onFailure(CanCall<ListResult<Ranking>> call, CanErrorWrapper errorWrapper) {
                ToastUtil.toastShort("加载数据失败,请稍后再试!");
            }
        });
    }

}
