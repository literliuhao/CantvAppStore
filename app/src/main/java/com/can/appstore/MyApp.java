package com.can.appstore;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.can.appstore.entity.TvInfoModel;
import com.can.appstore.upgrade.MyUpgradeListener;
import com.dataeye.sdk.api.app.DCAgent;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MyApp extends Application {
    private static MyApp INSTANCE;
    public static String DATAEYE_CHANNELID = "C42S-10002";//测试渠道,正式的默认渠道

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        initDataEye();
        //初始化bugly
        initBugly(true);
    }

    public static Context getContext() {
        return INSTANCE.getApplicationContext();
    }

    private void initDataEye() {
        getDataEyeChannelId();
        DCAgent.openAdTracking();//是否跟踪推广分析，默认是False，调用即为True.该接口必须在SDK初始化之前调用.
        DCAgent.initWithAppIdAndChannelId(getApplicationContext(), AppConstants.DATAEYE_APPID, MyApp.DATAEYE_CHANNELID);
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
            Beta.upgradeListener = new MyUpgradeListener(this, downloadSelf);
            //测试使用key
            //Bugly.init(getApplicationContext(), "900059606", false);
            //正式版本发布使用key
            Bugly.init(getApplicationContext(), "e3c3b1806e", false);
            Beta.checkUpgrade(false, true);
        } catch (Exception e) {
            e.printStackTrace();
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
