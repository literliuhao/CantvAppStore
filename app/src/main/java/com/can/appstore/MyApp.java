package com.can.appstore;

import android.app.Application;
import android.content.Context;

import com.dataeye.sdk.api.app.DCAgent;

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

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        initDataEye();
    }

    private void initDataEye() {
        DCAgent.openAdTracking();//是否跟踪推广分析，默认是False，调用即为True.该接口必须在SDK初始化之前调用.
        DCAgent.initWithAppIdAndChannelId(this, AppConstants.DATAEYE_APPID, AppConstants.DATAEYE_CHANNELID);
    }

    public static Context getContext() {
        return INSTANCE.getApplicationContext();
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
