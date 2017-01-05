package com.can.appstore.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;

import com.can.appstore.AppConstants;
import com.can.appstore.entity.TvInfoModel;

/**
 * @author chenjian
 */
public class DataEyeUtil {

    public static String getDataEyeChannel(Context context) {
        TvInfoModel.getInstance().init(context);
        String channelId = TvInfoModel.getInstance().getChannelId();
        String modelName = TvInfoModel.getInstance().getModelName();

        if (channelId == null) {
            // 从系统中取
            ContentResolver contentResolver = context.getContentResolver();
            channelId = Settings.System.getString(contentResolver, AppConstants.SYSTEM_PROVIDER_KEY_CHANNELID);
            modelName = Settings.System.getString(contentResolver, AppConstants.SYSTEM_PROVIDER_KEY_MODEL);
        }
        return getDataEyeChannel(channelId, modelName);
    }

    public static String getDataEyeChannel(String channelId, String modelName) {
        if (channelId != null && channelId.contains("|")) {
            channelId = channelId.substring(0, channelId.indexOf("|")).trim();
        }

        if (!TextUtils.isEmpty(modelName) && !TextUtils.isEmpty(channelId)) {
            return modelName + "-" + channelId;
        } else if (!TextUtils.isEmpty(channelId)) {
            return channelId;
        }
        return AppConstants.DATAEYE_DEFAULT_CHANNEL;
    }

    public static void updateDataEyeChannel(Context context) {
        // 当取到渠道信息时，更新DataEye缓存的渠道信息
        if (TvInfoModel.getInstance().alreadyInit()) {
            String dataEyeSpName = String.format("dc.%1$s.preferences", AppConstants.DATAEYE_APPID);

            SharedPreferences sharedPreferences = context.getSharedPreferences(dataEyeSpName, Context.MODE_PRIVATE);

            String newChannel = DataEyeUtil.getDataEyeChannel(
                    TvInfoModel.getInstance().getChannelId(),
                    TvInfoModel.getInstance().getModelName());

            String dcChannel = sharedPreferences.getString("DC_CHANNEL", "");

            if (!dcChannel.equals(newChannel)) {
                sharedPreferences.edit().putString("DC_CHANNEL", newChannel).apply();
            }
        }
    }
}
