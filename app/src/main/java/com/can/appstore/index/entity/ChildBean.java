package com.can.appstore.index.entity;

/**
 * Created by liuhao on 2016/10/13.
 */

public class ChildBean {
    private int id;
    private String action;
    private int x;
    private int y;
    private int width;
    private int height;
    private String title;
    private String actionData;
    private String icon;

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setActionData(String actionData) {
        this.actionData = actionData;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getActionData() {
        return actionData;
    }

    public String getIcon() {
        return icon;
    }


}
