package com.can.appstore.homerank;

import com.can.appstore.MyApp;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Ranking;
import com.can.appstore.homerank.utils.GsonUtil;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.common.network.NetworkUtil;
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
        if (!NetworkUtil.isNetworkConnected(MyApp.getContext())) {
//            mView.noNetWork();
            mView.getData(getDefaultList());
            return;
        }
        mView.startLoading();
        HttpManager.getApiService().getAppsRankingList().enqueue(new CanCallback<ListResult<Ranking>>() {
            @Override
            public void onResponse(CanCall<ListResult<Ranking>> call, Response<ListResult<Ranking>> response) throws Exception {
                ListResult<Ranking> body = response.body();
                if(null != body){
                    List<Ranking> data = body.getData();
                    if (data != null && data.size() > 0) {
                        mView.getData(data);
                    } else {
                        mView.getData(getDefaultList());
                    }
                }
            }

            @Override
            public void onFailure(CanCall<ListResult<Ranking>> call, CanErrorWrapper errorWrapper) {
                mView.getData(getDefaultList());
            }
        });
    }

    /**
     * 断网时的默认数据
     *
     * @return
     */
    public List getDefaultList() {
        List defaultList = new ArrayList<>();
        Ranking ranking = GsonUtil.jsonToBean(mDefaultJson, Ranking.class);
        for (int i = 0; i < 4; i++) {
            defaultList.add(ranking);
        }
        return defaultList;
    }

    public String mDefaultJson = " {\n" +
            "        \"id\": \"8\",\n" +
            "        \"name\": \"应用\",\n" +
            "        \"data\": [\n" +
            "            {\n" +
            "                \"id\": \"113\",\n" +
            "                \"name\": \"往以前\",\n" +
            "                \"icon\": \"http://172.16.11.32:8010/upload/Application/2016-11-01/58183f7c02787.png\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"35\",\n" +
            "                \"name\": \"哈哈库\",\n" +
            "                \"icon\": \"http://172.16.11.32:8010/upload/ss\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"3\",\n" +
            "                \"name\": \"炸金花\",\n" +
            "                \"icon\": \"http://172.16.11.32:8010/upload/ss\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"123\",\n" +
            "                \"name\": \"在苹果\",\n" +
            "                \"icon\": \"http://172.16.11.32:8010/upload/Application/2016-11-01/58183f7c02787.png\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"122\",\n" +
            "                \"name\": \"考生共同\",\n" +
            "                \"icon\": \"http://172.16.11.32:8010/upload/Application/2016-11-01/58183f7c02787.png\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }";

}
