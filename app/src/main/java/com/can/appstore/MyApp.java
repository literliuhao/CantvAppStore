package com.can.appstore;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.can.appstore.upgrade.MyUpgradeListener;
import com.can.appstore.utils.DataEyeUtil;
import com.dataeye.sdk.api.app.DCAgent;
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

    private static final String TAG = "Tinker.SampleApplicationLike";
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
        initDataEye();
        //初始化bugly
        initBugly(true);
    }

    private void initDataEye() {
        String dataEyeChannelId = DataEyeUtil.getDataEyeChannel(getContext());
        DCAgent.openAdTracking();//是否跟踪推广分析，默认是False，调用即为True.该接口必须在SDK初始化之前调用.

        DCAgent.initWithAppIdAndChannelId(getApplication().getApplicationContext(),
                AppConstants.DATAEYE_APPID, dataEyeChannelId);
        Log.d("DataEye", "DataEye渠道号: " + dataEyeChannelId);
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

    /**
     * Bugly实现自更新
     *
     * @param downloadSelf 是否自己下载apk
     *                     自下载：可控制下载、安装
     *                     Bugly下载：可控制下载，安装Bugly自行调用
     */
    private void initBugly(final boolean downloadSelf) {
        try {
            Beta.autoCheckUpgrade = false;
            Beta.showInterruptedStrategy = false;
            Beta.upgradeListener = new MyUpgradeListener(getApplication().getApplicationContext(),
                    downloadSelf);
            //测试使用key
            //Bugly.init(getApplication().getApplicationContext(), "900059606", false);
            //正式版本发布使用key
            Bugly.init(getApplication().getApplicationContext(), "e3c3b1806e", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
