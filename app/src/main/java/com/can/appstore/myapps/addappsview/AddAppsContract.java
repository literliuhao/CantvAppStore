package com.can.appstore.myapps.addappsview;


import java.util.List;

import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

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

        void loadAddAppInfoSuccess(List<PackageUtil.AppInfo> infoList);

        void showCanSelectCount(int cansel, int alreadyshow);

        void saveSelectInfo(List<AppInfo> list);

        void setAlreadySelectApp(int[] alreadySelect);
    }

}
