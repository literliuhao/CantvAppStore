package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.can.tvlib.common.custom.CollectionUtil;


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
    @SerializedName("baseWidth")
    private int baseWidth;
    @SerializedName("baseHeight")
    private int baseHeight;
    @SerializedName("lineSpace")
    private int lineSpace;
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

    public int getBaseWidth() {
        return baseWidth;
    }

    public void setBaseWidth(int baseWidth) {
        this.baseWidth = baseWidth;
    }

    public int getBaseHeight() {
        return baseHeight;
    }

    public void setBaseHeight(int baseHeight) {
        this.baseHeight = baseHeight;
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public List<Layout> getLayout() {
        return CollectionUtil.emptyIfNull(layout);
    }

    public void setLayout(List<Layout> layout) {
        this.layout = layout;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Navigation{");
        sb.append("id='").append(id).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", baseWidth=").append(baseWidth);
        sb.append(", baseHeight=").append(baseHeight);
        sb.append(", lineSpace=").append(lineSpace);
        sb.append(", layout=").append(layout);
        sb.append('}');
        return sb.toString();
    }
}
