package com.can.appstore.uninstallmanager.csutom;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.can.appstore.AppConstants;
import com.can.appstore.entity.SelectedAppInfo;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil;


/**
 * Created by JasonF on 2016/11/16.
 */
public class CustomAsyncTaskLoader extends AsyncTaskLoader<List<SelectedAppInfo>> {
    private static final String TAG = "CustomAsyncTaskLoader";
    public static final int FILTER_ALL_APP = 0; // 所有应用程序
    public static final int FILTER_SYSTEM_APP = 1; // 系统程序
    public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
    public static final int FILTER_PRE_INSTALL_THIRD_APP = 3; // 获取处于白名单中的系统应用 + 第三方应用
    public static final int FILTER_LOSE_PRE_INSTALL_THIRD_APP = 4; // 忽略处于白名单中的系统应用 + 第三方应用
    private Context mContext;
    private int mAppsType;
    private List<String> mAppWhiteList = new ArrayList<>();

    public CustomAsyncTaskLoader(Context contex, int getAppsType) {
        super(contex);
        this.mContext = contex;
        this.mAppsType = getAppsType;
    }

    @Override
    public List<SelectedAppInfo> loadInBackground() {
        List<PackageUtil.AppInfo> appinfos = new ArrayList<>();
        switch (mAppsType) {
            case FILTER_ALL_APP:
                PackageUtil.findAllApps(mContext, appinfos);
                break;
            case FILTER_SYSTEM_APP:
                PackageUtil.findAllSystemApps(mContext, appinfos);
                break;
            case FILTER_THIRD_APP:
                PackageUtil.findAllThirdPartyApps(mContext, appinfos);
                break;
            case FILTER_PRE_INSTALL_THIRD_APP:
                PackageUtil.findAllComplexApps(mContext, appinfos, mAppWhiteList);
                break;
            case FILTER_LOSE_PRE_INSTALL_THIRD_APP:
                PackageUtil.findLoseWhiteAllComplexApps(mContext, appinfos, AppConstants.PRE_APPS);
                break;
            default:
                break;
        }
        Log.d(TAG, "loadInBackground: appinfos = " + appinfos);
        ArrayList<SelectedAppInfo> selectedAppInfos = new ArrayList<>();
        for(PackageUtil.AppInfo appInfo : appinfos) {
            selectedAppInfos.add(SelectedAppInfo.wrap(appInfo));
        }
        return selectedAppInfos;
    }

    @Override
    public void onStartLoading() {
        Log.d(TAG, "onStartLoading()");
        forceLoad();
        super.onStartLoading();
    }

    @Override
    public void deliverResult(List<SelectedAppInfo> data) {
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
    public void onCanceled(List<SelectedAppInfo> data) {
        super.onCanceled(data);
        Log.d(TAG, "onCanceled()");
        releaseResources(data);
    }

    private void releaseResources(List<SelectedAppInfo> data) {
        if (data != null && data.size() > 0) {
            data.clear();
            data = null;
        }
    }
}
