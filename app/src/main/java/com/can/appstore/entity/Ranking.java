package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.can.tvlib.utils.CollectionUtil;

/**
 * 排行榜
 */
public class Ranking {

    /**
     * id :
     * name : 应用排行榜
     * data : []
     */

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("data")
    private List<AppInfo> data;

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

    public List<AppInfo> getData() {
        return CollectionUtil.emptyIfNull(data);
    }

    public void setData(List<AppInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Ranking{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
