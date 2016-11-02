package com.can.appstore.special_detail.bean;

/**
 * Created by atang on 2016/10/24.
 */

public class AppDetail {

    private String appId;
    private String appName;
    private String appIcon;
    private String appSize;
    private String appDownloadNum;

    public AppDetail(String appId, String appName, String appIcon) {
        this.appId = appId;
        this.appName = appName;
        this.appIcon = appIcon;
    }

    @Override
    public String toString() {
        return "AppDetail{" +
                "appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", appIcon='" + appIcon + '\'' +
                '}';
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public String getAppDownloadNum() {
        return appDownloadNum;
    }

    public void setAppDownloadNum(String appDownloadNum) {
        this.appDownloadNum = appDownloadNum;
    }
}
