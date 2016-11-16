package com.can.appstore.search;

import android.os.Handler;

import com.can.appstore.search.bean.DefaultApp;
import com.can.appstore.search.bean.SearchApp;

import java.util.ArrayList;
import java.util.List;

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List list = simulatedAppList(searCon);
                mView.getAppList(list);
            }
        }, 2000);
    }

    @Override
    public void getDefaultList() {
        List<DefaultApp> defaultApps = new ArrayList<>();
        for (int i = 0; i < 8; i++) {

            defaultApps.add(new DefaultApp("默认数据" + i, "mr" + i));
        }
        ArrayList<SearchApp> appList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            appList.add(new SearchApp("热门App" + i, "rm" + i));
        }
        mView.getDefaultList(defaultApps, appList);
    }

    public List simulatedAppList(String con) {
        ArrayList<SearchApp> appList = new ArrayList<>();
        if (con.equalsIgnoreCase("xx")) {
            return appList;
        }

        for (int i = 0; i < 50; i++) {
            appList.add(new SearchApp(con + "_App" + i+"这是测试数据", con + "_" + i));
        }
        return appList;
    }

}
