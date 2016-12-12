package com.can.appstore.installpkg;

/**
 * EventBus
 * Created by shenpx on 2016/11/30 0030.
 */

public class InstallApkModel {

    private int position;//0成功 1失败
    private String name;
//    private String pkgName;
//    private int versioncode;

    public InstallApkModel(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public int getNumber() {
        return position;
    }

    public void setNumber(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
