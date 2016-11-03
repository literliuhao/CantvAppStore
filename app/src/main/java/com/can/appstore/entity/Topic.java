package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 「列表页」左侧分类
 */
public class Topic {
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Topic{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
