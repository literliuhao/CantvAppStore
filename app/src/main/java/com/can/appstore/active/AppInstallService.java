package com.can.appstore.active;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.downloadlib.NetworkUtils;
import cn.can.downloadlib.utils.SdcardUtils;
import cn.can.downloadlib.utils.ShellUtils;
import cn.can.downloadlib.utils.ToastUtils;
import cn.can.tvlib.utils.ApkUtils;
import cn.can.tvlib.utils.MD5Util;
import cn.can.tvlib.utils.PackageUtil;

import static com.can.appstore.MyApp.mContext;
import static com.can.appstore.R.string.active_app_installing;


public class AppInstallService extends Service implements DownloadTaskListener {
    private final static String TAG = "AppInstallService";
    public static final int APP_INSTALLING = 8;
    public static final int APP_INSTALL_SUCESS = 9;
    public static final int APP_INSTALL_FAIL = 10;
    private long mLimitSpace = 50;
    private ActiveActivity mActiveActivity;
    private DownloadManager mDownloadManager;
    private DownloadTask mDownloadTask;
    private long mLastClickTime;
    private String mDownloadUrl;

    public AppInstallService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new InstallBinder();
    }

    public void initDownloadTask(String downloadUrl) {
        Log.d(TAG, "初始化下载状态");
        mDownloadUrl = downloadUrl;
        mDownloadManager = DownloadManager.getInstance(this.getApplicationContext());
        DownloadTask downloadTask = mDownloadManager.addSingleTaskListener(MD5.MD5(downloadUrl), this);
        mDownloadTask = downloadTask;
        if (downloadTask != null) {
            if (downloadTask.getDownloadStatus() == APP_INSTALLING) {
                mActiveActivity.refreshTextProgressbarTextStatus(active_app_installing);
                return;
            }
            float downloadPercent = downloadTask.getPercent();
            if (downloadPercent > 1 && downloadPercent < 100) {
                mActiveActivity.refreshTextProgressbarTextStatus(R.string.active_null_str);
                mActiveActivity.refreshProgressbarProgress(downloadTask.getPercent());
            } else {
                mActiveActivity.refreshTextProgressbarTextStatus(R.string.active_click_participate);
            }
        } else {
            mActiveActivity.refreshTextProgressbarTextStatus(R.string.active_click_participate);
        }
    }

    public void clickBtnDownload(AppInfo appInfo) {
        //需做按钮连续点击限制
        if (isFastContinueClickView()) {
            return;
        }
        if (ApkUtils.isAvailable(mContext, appInfo.getPackageName())) {
            PackageUtil.openApp(mContext, appInfo.getPackageName());
            return;
        }
        if (!NetworkUtils.isNetworkConnected(this.getApplicationContext())) {
            mActiveActivity.showToast(R.string.network_connection_disconnect);
            return;
        }
        String downloadUrl = appInfo.getUrl();
        mDownloadUrl = downloadUrl;
        if (TextUtils.isEmpty(downloadUrl)) {
//            mOperationView.showToast("下载地址异常");
        }

        DownloadTask downloadTask = mDownloadManager.getSigleTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();
            if (status == APP_INSTALL_FAIL) {
                if (MD5Util.getFileMD5(downloadTask.getFilePath()).equalsIgnoreCase(downloadTask.getId())) {
                    installApk(downloadTask);
                } else {
                    mActiveActivity.refreshTextProgressbarTextStatus(R.string.downlaod_restart);
                    mActiveActivity.showToast(R.string.install_apk_fail);
                }
                return;
            }
            if (status == APP_INSTALLING) {
                mActiveActivity.showToast(R.string.install_installing);
                return;
            }
            //
            if (status == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                mDownloadManager.cancel(downloadTask);
                mDownloadManager.singleTask(downloadTask, this);
            }
        } else {
            downloadTask = new DownloadTask();
            String md5 = MD5.MD5(downloadUrl);
            downloadTask.setFileName(md5);
            downloadTask.setId(md5);
            downloadTask.setUrl(downloadUrl);
            mDownloadManager.singleTask(downloadTask, this);
        }
        mDownloadTask = downloadTask;
    }

    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.d(TAG, "onPrepare: " + downloadTask.getCompletedSize());
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
            if (downloadTask.getId().equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
                mDownloadTask = downloadTask;
                mActiveActivity.refreshTextProgressbarTextStatus(R.string.active_null_str);
            }
        }
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.d(TAG, "onStart: " + downloadTask.getCompletedSize());
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        Log.d(TAG, "onDownloading: " + downloadTask.getCompletedSize());
        if (downloadTask.getId().equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            mDownloadTask = downloadTask;
            mActiveActivity.refreshProgressbarProgress(downloadTask.getPercent());
        }
    }

    @Override
    public void onPause(DownloadTask downloadTask) {
        Log.d(TAG, "onPause: " + downloadTask.getCompletedSize());
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {
        Log.d(TAG, "onCancel: " + downloadTask.getCompletedSize());
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        Log.d(TAG, "onCompleted: " + downloadTask.getCompletedSize());
        if (downloadTask.getId().equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            mActiveActivity.refreshProgressbarProgress(0);
            mActiveActivity.refreshTextProgressbarTextStatus(active_app_installing);
            installApk(downloadTask);
            release();
        }
    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {
        Log.d(TAG, "onError(downloadTask " + downloadTask.getCompletedSize() + ", errorCode)" + errorCode);
        if (downloadTask.getId().equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            switch (errorCode) {
                case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                    mActiveActivity.showToast(R.string.downlaod_error);
                    break;
                case DOWNLOAD_ERROR_IO_ERROR:
                    mActiveActivity.showToast(R.string.downlaod_error);
                    break;
                case DOWNLOAD_ERROR_NETWORK_ERROR:
                    mActiveActivity.showToast(R.string.network_connection_error);
                    break;
                case DOWNLOAD_ERROR_UNKONW_ERROR:
                    mActiveActivity.showToast(R.string.unkonw_error);
                    break;
            }
            if (errorCode != DOWNLOAD_ERROR_NETWORK_ERROR) {
                mActiveActivity.refreshProgressbarProgress(0);
                mActiveActivity.refreshTextProgressbarTextStatus(R.string.downlaod_restart);
            }
        }
    }

    public void release() {
        if (mDownloadManager != null) {
            mDownloadManager.removeDownloadListener(mDownloadTask, this);
        }
    }

    public boolean slientInstall(String path) {
        long space = SdcardUtils.getSDCardAvailableSpace() / 1014 / 1024;

        if (space < mLimitSpace) {
            ToastUtils.showMessageLong(mContext.getApplicationContext(), cn.can.downloadlib.R.string.error_msg);
            return false;
        }
        ShellUtils.CommandResult res = ShellUtils.execCommand("pm install " + path, false);
        return res.result == 0;
    }

    public void installApk(DownloadTask downloadTask) {
        boolean isInstall = slientInstall(downloadTask.getSaveDirPath());
        if (isInstall) {
            Log.d(TAG, "安装成功");
            mActiveActivity.refreshTextProgressbarTextStatus(R.string.active_click_participate);
            mDownloadManager.deleteSigleTask(downloadTask.getId());
        } else {
            Log.d(TAG, "安装失败");
            mActiveActivity.refreshTextProgressbarTextStatus(R.string.downlaod_restart);
        }
        downloadTask.setDownloadStatus(isInstall ? APP_INSTALL_SUCESS : APP_INSTALL_FAIL);
    }

    private boolean isFastContinueClickView() {
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - mLastClickTime < 1500) {
            return true;
        }
        mLastClickTime = curClickTime;
        return false;
    }

    class InstallBinder extends Binder {
        public AppInstallService getInstallService() {
            return AppInstallService.this;
        }

        public DownloadTaskListener getDownloadTaskListener() {
            return AppInstallService.this;
        }

        public void setActivity(ActiveActivity activity) {
            mActiveActivity = activity;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
