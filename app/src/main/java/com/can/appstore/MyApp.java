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
        return INSTANCE.getApplicationContext();
    }

    private void getDataEyeChannelId() {
        TvInfoModel.getInstance().init(this);
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


    //    /**
    //     * 注册应用安装卸载的广播
    //     */
    //    public AppInstallReceiver mAppInstallReceiver;
    //
    //    private void registerInstallReceiver() {
    //        if (mAppInstallReceiver == null) {
    //            mAppInstallReceiver = new AppInstallReceiver();
    //            IntentFilter filter = new IntentFilter();
    //            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
    //            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
    //            filter.addAction(Intent.ACTION_VIEW);
    //            filter.addDataScheme("package");
    //            registerReceiver(mAppInstallReceiver, filter);
    //        }
    //    }
    //    class AppInstallReceiver extends BroadcastReceiver {
    //        @Override
    //        public void onReceive(Context context, Intent intent) {
    //            // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
    //            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
    //                String packageName = intent.getDataString().substring(8);
    //                String appName = PackageUtil.getAppInfo(getApplicationContext(), packageName).appName;
    //                ToastUtils.showMessage(getApplicationContext(), appName + getResources().getString(R.string.install_success));
    //            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
    //                String packageName = intent.getData().getSchemeSpecificPart();
    //            }
    //            myAppList = new MyAppsListDataUtil(getApplicationContext()).getAllAppList(myAppList);
    //
    //        }
    //    }
}
