package com.can.appstore.http;

import com.google.gson.annotations.SerializedName;

/**
 * 机型信息容器
 * @author 陈建
 */
public class TvInfoHolderWrapper {

    /**
     * data : {"channelId":"20001","channelName":"合作终端水平渠道","internalmodelName":"JOHE.LIVE_F6","internalmodelId":"56","modelName":"JOHE.LIVE_F6","modelId":"55","vendorName":"亿典","vendorId":"2"}
     * status : 200
     * info : 成功
     */

    @SerializedName("data")
    private TvInfoHolder data;
    @SerializedName("status")
    private int status;
    @SerializedName("info")
    private String info;

    public TvInfoHolder getData() {
        return data;
    }

    public void setData(TvInfoHolder data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static class TvInfoHolder {
        /**
         * channelId : 20001
         * channelName : 合作终端水平渠道
         * internalmodelName : JOHE.LIVE_F6
         * internalmodelId : 56
         * modelName : JOHE.LIVE_F6
         * modelId : 55
         * vendorName : 亿典
         * vendorId : 2
         */

        @SerializedName("channelId")
        private String channelId;
        @SerializedName("channelName")
        private String channelName;
        @SerializedName("internalmodelName")
        private String internalmodelName;
        @SerializedName("internalmodelId")
        private String internalmodelId;
        @SerializedName("modelName")
        private String modelName;
        @SerializedName("modelId")
        private String modelId;
        @SerializedName("vendorName")
        private String vendorName;
        @SerializedName("vendorId")
        private String vendorId;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getInternalmodelName() {
            return internalmodelName;
        }

        public void setInternalmodelName(String internalmodelName) {
            this.internalmodelName = internalmodelName;
        }

        public String getInternalmodelId() {
            return internalmodelId;
        }

        public void setInternalmodelId(String internalmodelId) {
            this.internalmodelId = internalmodelId;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getModelId() {
            return modelId;
        }

        public void setModelId(String modelId) {
            this.modelId = modelId;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public String getVendorId() {
            return vendorId;
        }

        public void setVendorId(String vendorId) {
            this.vendorId = vendorId;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("TvInfoHolder{");
            sb.append("channelId='").append(channelId).append('\'');
            sb.append(", channelName='").append(channelName).append('\'');
            sb.append(", internalmodelName='").append(internalmodelName).append('\'');
            sb.append(", internalmodelId='").append(internalmodelId).append('\'');
            sb.append(", modelName='").append(modelName).append('\'');
            sb.append(", modelId='").append(modelId).append('\'');
            sb.append(", vendorName='").append(vendorName).append('\'');
            sb.append(", vendorId='").append(vendorId).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TvInfoHolderWrapper{");
        sb.append("data=").append(data);
        sb.append(", status=").append(status);
        sb.append(", info='").append(info).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
