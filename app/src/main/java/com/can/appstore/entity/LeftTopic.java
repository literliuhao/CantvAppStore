package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 「列表页」左侧菜单项
 */
public class LeftTopic {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
