/*
package com.can.appstore.myapps.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

*/
/**
 * Created by wei on 2016/10/11.
 *//*


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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AppInfo appInfo = (AppInfo) o;

        if (versionCode != appInfo.versionCode)
            return false;
        if (isSystemApp != appInfo.isSystemApp)
            return false;
        if (size != appInfo.size)
            return false;
        if (installTime != appInfo.installTime)
            return false;
        if (appName != null ? !appName.equals(appInfo.appName) : appInfo.appName != null)
            return false;
        if (packageName != null ? !packageName.equals(appInfo.packageName) : appInfo.packageName != null)
            return false;
        if (versionName != null ? !versionName.equals(appInfo.versionName) : appInfo.versionName != null)
            return false;
        if (installApkpath != null ? !installApkpath.equals(appInfo.installApkpath) : appInfo.installApkpath != null)
            return false;
        return Apkpath != null ? Apkpath.equals(appInfo.Apkpath) : appInfo.Apkpath == null;

    }

    @Override
    public int hashCode() {
        int result = appName != null ? appName.hashCode() : 0;
        result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
        result = 31 * result + (versionName != null ? versionName.hashCode() : 0);
        result = 31 * result + versionCode;
        result = 31 * result + (isSystemApp ? 1 : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (installApkpath != null ? installApkpath.hashCode() : 0);
        result = 31 * result + (Apkpath != null ? Apkpath.hashCode() : 0);
        result = 31 * result + (int) (installTime ^ (installTime >>> 32));
        return result;
    }
}
*/
