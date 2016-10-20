package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

public class Layout {

    /**
     * id :
     * title :
     * action :
     * width : 1
     * height : 2
     * x : 0
     * y : 3
     * icon : http://......
     * actionData : JSON形式字符串
     */

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("action")
    private String action;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("x")
    private int x;
    @SerializedName("y")
    private int y;
    @SerializedName("icon")
    private String icon;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getActionData() {
        return actionData;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }
}
