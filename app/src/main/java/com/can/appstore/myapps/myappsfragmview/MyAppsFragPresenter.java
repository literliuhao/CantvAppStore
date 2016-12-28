package com.can.appstore.myapps.myappsfragmview;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.can.appstore.myapps.ui.MyAppsFragment;
import com.can.appstore.myapps.utils.MyAppsListDataUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

import static android.util.Config.LOGD;

/**
 * Created by wei on 2016/11/9.
 */

public class MyAppsFragPresenter implements MyAppsFramentContract.Presenter {
    private static final int SYSTEM_APPICON_PIC_COUNT = 6;//系统应用最多放置的图片数量
    public static String TAG = "MyAppsFragPresenter";
    private MyAppsFramentContract.View mView;
    private Context mContext;
    private MyAppsListDataUtil mMyAppsListDataUtil;
    private AppInstallReceiver mAppInstallReceiver;

    //主页显示的第三方应用
    private List<AppInfo> mShowList = new ArrayList<>();
    //系统应用
    List<AppInfo> systemApp;
    //系统应用的icon
    private List<Drawable> mDrawables = new ArrayList<>();
    //内存维护的全局应用List
    public List<AppInfo> myAppList = new ArrayList<AppInfo>() {
    };


    public MyAppsFragPresenter(MyAppsFramentContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public void startLoad() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                //初始化数据
                mMyAppsListDataUtil = new MyAppsListDataUtil(mContext);
                //所有的第三方应用
                myAppList = PackageUtil.findAllThirdPartyAppsNoDelay(mContext, myAppList);
                systemApp = mMyAppsListDataUtil.getSystemApp(null);
                mShowList = mMyAppsListDataUtil.getShowList(mShowList, myAppList);
                Log.d("TAG", "doInBackground   mShowList : " + mShowList.size());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mDrawables = sysAppInfo2Drawble(systemApp, mDrawables);
                mView.loadAppInfoSuccess(mShowList, myAppList.size());
                mView.loadCustomDataSuccess(mDrawables);
                removeHideApps();
            }
        }.execute();

    }

    //筛选隐藏应用
    public void removeHideApps() {
        systemApp = mMyAppsListDataUtil.removeHideApp(systemApp);
        mDrawables = sysAppInfo2Drawble(systemApp, mDrawables);
        mView.loadCustomDataSuccess(mDrawables);
    }

    private List<Drawable> sysAppInfo2Drawble(List<AppInfo> list, List<Drawable> mDrawablelist) {
        if (mDrawablelist.size() != 0) {
            mDrawablelist.clear();
        }
        if (list.size() <= SYSTEM_APPICON_PIC_COUNT) {
            for (int i = 0; i < list.size(); i++) {
                mDrawablelist.add(list.get(i).appIcon);
            }
        } else {
            for (int i = 0; i < SYSTEM_APPICON_PIC_COUNT; i++) {
                mDrawablelist.add(list.get(i).appIcon);
            }
        }
        return mDrawablelist;
    }

    @Override
    public void addListener() {
        registerInstallReceiver();
    }

    /**
     * 注册应用安装卸载的广播
     */
    private void registerInstallReceiver() {
        if (mAppInstallReceiver == null) {
            mAppInstallReceiver = new MyAppsFragPresenter.AppInstallReceiver();
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
            Log.d(TAG, "onReceive: 接受到应用安装的广播");
            // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
                Log.d(TAG, "installapp    : " + packageName);
                Boolean isContains = false;
                for (int i = 0; i < myAppList.size(); i++) {
                    if (packageName.equals(myAppList.get(i).packageName)) {
                        isContains = true;
                        break;
                    }
                }
                if (mShowList.size() < MyAppsFragment.AT_MOST_SHOW_COUNT && !isContains) {
                    mShowList.add(PackageUtil.getAppInfo(mContext, packageName));
                    mMyAppsListDataUtil.saveShowList(mShowList);
                    Log.d(TAG, "onReceive: saveShowList_size : " + mShowList.size());
                }
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.d(TAG, "uninstallapp   " + packageName);
            }
            startLoad();
        }
    }

    @Override
    public void release() {
        if (mMyAppsListDataUtil != null) {
            mMyAppsListDataUtil = null;
        }
        if (mShowList != null) {
            mShowList.clear();
            mShowList = null;
        }
        if (systemApp != null) {
            systemApp.clear();
            systemApp = null;
        }
        if (mDrawables != null) {
            mDrawables.clear();
            mDrawables = null;
        }
    }


    public void topApp(int position) {
        AppInfo appInfo = mShowList.get(position);
        mShowList.remove(position);
        mShowList.add(2, appInfo);
        mView.loadAppInfoSuccess(mShowList, myAppList.size());
        mMyAppsListDataUtil.saveShowList(mShowList);
    }

    public void removeApp(int position) {
        mShowList.remove(position);
        mMyAppsListDataUtil.saveShowList(mShowList);
        mView.loadAppInfoSuccess(mShowList, myAppList.size());
    }

    public void unRegiestr() {
        if (mAppInstallReceiver != null) {
            mContext.unregisterReceiver(mAppInstallReceiver);
            mAppInstallReceiver = null;
        }
    }
}
