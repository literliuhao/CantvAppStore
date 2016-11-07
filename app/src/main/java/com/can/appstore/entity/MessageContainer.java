package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.can.tvlib.utils.CollectionUtil;

public class MessageContainer {

    /**
     * timestamp : 1476699293
     * messages : []
     */

    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("messages")
    private List<Message> messages;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<Message> getMessages() {
        return CollectionUtil.emptyIfNull(messages);
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MessageContainer{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", messages=").append(messages);
        sb.append('}');
        return sb.toString();
    }
}
