package com.can.appstore.uninstallmanager;

import java.util.List;

import cn.can.tvlib.utils.PackageUtil;

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

        void loadAllAppInfoSuccess(List<PackageUtil.AppInfo> infoList);

        void showCurStorageProgress(int progress, String storage);

        void refreshSelectCount(int count);
    }
}
