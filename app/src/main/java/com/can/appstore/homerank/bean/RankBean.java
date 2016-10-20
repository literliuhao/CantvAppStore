package com.can.appstore.homerank.bean;

import java.util.List;

/**
 * Created by yibh on 2016/10/17 16:31 .
 */

public class RankBean {


    /**
     * status : 0
     * message : 成功
     * data : [{"id":"123","name":"应用排行榜","data1":[{}]}]
     */

    private int status;
    private String message;
    /**
     * id : 123
     * name : 应用排行榜
     * data1 : [{}]
     */

    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String name;
        private List<AppInfo> data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<AppInfo> getData() {
            return data;
        }

        public void setData(List<AppInfo> data) {
            this.data = data;
        }

    }
}
