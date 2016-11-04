package com.can.appstore.appdetail;

import android.graphics.drawable.Drawable;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by JasonF on 2016/10/13.
 */

public class AppInfo implements CanRecyclerViewAdapter.Selectable {
    public String appName = "";
    public String packageName = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable appIcon = null;
    public boolean isSystemApp = false;
    public long size = 0;
    public String installApkpath = "";   //已经安装的apk文件的路径  在data/app下有   .apk文件
    public String Apkpath = "";  // 所有的apk文件的路径
    public boolean isSelect = false;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getInstallApkpath() {
        return installApkpath;
    }

    public void setInstallApkpath(String installApkpath) {
        this.installApkpath = installApkpath;
    }

    public String getApkpath() {
        return Apkpath;
    }

    public void setApkpath(String apkpath) {
        Apkpath = apkpath;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", appIcon=" + appIcon +
                ", isSystemApp=" + isSystemApp +
                ", size=" + size +
                ", installApkpath='" + installApkpath + '\'' +
                ", Apkpath='" + Apkpath + '\'' +
                ", isSelect=" + isSelect +
                '}';
    }

    @Override
    public boolean isSelected() {
        return isSelect;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelect = selected;
    }
}
