package com.can.appstore.entity;

import android.support.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AdReportParam {
    /**
     * 用户是否点击，是
     */
    public static final int USER_ACTION_YES = 1;
    /**
     * 用户是否点击，否
     */
    public static final int USER_ACTION_NO = 0;

    /**
     * 用户类型，VIP
     */
    public static final int MEMBER_VIP = 1;
    /**
     * 用户类型，普通用户
     */
    public static final int MEMBER_NORMAL = 2;

    public static final int DEVICE_TYPE_TV = 1;
    public static final int DEVICE_TYPE_OTT = 2;

    @IntDef({USER_ACTION_NO, USER_ACTION_YES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserAction {
    }


    @IntDef({MEMBER_VIP, MEMBER_NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Member {
    }


    @IntDef({1})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Platform {
    }


    @IntDef({DEVICE_TYPE_TV, DEVICE_TYPE_OTT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DeviceType {
    }

    @SerializedName("adpositionid")
    private String adPositionId;
    @SerializedName("adtfid")
    private String adtfId;
    @SerializedName("userid")
    private int userId;
    @SerializedName("mac")
    private String mac;
    @SerializedName("member")
    private int member;
    @SerializedName("area")
    private String area;
    @SerializedName("model")
    private String model;
    @SerializedName("channel")
    private String channel;
    @SerializedName("versionid")
    private String versionId;
    @SerializedName("useraction")
    private int userAction;
    @SerializedName("platform")
    private int platform;
    @SerializedName("devicetype")
    private int deviceType;
    @SerializedName("materialid")
    private String materialId;
    @SerializedName("duration")
    private int duration;
    @SerializedName("impressions")
    private int impressions;

    public String getAdPositionId() {
        return adPositionId;
    }

    public void setAdPositionId(String adPositionId) {
        this.adPositionId = adPositionId;
    }

    public String getAdtfId() {
        return adtfId;
    }

    public void setAdtfId(String adtfId) {
        this.adtfId = adtfId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public int getUserAction() {
        return userAction;
    }

    public void setUserAction(int userAction) {
        this.userAction = userAction;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }




}
