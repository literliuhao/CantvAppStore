package com.can.appstore.uninstallmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.appdetail.AppUtils;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;

/**
 * Created by JasonF on 2016/10/17.
 */

public class UninstallManagerPresenter implements UninstallManagerContract.Presenter {
    private static final String TAG = "UninstallManagerPresen";
    private UninstallManagerContract.View mView;
    private Context mContext;
    private static List<PackageUtil.AppInfo> mAppInfoList;
    private AppInstallReceiver mInstalledReceiver;
    private ArrayList<String> mSelectPackageName = new ArrayList<String>();

    public UninstallManagerPresenter(UninstallManagerContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void startLoad() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mView.showLoadingDialog();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (mAppInfoList != null) {
                    mAppInfoList.clear();
                }
                mAppInfoList = PackageUtil.findAllThirdPartyApps(mContext, mAppInfoList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mView.loadAllAppInfoSuccess(mAppInfoList);
                mView.hideLoadingDialog();
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
     *
     * @return
     */
    public void calculateCurStoragePropgress() {
        long freeSize = SystemUtil.getSDCardAvailableSpace();
        long totalSize = SystemUtil.getSDCardTotalSpace();
        int progress = (int) ((freeSize * 100) / totalSize);
        Log.d(TAG, "freeSize : " + freeSize + "  totalSize : " + totalSize + "  progress : " + progress);
        String freeStorage = mContext.getResources().getString(R.string.uninsatll_manager_free_storage) + StringUtils.formatFileSize(freeSize, false);
        mView.showCurStorageProgress(progress, freeStorage);
    }

    /**
     * 计算当前总行数
     *
     * @return
     */
    public int calculateCurTotalRows() {
        int totalCount = mAppInfoList.size();
        int totalRows = totalCount / 3;
        if (totalCount % 3 != 0) {
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
        return position / 3 + 1;
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
                if (mSelectPackageName != null) {
                    refreshItemInListPosition(packageName);
                    continueUninstall1();
                } else {
                    startLoad();
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
    private void continueUninstall1() {
        if (mSelectPackageName.size() > 0) {
            mSelectPackageName.remove(0);
            mView.refreshSelectCount(mSelectPackageName.size());
            PackageUtil.unInstall(mContext, mSelectPackageName.get(0));
        }
    }

    public void unRegiestr() {
        if (mInstalledReceiver != null) {
            mContext.unregisterReceiver(mInstalledReceiver);
            mInstalledReceiver = null;
        }
    }

    /**
     * 批量卸载
     *
     * @param selectPackageList
     */
    public void batchUninstallApp(ArrayList<String> selectPackageList) {
        this.mSelectPackageName = selectPackageList;
        PackageUtil.unInstall(mContext, mSelectPackageName.get(0));
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
    }
}
