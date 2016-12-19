package com.can.appstore.db.entity;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by HEKANG on 2016/12/7.
 */
@Entity
public class MessageInfo {

    @Id
    private  Long     _id;         //主键
    @SerializedName("id")
    private  String   id;          // 消息id
    @SerializedName("date")
    private  String   date;        // 消息日期（格式：2106-10-21）
    @SerializedName("expires")
    private  long     expires;     // 消息有效期（格式：时间戳（单位s））
    @SerializedName("title")
    private  String   title;
    private  boolean  status;      // 消息状态，是否被查看（false：未查看、true：已查看）
    @SerializedName("action")
    private  String   action ;
    @SerializedName("actionData")
    private  String   actionData;
    private  String   userId;      // 用户id，默认取机器mac
    private  String   extra1;
    private  String   extra2;
    public String getExtra2() {
        return this.extra2;
    }
    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }
    public String getExtra1() {
        return this.extra1;
    }
    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getActionData() {
        return this.actionData;
    }
    public void setActionData(String actionData) {
        this.actionData = actionData;
    }
    public String getAction() {
        return this.action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public boolean getStatus() {
        return this.status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getExpires() {
        return this.expires;
    }
    public void setExpires(long expires) {
        this.expires = expires;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    @Generated(hash = 250859644)
    public MessageInfo(Long _id, String id, String date, long expires,
            String title, boolean status, String action, String actionData,
            String userId, String extra1, String extra2) {
        this._id = _id;
        this.id = id;
        this.date = date;
        this.expires = expires;
        this.title = title;
        this.status = status;
        this.action = action;
        this.actionData = actionData;
        this.userId = userId;
        this.extra1 = extra1;
        this.extra2 = extra2;
    }
    @Generated(hash = 1292770546)
    public MessageInfo() {
    }

}
