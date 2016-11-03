package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 消息中心==》消息
 */
public class Message {

    /**
     * id :
     * title :
     * date : 2016-10-16
     * expires : 1476699293
     * action : 见action​常量​与 actionData
     * actionData : 见action​常量​与 actionData
     */

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("date")
    private String date;
    @SerializedName("expires")
    private int expires;
    @SerializedName("action")
    private String action;
    @SerializedName("actionData")
    private String actionData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Message{");
        sb.append("id='").append(id).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", expires=").append(expires);
        sb.append(", action='").append(action).append('\'');
        sb.append(", actionData='").append(actionData).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
