package com.can.appstore.uninstallmanager;

import com.can.appstore.appdetail.AppDetailContract;
import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;

import java.util.List;

import cn.can.tvlib.utils.PackageUtil;

/**
 * Created by JasonF on 2016/10/17.
 */

public interface UninstallManagerContract {

    interface Presenter extends BasePresenter {
        void startLoad();

        void addListener();
    }

    interface View extends BaseView<AppDetailContract.Presenter> {
        void loadAllAppInfoSuccess(List<PackageUtil.AppInfo> infoList);

        void showCurStorageProgress(int progress, String storage);

        void refreshSelectCount(int count);
    }
}