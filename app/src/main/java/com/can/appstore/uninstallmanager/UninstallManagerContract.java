package com.can.appstore.uninstallmanager;

import com.can.appstore.appdetail.AppInfo;

import java.util.List;

/**
 * Created by JasonF on 2016/10/17.
 */

public interface UninstallManagerContract {

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

        void showCurStorageProgress(int progress, String storage);

        void refreshSelectCount(int count);
    }
}
