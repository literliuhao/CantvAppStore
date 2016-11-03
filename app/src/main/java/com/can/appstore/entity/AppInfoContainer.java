package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.can.tvlib.utils.CollectionUtil;

/**
 * 应用列表容器，用于排行列表、应用列表
 */
public class AppInfoContainer {

    /**
     * typeName : 应用排行
     * topics : [{"id":"","name":"应用","isFocused":true}]
     * total : 100
     * data : []
     */

    @SerializedName("typeName")
    private String typeName;
    @SerializedName("total")
    private int total;
    @SerializedName("topics")
    private List<Topic> topics;
    @SerializedName("data")
    private List<AppInfo> data;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Topic> getTopics() {
        return CollectionUtil.emptyIfNull(topics);
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<AppInfo> getData() {
        return CollectionUtil.emptyIfNull(data);
    }

    public void setData(List<AppInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AppInfoContainer{");
        sb.append("typeName='").append(typeName).append('\'');
        sb.append(", total=").append(total);
        sb.append(", topics=").append(topics);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
