package com.can.appstore.entity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import cn.can.tvlib.common.storage.PreferencesUtils;


/**
 * TMS机型信息
 *
 * @author 陈建
 */
public class TvInfoModel {
    public static final String TAG = "TvInfoModel";
    private static final TvInfoModel instance = new TvInfoModel();

    /**
     * channelId : 10002
     * channelName : 自有终端水平渠道（低配版）
     * internalmodelName : QHTF_F5
     * internalmodelId : 44
     * modelName : QHTF_F5
     * modelId : 42
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

    public String getChannelName() {
        return channelName;
    }


    public String getInternalmodelName() {
        return internalmodelName;
    }


    public String getInternalmodelId() {
        return internalmodelId;
    }


    public String getModelName() {
        return modelName;
    }


    public String getModelId() {
        return modelId;
    }


    public String getVendorName() {
        return vendorName;
    }


    public String getVendorId() {
        return vendorId;
    }

    public boolean alreadyInit() {
        return !(TextUtils.isEmpty(channelId) ||
                TextUtils.isEmpty(internalmodelId) ||
                TextUtils.isEmpty(modelId));
    }

    public static TvInfoModel getInstance() {
        return instance;
    }

    public void copyFrom(TvInfoModel that) {
        if (that == null) {
            return;
        }
        this.channelId = that.channelId;
        this.channelName = that.channelName;
        this.internalmodelName = that.internalmodelName;
        this.internalmodelId = that.internalmodelId;
        this.modelName = that.modelName;
        this.modelId = that.modelId;
        this.vendorName = that.vendorName;
        this.vendorId = that.vendorId;
    }

    public boolean init(Context context) {
        if (alreadyInit()) return true;
        String sp_key = PreferencesUtils.getString(context, TAG, null);
        if (sp_key == null) {
            return false;
        }
        Gson gson = new Gson();
        TvInfoModel tvInfoModel = gson.fromJson(sp_key, TvInfoModel.class);
        copyFrom(tvInfoModel);
        return alreadyInit();
    }

    public void saveToSp(Context context) {
        if (context == null) {
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(this);
        PreferencesUtils.putString(context, TAG, json);
        Log.d(TAG, "saveToSp");
    }
}
