package com.can.appstore.index.entity;

import java.util.List;

/**
 * Created by liuhao on 2016/10/13.
 */

public class LayoutBean {
    private static final long serialVersionUID = 1L;
    private int id;
    private String title;
    private int baseWidth;
    private int baseHeight;
    private int lineSpace;

    private List<ChildBean> pages;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChildBean> getPages() {
        return pages;
    }

    public void setPages(List<ChildBean> pages) {
        this.pages = pages;
    }

    public void setBaseWidth(int baseWidth) {
        this.baseWidth = baseWidth;
    }

    public void setBaseHeight(int baseHeight) {
        this.baseHeight = baseHeight;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public int getBaseWidth() {
        return baseWidth;
    }

    public int getBaseHeight() {
        return baseHeight;
    }

    public int getLineSpace() {
        return lineSpace;
    }
}
