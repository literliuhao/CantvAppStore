package com.can.appstore.myapps.allappsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.myapps.utils.MyAppsListDataUtil;

import java.util.List;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.tvlib.ui.widgets.LoadingDialog;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.ToastUtils;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by wei on 2016/11/8.
 */

public class AllAppsPresenter implements AllAppsContract.Presenter, AppInstallListener {


    private LoadingDialog mLoadingDialog;
    private AllAppsContract.View mView;
    private MyAppsListDataUtil mMyAppsListDataUtil;
    private List<PackageUtil.AppInfo> allAppsList;
    private Context mContext;

    private AppInstallReceiver mAppInstallReceiver;

    private DownloadManager mDownloadManager;
    private String mUninstallApkName;

    public AllAppsPresenter(AllAppsContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
        initDownManger();
    }

    private void initDownManger() {
        mDownloadManager = DownloadManager.getInstance(mContext);
    }


    @Override
    public void startLoad() {
        new AsyncTask<Void, Void, Void>() {
            //加载数据之前
            @Override
            protected void onPreExecute() {
                mView.showLoading();
            }

            //加载数据
            @Override
            protected Void doInBackground(Void... params) {
                mMyAppsListDataUtil = new MyAppsListDataUtil(mContext);
                allAppsList = mMyAppsListDataUtil.getAllAppList(allAppsList);
                return null;
            }

            //加载完数据
            @Override
            protected void onPostExecute(Void aVoid) {
                mView.loadAllAppInfoSuccess(allAppsList);
                mView.hideLoading();
                removeHideApps();
            }
        }.execute();

    }

    private void removeHideApps() {
        allAppsList = mMyAppsListDataUtil.removeHideApp(allAppsList);
        mView.loadAllAppInfoSuccess(allAppsList);
    }

    @Override
    public void addListener() {
        registerInstallReceiver();
    }

    @Override
    public void release() {
        if (mMyAppsListDataUtil != null) {
            mMyAppsListDataUtil = null;
        }
        if (allAppsList != null) {
            allAppsList.clear();
            allAppsList = null;
        }
        hideLoading();
    }

    public void showLoading(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.px80));
            mLoadingDialog.setMessage(msg);
            mLoadingDialog.show();
        }
    }

    public void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }



    /**
     * 注册应用安装卸载的广播
     */
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

    @Override
    public void onInstalling(DownloadTask downloadTask) {
    }

    @Override
    public void onInstallSucess(String id) {
    }

    @Override
    public void onInstallFail(String id) {
    }

    @Override
    public void onUninstallSucess(String id) {
        Log.d(TAG, "onUninstallSucess: " + mUninstallApkName);
        ToastUtils.showMessage(mContext, mUninstallApkName + mContext.getResources().getString(R.string.uninstall_success));
    }

    @Override
    public void onUninstallFail(String id) {
        Log.d(TAG, "onUninstallFail: " + mUninstallApkName);
        ToastUtils.showMessage(mContext, mUninstallApkName + mContext.getResources().getString(R.string.uninstall_fail));
    }


    class AppInstallReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
                Log.d(TAG, "install packageName : " + packageName);
                startLoad();
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.d(TAG, "uninstall packageName : " + packageName);
                refreshItemInListPosition(packageName);
            }
        }
    }

    private void refreshItemInListPosition(String packageName) {
        for (int j = 0; j < allAppsList.size(); j++) {
            if (packageName.equals(allAppsList.get(j).packageName)) {
                allAppsList.remove(j);
                mView.loadAllAppInfoSuccess(allAppsList);
            }
        }
    }

    public void getUninstallAppInfo(int position) {
        mView.showUninstallDialog(allAppsList.get(position));
    }

    /**
     * 计算当前总行数
     *
     * @return
     */
    public int calculateCurTotalRows() {
        int totalCount = allAppsList.size();
        int totalRows = totalCount / 5;
        if (totalCount % 5 != 0) {
            totalRows = totalRows + 1;
        }
        return totalRows;
    }

    /**
     * 计算当前行数
     *
     * @param position
     * @return
     */
    public int calculateCurRows(int position) {
        return position / 5 + 1;
    }

    public void startApp(int position) {
        PackageUtil.openApp(mContext, allAppsList.get(position).packageName);
    }

    public void unRegiestr() {
        if (mAppInstallReceiver != null) {
            mContext.unregisterReceiver(mAppInstallReceiver);
            mAppInstallReceiver = null;
        }
    }


    /**
     * 静默卸载
     *
     * @param packageName
     */
    public void silentUninstall(String appName, String packageName) {
        mUninstallApkName = appName;
        mDownloadManager.uninstall(packageName);
    }
}
