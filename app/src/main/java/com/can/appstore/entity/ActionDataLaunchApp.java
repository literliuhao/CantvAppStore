package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import cn.can.tvlib.common.custom.CollectionUtil;


public class ActionDataLaunchApp {

    /**
     * packageName : 应用包名
     * activityName :
     * intentAction :
     * intentExtra : {}
     * md5sum :
     * url :
     */

    @SerializedName("packageName")
    private String packageName;
    @SerializedName("activityName")
    private String activityName;
    @SerializedName("intentAction")
    private String intentAction;
    @SerializedName("intentExtra")
    private Map<String,?> intentExtra;
    @SerializedName("md5sum")
    private String md5sum;
    @SerializedName("url")
    private String url;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getIntentAction() {
        return intentAction;
    }

    public void setIntentAction(String intentAction) {
        this.intentAction = intentAction;
    }

    public Map<String,?> getIntentExtra() {
        return CollectionUtil.emptyIfNull(intentExtra);
    }

    public void setIntentExtra(Map<String,?> intentExtra) {
        this.intentExtra = intentExtra;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ActionDataLaunchApp{");
        sb.append("packageName='").append(packageName).append('\'');
        sb.append(", activityName='").append(activityName).append('\'');
        sb.append(", intentAction='").append(intentAction).append('\'');
        sb.append(", intentExtra=").append(intentExtra);
        sb.append(", md5sum='").append(md5sum).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
