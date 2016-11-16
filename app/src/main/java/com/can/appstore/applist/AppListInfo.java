package com.can.appstore.applist;

/**
 * Created by 4 on 2016/10/19.
 */

public class AppListInfo {
    private int id;
    private String appName;
    private String packageName;
    private String icon;
    private String size;
    private String downloadVolume;//下载量
    private String recommend;//推荐语
    private int versionCode;//版本号
    private boolean isNew;//是否是新app

    public int getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }

    public String getIcon() {
        return icon;
    }

    public String getSize() {
        return size;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDownloadVolume() {
        return downloadVolume;
    }

    public String getRecommend() {
        return recommend;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setDownloadVolume(String downloadVolume) {
        this.downloadVolume = downloadVolume;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }


}
