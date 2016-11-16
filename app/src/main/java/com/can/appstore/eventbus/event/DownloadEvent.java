package com.can.appstore.eventbus.event;

import com.can.appstore.eventbus.entity.DownloadEntity;

/**
 * Created by laiforg on 2016/11/9.
 */

public class DownloadEvent extends AbsCanEvent {

    public static final int DOWNLOADEVENT_UPDATE_LIST_STATUS=0x1;

    DownloadEntity data;

    public DownloadEntity getData() {
        return data;
    }

    public void setData(DownloadEntity data) {
        this.data = data;
    }
}
