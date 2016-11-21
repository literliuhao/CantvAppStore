package com.can.appstore.uninstallmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.appdetail.tempfile.CanDialog;
import com.can.appstore.uninstallmanager.csutom.CustomAsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;
import cn.can.tvlib.utils.ToastUtils;

/**
 * Created by JasonF on 2016/10/17.
 */

public class UninstallManagerPresenter implements UninstallManagerContract.Presenter, LoaderManager.LoaderCallbacks<List<PackageUtil.AppInfo>>, AppInstallListener {
    private static final String TAG = "UninstallManagerPresen";
    private static final int LOADER_ID = 0;
    private static final int COLUMN_COUNT = 3;
    private UninstallManagerContract.View mView;
    private Context mContext;
    private static List<PackageUtil.AppInfo> mAppInfoList;
    private AppInstallReceiver mInstalledReceiver;
    private ArrayList<String> mSelectPackageName = new ArrayList<>();
    private LoaderManager mLoaderManager;
    private DownloadManager mDownloadManager;
    private String mCurUninstallApkName = "";
    private CanDialog mCanDialog;

    public UninstallManagerPresenter(UninstallManagerContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
        initDownloadManager();
        addListener();
    }

    public void initDownloadManager() {
        mDownloadManager = DownloadManager.getInstance(mContext);
    }

    @Override
    public void startLoad(LoaderManager loaderManager) {
        this.mLoaderManager = loaderManager;
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    @Override
    public void addListener() {
        registerInstallReceiver();
        mDownloadManager.setAppInstallListener(UninstallManagerPresenter.this);
    }

    /**
     * 注册应用安装卸载的广播
     */
    private void registerInstallReceiver() {
        if (mInstalledReceiver == null) {
            mInstalledReceiver = new AppInstallReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_VIEW);
            filter.addDataScheme("package");
            mContext.registerReceiver(mInstalledReceiver, filter);
        }
    }

    /**
     * 计算当前的内存进度
     */
    void calculateCurStoragePropgress() {
        long freeSize = SystemUtil.getSDCardAvailableSpace();
        long totalSize = SystemUtil.getSDCardTotalSpace();
        int progress = (int) (((totalSize - freeSize) * 100) / totalSize);
        String freeStorage = mContext.getResources().getString(R.string.uninsatll_manager_free_storage) + StringUtils.formatFileSize(freeSize, false);
        mView.showCurStorageProgress(progress, freeStorage);
    }

    @Override
    public void onItemFocus(int position) {
        int rowNum = calculateCurRows(position);
        int total = calculateCurTotalRows();
        String rowFmt = String.format(mContext.getResources().getString(R.string.rows_str), rowNum, total);
        int pos = rowFmt.indexOf("/");
        SpannableStringBuilder spanString = new SpannableStringBuilder(rowFmt);
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#CCFFFFFF")), 0, pos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.refreshRows(spanString);
    }

    /**
     * 计算当前总行数
     */
    private int calculateCurTotalRows() {
        int totalCount = mAppInfoList.size();
        int totalRows = totalCount / COLUMN_COUNT;
        if (totalCount % COLUMN_COUNT != 0) {
            totalRows = totalRows + 1;
        }
        return totalRows;
    }

    /**
     * 计算当前行数
     */
    private int calculateCurRows(int position) {
        return position / COLUMN_COUNT + 1;
    }

