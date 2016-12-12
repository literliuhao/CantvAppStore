package com.can.appstore.update.model;

/**
 * EventBus
 * Created by shenpx on 2016/11/30 0030.
 */

public class UpdateApkInstallModel {

    private int number;//0成功1失败
    private String appname;
    private String url;

    public UpdateApkInstallModel(int number, String appname, String url) {
        this.number = number;
        this.appname = appname;
        this.url = url;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
