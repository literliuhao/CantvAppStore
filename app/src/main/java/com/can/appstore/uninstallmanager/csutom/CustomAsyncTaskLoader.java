package com.can.appstore.uninstallmanager.csutom;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.can.appstore.AppConstants;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil;


/**
 * Created by JasonF on 2016/11/16.
 */
public class CustomAsyncTaskLoader extends AsyncTaskLoader<List<PackageUtil.AppInfo>> {
    private static final String TAG = "CustomAsyncTaskLoader";
    public static final int FILTER_ALL_APP = 0; // 所有应用程序
    public static final int FILTER_SYSTEM_APP = 1; // 系统程序
    public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
    public static final int FILTER_PRE_INSTALL_THIRD_APP = 3; // 获取处于白名单中的系统应用 + 第三方应用
    public static final int FILTER_LOSE_PRE_INSTALL_THIRD_APP = 4; // 忽略处于白名单中的系统应用 + 第三方应用
    private Context mContext;
    private int mAppsType;
    private List<PackageUtil.AppInfo> mAppinfos;
    private List<String> mAppWhiteList = new ArrayList<>();

    public CustomAsyncTaskLoader(Context contex, int getAppsType) {
        super(contex);
        this.mContext = contex;
        this.mAppsType = getAppsType;
    }

    @Override
    public List<PackageUtil.AppInfo> loadInBackground() {
        switch (mAppsType) {
            case FILTER_ALL_APP:
                mAppinfos = PackageUtil.findAllApps(mContext, mAppinfos);
                break;
            case FILTER_SYSTEM_APP:
                mAppinfos = PackageUtil.findAllSystemApps(mContext, mAppinfos);
                break;
            case FILTER_THIRD_APP:
                mAppinfos = PackageUtil.findAllThirdPartyApps(mContext, mAppinfos);
                break;
            case FILTER_PRE_INSTALL_THIRD_APP:
                mAppinfos = PackageUtil.findAllComplexApps(mContext, mAppinfos, mAppWhiteList);
                break;
            case FILTER_LOSE_PRE_INSTALL_THIRD_APP:
                mAppinfos = PackageUtil.findLoseWhiteAllComplexApps(mContext, mAppinfos, AppConstants.PRE_APPS);
                break;
            default:
                break;
        }
        Log.d(TAG, "loadInBackground: mAppinfos = " + mAppinfos);
        return mAppinfos;
    }

    @Override
    public void onStartLoading() {
        Log.d(TAG, "onStartLoading()");
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public void deliverResult(List<PackageUtil.AppInfo> data) {
        Log.d(TAG, "deliverResult()");
        if (isReset()) {
            if (data != null) {
                releaseResources(data);
            }
        }
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    public void onStopLoading() {
        Log.d(TAG, "onStopLoading()");
        cancelLoad();
        super.onStopLoading();
    }

    @Override
    public void onReset() {
        Log.d(TAG, "onReset()");
        super.onReset();
    }

    @Override
    public void onCanceled(List<PackageUtil.AppInfo> data) {
        super.onCanceled(data);
        Log.d(TAG, "onCanceled()");
        releaseResources(data);
    }

    private void releaseResources(List<PackageUtil.AppInfo> data) {
        if (data != null && data.size() > 0) {
            data.clear();
            data = null;
        }
    }
}
