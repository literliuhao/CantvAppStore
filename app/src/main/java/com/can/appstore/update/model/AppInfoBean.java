package com.can.appstore.update.model;

import android.graphics.drawable.Drawable;

/**
 * Created by shenpx on 2016/10/12 0012.
 * app信息bean
 */

public class AppInfoBean {

    private String packageName;
    private String appName;
    private String iconUrl;//图标，url
    private String appSize;
    private String versionCode;
    private String downloadUrl;
    private int progress;
    private boolean install;//是否已安装
    private String versionName;
    private Drawable icon;//图标，删除后期
    private String fliePath;//路径
    private boolean isInstalling;//是否开始安装

    public boolean getIsInstalling() {
        return isInstalling;
    }

    public void setInstalling(boolean installing) {
        isInstalling = installing;
    }

    public String getFliePath() {
        return fliePath;
    }

    public void setFliePath(String fliePath) {
        this.fliePath = fliePath;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean getInstall() {
        return install;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "AppInfoBean{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", appSize='" + appSize + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", progress_updateapp=" + progress +
                '}';
    }
}
