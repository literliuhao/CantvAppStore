package com.can.appstore.update.model;

/**
 * EventBus Apk待更新数实体类
 * Created by shenpx on 2016/11/30 0030.
 */

public class UpdateApkModel {
    /**
     * 获取可更新App数量
     */
    private int number;

    public UpdateApkModel(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
