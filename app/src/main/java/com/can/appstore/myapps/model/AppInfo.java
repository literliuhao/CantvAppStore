package com.can.appstore.myapps.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by wei on 2016/10/11.
 */

public class AppInfo implements Serializable{
    public String appName = "";
    public String packageName = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable appIcon = null;
    public boolean isSystemApp = false;
    public long size = 0;
    public String installApkpath = "";   //已经安装的apk文件的路径  在data/app下有   .apk文件
    public String Apkpath = "";  // 所有的apk文件的路径
    public long  installTime = 0;

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
                ", installTime=" + installTime +
                '}';
    }

    public AppInfo(String appName, Drawable appIcon) {
        this.appName = appName;
        this.appIcon = appIcon;
    }

    public AppInfo() {   }


}