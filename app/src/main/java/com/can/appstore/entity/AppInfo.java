package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.can.tvlib.utils.CollectionUtil;

public class AppInfo {

    /**
     * id : 1
     * name : 欢乐斗地主
     * packageName :
     * versionName : v1.0.1
     * versionCode : 101
     * icon : 图标url
     * sizeStr : 10MB
     * sise : 1024000
     * md5 :
     * description : 推荐语
     * downloadCount : 100+
     * url : 下载地址
     * marker : ======图标或类型======
     * updateTime : 2016-09-10
     * controls : ["支持设备1","支持设备2"]
     * thumbs : ["截图1","截图2"]
     * recommend : []
     * about : 简介
     * updateLog : 更新说明
     * developer : 开发者
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("packageName")
    private String packageName;
    @SerializedName("versionName")
    private String versionName;
    @SerializedName("versionCode")
    private int versionCode;
    @SerializedName("icon")
    private String icon;
    @SerializedName("sizeStr")
    private String sizeStr;
    @SerializedName("sise")
    private int sise;
    @SerializedName("md5")
    private String md5;
    @SerializedName("description")
    private String description;
    @SerializedName("downloadCount")
    private String downloadCount;
    @SerializedName("url")
    private String url;
    @SerializedName("marker")
    private String marker;
    @SerializedName("updateTime")
    private String updateTime;
    @SerializedName("about")
    private String about;
    @SerializedName("updateLog")
    private String updateLog;
    @SerializedName("developer")
    private String developer;
    @SerializedName("controls")
    private List<String> controls;
    @SerializedName("thumbs")
    private List<String> thumbs;
    @SerializedName("recommend")
    private List<AppInfo> recommend;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSizeStr() {
        return sizeStr;
    }

    public void setSizeStr(String sizeStr) {
        this.sizeStr = sizeStr;
    }

    public int getSise() {
        return sise;
    }

    public void setSise(int sise) {
        this.sise = sise;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public List<String> getControls() {
        return CollectionUtil.emptyIfNull(controls);
    }

    public void setControls(List<String> controls) {
        this.controls = controls;
    }

    public List<String> getThumbs() {
        return CollectionUtil.emptyIfNull(thumbs);
    }

    public void setThumbs(List<String> thumbs) {
        this.thumbs = thumbs;
    }

    public List<AppInfo> getRecommend() {
        return CollectionUtil.emptyIfNull(recommend);
    }

    public void setRecommend(List<AppInfo> recommend) {
        this.recommend = recommend;
    }
}
