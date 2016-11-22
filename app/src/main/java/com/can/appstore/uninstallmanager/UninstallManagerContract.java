package com.can.appstore.uninstallmanager;

import android.support.v4.app.LoaderManager;
import android.text.SpannableStringBuilder;

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
        void startLoad(LoaderManager loaderManager);

        void addListener();

        void onItemFocus(int position);
    }

    interface View extends BaseView<AppDetailContract.Presenter> {
        void loadAllAppInfoSuccess(List<PackageUtil.AppInfo> infoList);

        void showCurStorageProgress(int progress, String storage);

        void refreshSelectCount(int count);

        void uninstallLastPosition(int position);//卸载最后一个位置,让刷新后的最后一个位置请求焦点

        void refreshRows(SpannableStringBuilder rows);
    }
}
