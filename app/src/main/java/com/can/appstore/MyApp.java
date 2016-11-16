package com.can.appstore;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.can.appstore.myapps.model.MyAppsListDataUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

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
    public static Context mContext;
    //内存维护的全局应用List
    public static List<AppInfo> myAppList = new ArrayList<AppInfo>();
    /**
     * 预装APP白名单
     */
    public final static List<String> PRE_APPS = new ArrayList<String>();




    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;

        PRE_APPS.add("cn.cibntv.ott");
        PRE_APPS.add("com.cantv.media");
        myAppList = PackageUtil.findAllThirdPartyApps(this,myAppList);
        registerInstallReceiver();
    }

    public Context getContext() {
        return mContext;
    }


    /**
     * 注册应用安装卸载的广播
     */
    public AppInstallReceiver mAppInstallReceiver;
    private void registerInstallReceiver() {
        if (mAppInstallReceiver == null) {
            mAppInstallReceiver = new AppInstallReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_VIEW);
            filter.addDataScheme("package");
            mContext.registerReceiver(mAppInstallReceiver, filter);
        }
    }

    class AppInstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
            }
            myAppList = new MyAppsListDataUtil(mContext).getAllAppList(myAppList);
        }
    }

}
