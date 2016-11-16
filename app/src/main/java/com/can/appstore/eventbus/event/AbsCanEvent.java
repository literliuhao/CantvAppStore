package com.can.appstore.eventbus.event;

/**
 * Created by laiforg on 2016/11/9.
 */

public abstract class AbsCanEvent {

    public int eventType;
    public String history;
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
