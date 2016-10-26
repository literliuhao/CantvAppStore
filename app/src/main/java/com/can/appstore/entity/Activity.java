package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

public class Activity {

    /**
     * id :
     * url : Web	URL
     * background : 活动背景图URL
     * recommend : {"id":1,"packageName":"应用包名","versionName":"v1.0.1","versionCode":101,"sise":1024000,"md5":"","url":"下载地址"}
     * expires : 1864564
     */

    @SerializedName("id")
    private String id;
    @SerializedName("url")
    private String url;
    @SerializedName("background")
    private String background;
    @SerializedName("recommend")
    private AppInfo recommend;
    @SerializedName("expires")
    private int expires;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public AppInfo getRecommend() {
        return recommend;
    }

    public void setRecommend(AppInfo recommend) {
        this.recommend = recommend;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }
}
