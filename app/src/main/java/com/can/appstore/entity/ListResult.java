package com.can.appstore.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.can.tvlib.utils.CollectionUtil;

public class ListResult<T> {

    /**
     * status : 0
     * message : 成功
     * data : []
     * total : 100
     */

    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    @SerializedName("total")
    private int total;
    @SerializedName("data")
    private List<T> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getData() {
        return CollectionUtil.emptyIfNull(data);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ListResult{");
        sb.append("status=").append(status);
        sb.append(", message='").append(message).append('\'');
        sb.append(", total=").append(total);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
