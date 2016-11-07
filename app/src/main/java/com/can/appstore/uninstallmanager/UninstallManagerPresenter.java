package com.can.appstore.uninstallmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.CheckBox;

import com.can.appstore.R;
import com.can.appstore.appdetail.AppInfo;
import com.can.appstore.appdetail.AppUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import cn.can.tvlib.ui.widgets.LoadingDialog;

/**
 * Created by JasonF on 2016/10/17.
 */

public class UninstallManagerPresenter implements UninstallManagerContract.Presenter {
    private static final String TAG = "UninstallManagerPresen";
    private UninstallManagerContract.View mView;
    private Context mContext;
    private static List<AppInfo> mAppInfoList;
    private AppInstallReceiver mInstalledReceiver;
    private LoadingDialog mLoadingDialog;
    private BroadcastReceiver mHomeReceivcer;
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
                mView.showLoading();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (mAppInfoList != null) {
                    mAppInfoList.clear();
                }
                mAppInfoList = AppUtils.findAllInstallApkInfo(mContext);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mView.loadAllAppInfoSuccess(mAppInfoList);
                mView.hideLoading();
            }
        }.execute();
    }

    public void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public void showLoading(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.dimen_80px));
            mLoadingDialog.setMessage(msg);
            mLoadingDialog.show();
        }
    }

    @Override
    public void addListener() {
        registerInstallReceiver();
        registHomeBoradCast();
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
        long freeSize = AppUtils.getSDAvaliableSize();
        long totalSize = AppUtils.getSDTotalSize();
        int progress = (int) ((freeSize * 100) / totalSize);
        Log.d(TAG, "freeSize : " + freeSize + "  totalSize : " + totalSize + "  progress : " + progress);
        String freeStorage = mContext.getResources().getString(R.string.uninsatll_manager_free_storage) + AppUtils.FormetFileSize(freeSize);
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

    /**
     * 注册按主页键的广播
     */
    private void registHomeBoradCast() {
        mHomeReceivcer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    mView.onClickHomeKey();
                    return;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.registerReceiver(mHomeReceivcer, filter);
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
            if (packageName.equals(mAppInfoList.get(j).getPackageName())) {
                mAppInfoList.remove(j);
                mView.loadAllAppInfoSuccess(mAppInfoList);
            }
        }
    }

    private void continueUninstall1() {
        mSelectPackageName.remove(0);
        mView.refreshSelectCount(mSelectPackageName.size());
        if (mSelectPackageName.size() > 0) {
            AppUtils.uninstallpkg(mContext, mSelectPackageName.get(0));
        }
    }

    public void unRegiestr() {
        if (mInstalledReceiver != null) {
            mContext.unregisterReceiver(mInstalledReceiver);
            mInstalledReceiver = null;
        }
        if (mHomeReceivcer != null) {
            mContext.unregisterReceiver(mHomeReceivcer);
            mHomeReceivcer = null;
        }
    }

    public void batchUninstallApp(ArrayList<String> selectPackageList) {
        this.mSelectPackageName = selectPackageList;
        AppUtils.uninstallpkg(mContext, mSelectPackageName.get(0));
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
        hideLoading();
    }
}
