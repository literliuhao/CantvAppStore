package com.can.appstore;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.can.appstore.entity.TvInfoModel;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MyApp extends DefaultApplicationLike {

    public static final String TAG = "Tinker.SampleApplicationLike";
    public static String DATAEYE_CHANNELID = "C42S-10002";//测试渠道,正式的默认渠道
    private static MyApp INSTANCE;
    public MyApp(Application application, int tinkerFlags,
                 boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
                 long applicationStartMillisTime, Intent tinkerResultIntent, Resources[] resources,
                 ClassLoader[] classLoader, AssetManager[] assetManager) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent, resources, classLoader,
                assetManager);
        INSTANCE = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        // 设置开发设备
        Bugly.setIsDevelopmentDevice(getApplication(), true);
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        Bugly.init(getApplication(), "e3c3b1806e", true);

        getDataEyeChannelId();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

    public static Context getContext() {
        return INSTANCE.getApplication().getApplicationContext();
    }

    private void getDataEyeChannelId() {
        TvInfoModel.getInstance().init(getApplication());
        String channelId = TvInfoModel.getInstance().getChannelId();
        String modelName = TvInfoModel.getInstance().getModelName();
        if (channelId != null && channelId.contains("|")) {
            channelId = channelId.substring(0, channelId.indexOf("|")).trim();
        }
        if (!TextUtils.isEmpty(modelName) && !TextUtils.isEmpty(channelId)) {
            MyApp.DATAEYE_CHANNELID = modelName + "-" + channelId;
        } else if (!TextUtils.isEmpty(channelId)) {
            MyApp.DATAEYE_CHANNELID = channelId;
        }
    }
}
