package com.can.appstore.myapps.myappsfragmview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.can.appstore.myapps.model.MyAppsListDataUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.PackageUtil.AppInfo;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by wei on 2016/11/9.
 */

public class MyAppsFragPresenter implements MyAppsFramentContract.Presenter {
    MyAppsFramentContract.View mView;
    Context mContext;

    MyAppsListDataUtil mMyAppsListDataUtil;

    AppInstallReceiver mAppInstallReceiver;


//    //本地全部的第三方应用
//    List<PackageUtil.AppInfo> mAppsList = new ArrayList<AppInfo>();
    //主页显示的第三方应用
    List<AppInfo> mShowList = new ArrayList<AppInfo>(18);
    //系统应用的icon
    List<Drawable> mDrawables = new ArrayList<Drawable>();


    public MyAppsFragPresenter(MyAppsFramentContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void startLoad() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //初始化数据
                mMyAppsListDataUtil = new MyAppsListDataUtil(mContext);
                mShowList = mMyAppsListDataUtil.getShowList(mShowList);
                List<AppInfo> systemApp = mMyAppsListDataUtil.getSystemApp(null);
                if(mDrawables.size()!= 0){
                    mDrawables.clear();
                }
                for (int i = 0; i < systemApp.size(); i++) {
                    mDrawables.add(systemApp.get(i).appIcon);
                }
                Log.i("MYSHOWLIST","------"+mShowList.size());
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                mView.loadAppInfoSuccess(mShowList,mDrawables);
            }
        }.execute();


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
            // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
                Log.d(TAG, "install packageName : " + packageName);
                startLoad();
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.d(TAG, "uninstall packageName : " + packageName);
                int position = 0;
                for (int i = 0; i < mShowList.size(); i++) {
                    if (packageName.equals(mShowList.get(i).packageName)) {
                        position = i;
                    }
                }
                removeApp(position);
            }
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
//        if (mAppsList != null) {
//            mAppsList.clear();
//            mShowList = null;
//        }
    }




    public void topApp(int position) {
        AppInfo appInfo = mShowList.get(position);
        mShowList.remove(position);
        mShowList.add(2, appInfo);
        mView.loadAppInfoSuccess(mShowList,mDrawables);
        mMyAppsListDataUtil.saveShowList(mShowList);
    }

    public void removeApp(int position) {
        mShowList.remove(position);
        mMyAppsListDataUtil.saveShowList(mShowList);
        mView.loadAppInfoSuccess(mShowList,mDrawables);
    }

    public void unRegiestr() {
        if (mAppInstallReceiver != null) {
            mContext.unregisterReceiver(mAppInstallReceiver);
            mAppInstallReceiver = null;
        }
    }

}
