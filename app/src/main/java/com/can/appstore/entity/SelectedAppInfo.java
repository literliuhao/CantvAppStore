package com.can.appstore.entity;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * ================================================
 * 作    者：JasonF
 * 版    本：
 * 创建日期：2017/1/11
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class SelectedAppInfo extends AppInfo implements CanRecyclerViewAdapter.Selectable {
    private boolean isSelected;

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean b) {
        isSelected = b;
    }

    public static SelectedAppInfo wrap(AppInfo appInfo) {
        SelectedAppInfo selectedAppInfo = new SelectedAppInfo();
        selectedAppInfo.apkPath = appInfo.apkPath;
        selectedAppInfo.appName = appInfo.appName;
        selectedAppInfo.appIcon = appInfo.appIcon;
        selectedAppInfo.isSystemApp = appInfo.isSystemApp;
        selectedAppInfo.packageName = appInfo.packageName;
        selectedAppInfo.installtime = appInfo.installtime;
        selectedAppInfo.installPath = appInfo.installPath;
        selectedAppInfo.size = appInfo.size;
        selectedAppInfo.versionCode = appInfo.versionCode;
        selectedAppInfo.versionName = appInfo.versionName;
        return selectedAppInfo;
    }
}
