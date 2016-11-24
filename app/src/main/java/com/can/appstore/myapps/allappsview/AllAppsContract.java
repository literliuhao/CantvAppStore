package com.can.appstore.myapps.allappsview;


import java.util.List;

import cn.can.tvlib.utils.PackageUtil;

/**
 * Created by wei on 2016/11/8.
 */

public interface AllAppsContract{
    interface Presenter {
        void startLoad();

        void addListener();

        void release();
    }

    interface View {
        void showLoading();

        void hideLoading();

        void showUninstallDialog(PackageUtil.AppInfo app);

        void loadAllAppInfoSuccess(List<PackageUtil.AppInfo> infoList);

        void uninstallLastPosition(int position);//卸载最后一个位置,让刷新后的最后一个位置请求焦点

    }

}
