package com.can.appstore;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.dataeye.sdk.api.app.DCAgent;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

/**
 * 自定义ApplicationLike类.
 *
 * 注意：这个类是Application的代理类，以前所有在Application的实现必须要全部拷贝到这里<br/>
 *
 * @author zhl
 * @since 2016/12/8
 */
public class MyApp extends DefaultApplicationLike {

    public static final String TAG = "Tinker.SampleApplicationLike";
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
        // 设置开发设备
        Bugly.setIsDevelopmentDevice(getApplication(), true);
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        Bugly.init(getApplication(), "e3c3b1806e", true);

        initDataEye();
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

    private void initDataEye() {
        DCAgent.openAdTracking();//是否跟踪推广分析，默认是False，调用即为True.该接口必须在SDK初始化之前调用.
        DCAgent.initWithAppIdAndChannelId(getApplication(), AppConstants.DATAEYE_APPID, AppConstants.DATAEYE_CHANNELID);
    }

    public static Context getContext() {
        return INSTANCE.getApplication().getApplicationContext();
    }

}
