package com.can.appstore.message.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by HEKANG on 2016/10/25.
 */
@Entity()
public class MessageInfo {

    @Id
    private Long  _id;   //数据表主键
    private String msgId; // 消息id
    private String msgDate; // 消息日期（格式：2106-10-21）
    private long msgExpires; // 消息有效期（格式：时间戳（s））
    private String msgTitle; // 消息标题（文本）
    private boolean status; // 消息状态，是否被查看（true：未查看、false：已查看）
    private String action ;
    private String actionData;
    private String userId; // 用户id
    private String extra1; // 扩展数据1
    private String extra2; // 扩展数据2
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
    public String getMsgTitle() {
        return this.msgTitle;
    }
    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }
    public long getMsgExpires() {
        return this.msgExpires;
    }
    public void setMsgExpires(long msgExpires) {
        this.msgExpires = msgExpires;
    }
    public String getMsgDate() {
        return this.msgDate;
    }
    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }
    public String getMsgId() {
        return this.msgId;
    }
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    @Generated(hash = 502481925)
    public MessageInfo(Long _id, String msgId, String msgDate, long msgExpires,
            String msgTitle, boolean status, String action, String actionData,
            String userId, String extra1, String extra2) {
        this._id = _id;
        this.msgId = msgId;
        this.msgDate = msgDate;
        this.msgExpires = msgExpires;
        this.msgTitle = msgTitle;
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
