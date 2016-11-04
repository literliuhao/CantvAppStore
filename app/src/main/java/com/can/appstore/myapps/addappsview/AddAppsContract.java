package com.can.appstore.myapps.addappsview;


import com.can.appstore.myapps.model.AppInfo;

import java.util.List;

/**
 * Created by wei on 2016/11/3.
 */

public interface AddAppsContract {
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

        void showCanSelectCount(int cansel, int alreadyshow);

        void saveSelectInfo(List<AppInfo>  list);
    }



}
