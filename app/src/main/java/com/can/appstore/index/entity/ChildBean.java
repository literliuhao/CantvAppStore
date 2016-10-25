package com.can.appstore.index.entity;

/**
 * Created by liuhao on 2016/10/13.
 */

public class ChildBean {
    private static final long serialVersionUID = 1L;
    private int id;
    private String bg;
    private String action;
    private int x;
    private int y;
    private int width;
    private int height;

    public int getId() {
        return id;
    }

    public String getBg() {
        return bg;
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

    public void setBg(String bg) {
        this.bg = bg;
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

}
