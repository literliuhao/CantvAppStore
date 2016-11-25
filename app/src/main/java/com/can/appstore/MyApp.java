package com.can.appstore;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.WindowManager;

import com.can.appstore.myapps.utils.MyAppsListDataUtil;
import com.can.appstore.upgrade.service.BuglyUpgradeService;
import com.can.appstore.upgrade.service.UpgradeService;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.proguard.aa;

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
    private static final String TAG = "MyApp";
    public static Context mContext;
    private static MyApp INSTANCE;
    public static int Width;
    public static int Height;
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

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Width = wm.getDefaultDisplay().getWidth();
        Height = wm.getDefaultDisplay().getHeight();

        //白名单
        PRE_APPS.add("com.cantv.wechatphoto");
        PRE_APPS.add("com.cantv.media");
        PRE_APPS.add("com.tvkou.linker");
        PRE_APPS.add("com.tvm.suntv.news.client.activity");
        PRE_APPS.add("com.cantv.market");

        //所有的第三方应用
        myAppList = PackageUtil.findAllThirdPartyAppsNoDelay(this, myAppList);
        registerInstallReceiver();
        initBuly(true);
    }

    public static Context getContext() {
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

    /**
     * Bugly实现自更新
     *
     * @param downloadSelf 是否自己下载apk
     *                     自下载：可控制下载、安装
     *                     Bugly下载：可控制下载，安装Bugly自行调用
     */
    private void initBuly(final boolean downloadSelf) {
        Beta.autoCheckUpgrade = false;
        Beta.showInterruptedStrategy = false;
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                if (strategy != null) {
                    Log.d(TAG, "onUpgrade: 更新");
                    Intent intent;
                    if (downloadSelf) {
                        intent = new Intent(MyApp.this, UpgradeService.class);
                    } else {
                        intent = new Intent(MyApp.this, BuglyUpgradeService.class);
                    }
                    MyApp.this.startService(intent);
                } else {
                    Log.d(TAG, "onUpgrade: 没有更新");
                }
            }
        };
        //Bugly.init(getApplicationContext(), "900059606", true);//测试使用
        Bugly.init(getApplicationContext(), "e3c3b1806e", false);
        Beta.checkUpgrade();
    }

}
