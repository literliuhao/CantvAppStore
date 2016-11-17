package com.can.appstore.eventbus.event;

/**
 * Created by laiforg on 2016/11/9.
 */

public abstract class AbsCanEvent {
    /**用于记录事件的类型，不同模块有不同时间类型*/
    public int eventType;
    /**debug调试字段，记录event事件发出源位置*/
    public String history;
    /**记录模块事件的标记*/
    public String tag;

    public AbsCanEvent(){

    }
    public int getEventType() {
        return eventType;
    }
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
    public String getHistory() {
        return history;
    }
    public void setHistory(String history) {
        this.history = history;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "AbsCanEvent{" +
                "eventType=" + eventType +
                ", history='" + history + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
