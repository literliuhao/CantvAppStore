package com.can.appstore.eventbus.dispatcher;

import com.can.appstore.eventbus.event.DownloadEvent;

/**
 * Created by laiforg on 2016/11/9.
 */

public class DownloadDispatcher extends AbsCanDispatcher {

    private static DownloadDispatcher instance;
    private DownloadDispatcher(){
    }
    public static DownloadDispatcher getInstance(){
        if (instance==null){
            synchronized (DownloadDispatcher.class){
                if(instance==null){
                    instance=new DownloadDispatcher();
                }
            }
        }
        return instance;
    }

    public void postDownloadStatusEvent(String history,String tag){
        DownloadEvent event=new DownloadEvent();
        event.setHistory(history);
        event.setTag(tag);
        event.setEventType(DownloadEvent.DOWNLOADEVENT_UPDATE_DOWNLOAD_STATUS);
        postEvent(event);
    }

    public void postInstallStatusEvent(String taskid,String history,String tag){
        DownloadEvent event=new DownloadEvent();
        event.setHistory(history);
        event.setTag(tag);
        event.setDownloadTaskId(taskid);
        event.setEventType(DownloadEvent.DOWNLOADEVENT_UPDATE_INSTALL_STATUS);
        postEvent(event);
    }

}