    @Override
    public Loader<List<PackageUtil.AppInfo>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: " + " id : " + id);
        mView.showLoadingDialog();
        return new CustomAsyncTaskLoader(mContext, CustomAsyncTaskLoader.FILTER_THIRD_APP);
    }

    @Override
    public void onLoadFinished(Loader<List<PackageUtil.AppInfo>> loader, List<PackageUtil.AppInfo> data) {
        Log.d(TAG, "onLoadFinished: " + "data :" + data.size());
        mView.hideLoadingDialog();
        if (mAppInfoList == null) {
            mAppInfoList = new ArrayList<>();
        } else {
            mAppInfoList.clear();
        }
        mAppInfoList.addAll(data);
        mView.loadAllAppInfoSuccess(mAppInfoList);
    }


    @Override
    public void onLoaderReset(Loader<List<PackageUtil.AppInfo>> loader) {
        Log.d(TAG, "onLoaderReset: ");
    }

    @Override
    public void onInstalling(DownloadTask downloadTask) {
        Log.d(TAG, "onInstalling: ");
    }

    @Override
    public void onInstallSucess(String id) {
        Log.d(TAG, "onInstallSucess: ");
    }

    @Override
    public void onInstallFail(String id) {
        Log.d(TAG, "onInstallFail: ");
    }

    @Override
    public void onUninstallSucess(String id) {
        Log.d(TAG, "onUninstallSucess: " + mCurUninstallApkName);
        ToastUtils.showMessage(mContext, mCurUninstallApkName + mContext.getResources().getString(R.string.uninstall_success));
    }

    @Override
    public void onUninstallFail(String id) {
        Log.d(TAG, "onUninstallFail: " + mCurUninstallApkName);
        ToastUtils.showMessage(mContext, mCurUninstallApkName + mContext.getResources().getString(R.string.uninstall_fail));
    }

    class AppInstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
                Log.d(TAG, "install packageName : " + packageName);
                mLoaderManager.restartLoader(LOADER_ID, null, UninstallManagerPresenter.this);
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.d(TAG, "uninstall packageName : " + packageName);
                calculateCurStoragePropgress();
                refreshItemInListPosition(packageName);
                if (mSelectPackageName != null) {
                    if (mSelectPackageName.size() > 0) {
                        continueUninstall();
                    }
                }
            }
        }
    }

    private void refreshItemInListPosition(String packageName) {
        for (int j = 0; j < mAppInfoList.size(); j++) {
            if (packageName.equals(mAppInfoList.get(j).packageName)) {
                mAppInfoList.remove(j);
                mView.loadAllAppInfoSuccess(mAppInfoList);
            }
        }
    }

    /**
     * 继续卸载
     */
    private void continueUninstall() {
        mSelectPackageName.remove(0);
        mView.refreshSelectCount(mSelectPackageName.size());
        if (mSelectPackageName.size() > 0) {
            startUninstall();
        }
    }

    void unRegiestr() {
        if (mInstalledReceiver != null) {
            mContext.unregisterReceiver(mInstalledReceiver);
            mInstalledReceiver = null;
        }
    }

    /**
     * 批量卸载
     */
    public void batchUninstallApp(ArrayList<String> selectPackageList) {
        this.mSelectPackageName = selectPackageList;
        startUninstall();
    }

    /**
     * 开始卸载
     */
    public void startUninstall() {
        int curUninstallPosition = getCurUninstallPosition(mSelectPackageName.get(0));
        PackageUtil.AppInfo appInfo = mAppInfoList.get(curUninstallPosition);
        Log.d(TAG, "startUninstall: curUninstallPosition : " + curUninstallPosition + "  info : " + appInfo);
        dismissUninstallDialog();
        showUninstallDialog(appInfo.appIcon, appInfo.appName, appInfo.packageName);
    }

    /**
     * 显示卸载对话框
     *
     * @param drawable
     * @param name
     * @param packName
     */
    public void showUninstallDialog(Drawable drawable, final String name, final String packName) {
        String ok = mContext.getResources().getString(R.string.ok);
        String cancle = mContext.getResources().getString(R.string.cancle);
        String makesureUninstall = mContext.getResources().getString(R.string.makesure_uninstall_apk);
        mCanDialog = new CanDialog(mContext);
        mCanDialog.setmIvDialogTitle(drawable).setmTvDialogTitle(name).setmTvDialogTopLeftContent(makesureUninstall).setmBtnDialogNegative(cancle)
                .setmBtnDialogPositive(ok).setOnCanBtnClickListener(new CanDialog.OnCanBtnClickListener() {
            @Override
            public void onClickPositive() {
                silentUninstall(name, packName);
                dismissUninstallDialog();
            }

            @Override
            public void onClickNegative() {
                dismissUninstallDialog();
            }
        });
        mCanDialog.show();
    }

    /**
     * 隐藏卸载对话框
     */
    public void dismissUninstallDialog() {
        if (mCanDialog != null) {
            mCanDialog.dismiss();
        }
    }

    /**
     * 获取当前卸载包名位置应用名称
     *
     * @param packName
     * @return
     */
    public int getCurUninstallPosition(String packName) {
        int uninstallPosition = 0;
        for (int j = 0; j < mAppInfoList.size(); j++) {
            if (packName.equals(mAppInfoList.get(j).packageName)) {
                uninstallPosition = j;
            }
        }
        return uninstallPosition;
    }

    @Override
    public void release() {
        if (mAppInfoList != null) {
            mAppInfoList.clear();
            mAppInfoList = null;
        }
        if (mSelectPackageName != null) {
            mSelectPackageName.clear();
            mSelectPackageName = null;
        }
        mDownloadManager.removeAppInstallListener(this);
        mLoaderManager.destroyLoader(LOADER_ID);
    }

    /**
     * 静默卸载
     *
     * @param packageName
     */
    public void silentUninstall(String appName, String packageName) {
        mCurUninstallApkName = appName;
        mDownloadManager.uninstall(packageName);
    }

}
