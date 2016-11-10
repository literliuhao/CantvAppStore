package com.can.appstore.eventbus.dispatcher;

import com.can.appstore.eventbus.event.AbsCanEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by laiforg on 2016/11/9.
 */

public abstract class AbsCanDispatcher {

    protected String TAG = AbsCanDispatcher.class.getSimpleName();
    protected EventBus mEventBus;

    public AbsCanDispatcher() {
        mEventBus = EventBus.getDefault();
    }

    public <E extends AbsCanEvent> void postEvent(E event) {
        mEventBus.post(event);
    }
}
