package com.can.appstore.index.entity;

import java.util.List;

/**
 * Created by liuhao on 2016/10/17.
 */

public class PageBean {
    private static final long serialVersionUID = 1L;
    private List<LayoutBean> pageLists;

    public List<LayoutBean> getPageLists() {
        return pageLists;
    }

    public void setPageLists(List<LayoutBean> pageLists) {
        this.pageLists = pageLists;
    }
}
