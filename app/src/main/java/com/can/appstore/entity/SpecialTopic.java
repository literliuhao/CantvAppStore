package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.can.tvlib.common.custom.CollectionUtil;


/**
 * 专题
 */
public class SpecialTopic {

    @SerializedName("id")
    private String id;
    // 标题
    @SerializedName("title")
    private String title;
    // 小图标
    @SerializedName("icon")
    private String icon;
    // 大图
    @SerializedName("background")
    private String background;
    // 推荐列表
    @SerializedName("recommend")
    private List<AppInfo> recommend;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<AppInfo> getRecommend() {
        return CollectionUtil.emptyIfNull(recommend);
    }

    public void setRecommend(List<AppInfo> recommend) {
        this.recommend = recommend;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SpecialTopic{");
        sb.append("id='").append(id).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", icon='").append(icon).append('\'');
        sb.append(", background='").append(background).append('\'');
        sb.append(", recommend=").append(recommend);
        sb.append('}');
        return sb.toString();
    }
}
