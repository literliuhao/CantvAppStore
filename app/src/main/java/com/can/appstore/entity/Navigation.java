package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.can.tvlib.utils.CollectionUtil;

/**
 * 首页导航
 */
public class Navigation {

    /**
     * id :
     * title : 推荐
     * layout : []
     */

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("layout")
    private List<Layout> layout;

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

    public List<Layout> getLayout() {
        return CollectionUtil.emptyIfNull(layout);
    }

    public void setLayout(List<Layout> layout) {
        this.layout = layout;
    }
}
