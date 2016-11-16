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

    public void postUpdateStatusEvent(String history,String tag){
        DownloadEvent event=new DownloadEvent();
        event.setHistory(history);
        event.setTag(tag);
        event.setEventType(DownloadEvent.DOWNLOADEVENT_UPDATE_LIST_STATUS);
        postEvent(event);
    }

}
