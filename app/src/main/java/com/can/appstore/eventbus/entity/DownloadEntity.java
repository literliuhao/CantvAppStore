package com.can.appstore.eventbus.entity;

import cn.can.downloadlib.DownloadTask;

/**
 * Created by laiforg on 2016/11/9.
 */

public class DownloadEntity {

    private DownloadTask task;

    public DownloadTask getTask() {
        return task;
    }

    public void setTask(DownloadTask task) {
        this.task = task;
    }
}
