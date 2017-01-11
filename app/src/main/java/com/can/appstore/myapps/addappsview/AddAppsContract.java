package com.can.appstore.myapps.addappsview;


import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;

import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil;


/**
 * Created by wei on 2016/11/3.
 */

public interface AddAppsContract {
    interface Presenter extends BasePresenter {
        void startLoad();

        void addListener();

        void release();
    }

    interface View extends BaseView{

        void loadAddAppInfoSuccess(List<PackageUtil.AppInfo> infoList);

        void showCanSelectCount(int cansel, int alreadyshow);

        void saveSelectInfo(List<PackageUtil.AppInfo> list);

        void setAlreadySelectApp(int[] alreadySelect);
    }

}
