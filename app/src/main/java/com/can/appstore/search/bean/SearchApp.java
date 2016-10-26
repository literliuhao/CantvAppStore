package com.can.appstore.search.bean;

/**
 * Created by yibh on 2016/10/13 18:36 .
 */

public class SearchApp {
    public String mName;
    public String mInitials;    //首字母
    public String mUrl;

    public SearchApp(String mName, String mInitials) {
        this.mName = mName;
        this.mInitials = mInitials;
    }
}
