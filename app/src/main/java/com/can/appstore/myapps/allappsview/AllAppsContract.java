package com.can.appstore.myapps.allappsview;

import com.can.appstore.myapps.model.AppInfo;

import java.util.List;

/**
 * Created by wei on 2016/11/8.
 */

public interface AllAppsContract {
    interface Presenter {
        void startLoad();

        void addListener();

        void release();
    }

    interface View {
        void showLoading();

        void hideLoading();

        void onClickHomeKey();

        void loadAllAppInfoSuccess(List<AppInfo> infoList);
    }

}
