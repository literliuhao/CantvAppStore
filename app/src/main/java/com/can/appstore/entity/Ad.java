package com.can.appstore.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 广告
 */
public class Ad {

    /**
     * adtfid : TF20161118003
     * name : 商城启动akp001
     * adpositionid : adscqd
     * adid : AD20160902001
     * adtypeid : 7
     * tftypeid : default
     * duration : 5
     * playaftersec : 0
     * refreshcycle : 30
     * timespan : 1480672434
     * material : [{"materialid":"SCTW20161118001","materialcontent":null,"materialurl":"http://172.16.11.32:7005/upload/16-11/18/4c7f87e15141d.jpg","materialtype":"2","materialmd5":"528429b068679f8a5a8ccdcc9553c528","order":"1","action":"OPEN_APP_MARKET_MAPPING_PAGE","actionParam":{"packageName":"com.amt.appstore.cibn","activityName":"com.amt.appstore.cibn","downloadUrl":"http://ams.ott.cibntv.net/apps/AppStore_CIBN_48_01.6.02.01_20160826_1520_signed.apk","md5":"A1CADF1AEEA8F54AB454E5FEBF729484","fileSize":"12","parameters":{"jumpid":"-1","pageindex":"-1","columnid":"-1","name":"","firstcategoryid":"-1","secondcategoryid":"-1","detailappid":"-1","detailid":"-1"}}}]
     */

    @SerializedName("adtfid")
    private String adtfid;
    @SerializedName("name")
    private String name;
    @SerializedName("adpositionid")
    private String adpositionid;
    @SerializedName("adid")
    private String adid;
    @SerializedName("adtypeid")
    private String adtypeid;
    @SerializedName("tftypeid")
    private String tftypeid;
    @SerializedName("duration")
    private String duration;
    @SerializedName("playaftersec")
    private String playaftersec;
    @SerializedName("refreshcycle")
    private String refreshcycle;
    @SerializedName("timespan")
    private String timespan;
    @SerializedName("material")
    private List<Material> material;

    public String getAdtfid() {
        return adtfid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdpositionid() {
        return adpositionid;
    }

    public String getAdid() {
        return adid;
    }

    public String getAdtypeid() {
        return adtypeid;
    }

    public String getTftypeid() {
        return tftypeid;
    }

    public String getDuration() {
        return duration;
    }

    public String getPlayaftersec() {
        return playaftersec;
    }

    public String getRefreshcycle() {
        return refreshcycle;
    }

    public String getTimespan() {
        return timespan;
    }

    public List<Material> getMaterial() {
        return material;
    }

    public void setMaterial(List<Material> material) {
        this.material = material;
    }

    public static class Material {
        /**
         * materialid : SCTW20161118001
         * materialcontent : null
         * materialurl : http://172.16.11.32:7005/upload/16-11/18/4c7f87e15141d.jpg
         * materialtype : 2
         * materialmd5 : 528429b068679f8a5a8ccdcc9553c528
         * order : 1
         */

        @SerializedName("materialid")
        private String materialid;
        @SerializedName("materialcontent")
        private Object materialcontent;
        @SerializedName("materialurl")
        private String materialurl;
        @SerializedName("materialtype")
        private String materialtype;
        @SerializedName("materialmd5")
        private String materialmd5;
        @SerializedName("order")
        private String order;
        @SerializedName("action")
        private String action;
        @SerializedName("actionParam")
        private JsonObject actionParam;

        public String getMaterialid() {
            return materialid;
        }

        public Object getMaterialcontent() {
            return materialcontent;
        }

        public String getMaterialurl() {
            return materialurl;
        }

        public String getMaterialtype() {
            return materialtype;
        }

        public String getMaterialmd5() {
            return materialmd5;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public String getAction() {
            return action;
        }

        public JsonObject getActionParam() {
            return actionParam;
        }

    }
}
