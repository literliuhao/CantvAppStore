package com.can.appstore.eventbus.event;

/**
 * Created by laiforg on 2016/11/9.
 */

public class DownloadEvent extends AbsCanEvent {

    /**下载页面点击全部暂停或全部继续时，刷新当前页面下载任务状态*/
    public static final int DOWNLOADEVENT_UPDATE_DOWNLOAD_STATUS =0x1;
    /**刷新安装状态*/
    public static final int DOWNLOADEVENT_UPDATE_INSTALL_STATUS=0x2;

    public String downloadTaskId;

    public String getDownloadTaskId() {
        return downloadTaskId;
    }
    public void setDownloadTaskId(String downloadTaskId) {
        this.downloadTaskId = downloadTaskId;
    }
}
