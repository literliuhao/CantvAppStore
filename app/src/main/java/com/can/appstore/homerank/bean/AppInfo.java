package com.can.appstore.homerank.bean;

/**
 * Created by yibh on 2016/10/17 16:40 .
 */

public class AppInfo {

    /**
     * id : 1
     * name : 欢乐斗地主
     * icon : 图标url
     * marker : ======图标或类型======
     */

    private int id;
    private String name;
    private String icon;
    private String marker;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }
}
