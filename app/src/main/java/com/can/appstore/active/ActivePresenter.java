package com.can.appstore.active;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.entity.Activity;
import com.can.appstore.entity.Result;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.downloadlib.utils.SdcardUtils;
import cn.can.tvlib.utils.ApkUtils;
import cn.can.tvlib.utils.MD5Util;
import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PromptUtils;
import cn.can.tvlib.utils.StringUtils;
import retrofit2.Response;

import static com.can.appstore.R.string.active_app_installing;

/**
 * Created by Atangs on 2016/11/2.
 * <p>
 * 未对数据接口
 * 缺少应用安装，以及安装是否成功状态未设置
 */

public class ActivePresenter implements ActiveContract.TaskPresenter, DownloadTaskListener, AppInstallListener {
    private final static String TAG = "ActivePresenter";
    private DownloadTask mDownloadTask;
    private ActiveContract.OperationView mOperationView;
    private DownloadManager mDownloadManager;
    private Context mContext;
    private long mLastClickTime;
    private String mPkgName;
    private String mDownloadUrl;
    private CanCall<Result<Activity>> mActiveData;
    private int mAppSize;

    public ActivePresenter(ActiveContract.OperationView operationView, Context context) {
        this.mOperationView = operationView;
        this.mContext = context.getApplicationContext();

    }

    private void initDownloadTask(String downloadUrl) {
        mDownloadManager = DownloadManager.getInstance(mContext);
        DownloadTask downloadTask = mDownloadManager.addSingleTaskListener(MD5.MD5(downloadUrl), ActivePresenter.this,
                ActivePresenter.this);
        mDownloadTask = downloadTask;
        if (downloadTask != null) {
            if (downloadTask.getDownloadStatus() == APP_INSTALLING) {
                mOperationView.refreshTextProgressbarTextStatus(active_app_installing);
                return;
            }
            float downloadPercent = downloadTask.getPercent();
            if (downloadPercent > 1 && downloadPercent < 100) {
                mOperationView.refreshTextProgressbarTextStatus(R.string.active_null_str);
                mOperationView.refreshProgressbarProgress(downloadTask.getPercent());
            } else {
                mOperationView.refreshTextProgressbarTextStatus(R.string.active_click_participate);
            }
        } else {
            mOperationView.refreshTextProgressbarTextStatus(R.string.active_click_participate);
        }
    }

    //---------------------------- ActiveContract.TaskPresenter ----------------------------------
    @Override
    public void requestActiveData(String activeId) {
        mActiveData = HttpManager.getApiService().getActivityInfo(activeId);
        mActiveData.enqueue(new CanCallback<Result<Activity>>() {
            @Override
            public void onResponse(CanCall<Result<Activity>> call, Response<Result<Activity>> response) throws Exception {
                Result<Activity> info = response.body();
                if (info == null) {
                    mOperationView.showNetworkRetryView(true, false);
                    return;
                }
                if (info.getData() == null) {
                    return;
                }
                Activity active = info.getData();
                boolean isWebView = StringUtils.isEmpty(active.getUrl());
                mOperationView.showNetworkRetryView(false, isWebView);
                if (isWebView) {
                    mPkgName = active.getRecommend().getPackageName();
                    mDownloadUrl = active.getRecommend().getUrl();
                    mAppSize = active.getRecommend().getSize();
                    mOperationView.setNativeLayout(active.getBackground());
                    initDownloadTask(mDownloadUrl);
                } else {
                    mOperationView.loadwebview(active.getUrl());
                }
            }

            @Override
            public void onFailure(CanCall<Result<Activity>> call, CanErrorWrapper errorWrapper) {
                if (!NetworkUtils.isNetworkConnected(mContext.getApplicationContext())) {
                    mOperationView.showNetworkRetryView(true, false);
                }
            }
        });
    }

    @Override
    public void clickBtnDownload() {
        if (ApkUtils.isAvailable(mContext, mPkgName)) {
            PackageUtil.openApp(mContext, mPkgName);
            return;
        }
        String downloadUrl = mDownloadUrl;
        if (TextUtils.isEmpty(downloadUrl)) {
//            mOperationView.showToast("下载地址异常");
        }
        //需做按钮连续点击限制
        if (isFastContinueClickView()) {
            return;
        }

        DownloadTask downloadTask = mDownloadManager.getSigleTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();
            if (status == APP_INSTALL_FAIL) {
                if (MD5Util.getFileMD5(downloadTask.getFilePath()).equalsIgnoreCase(downloadTask.getId())) {
                    mDownloadManager.install(downloadTask);
                } else {
                    mOperationView.showToast(R.string.pares_install_apk_fail);
                }
                return;
            }
            if (status == APP_INSTALLING) {
                mOperationView.showToast(R.string.installing);
                return;
            }
            //
            if (status == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                mDownloadManager.cancel(downloadTask);
                mDownloadManager.singleTask(downloadTask, ActivePresenter.this);
            }
        } else {
            downloadTask = new DownloadTask();
            String md5 = MD5.MD5(downloadUrl);
            downloadTask.setFileName(md5);
            downloadTask.setId(md5);
            downloadTask.setUrl(downloadUrl);
            mDownloadManager.singleTask(downloadTask, ActivePresenter.this);
            mDownloadManager.setAppInstallListener(ActivePresenter.this);
        }
        mDownloadTask = downloadTask;
    }

    @Override
    public void release() {
        removeAllListener();
        if (mActiveData != null && !mActiveData.isCanceled()) {
            mActiveData.cancel();
            mActiveData = null;
        }
    }


    // -------------------------------- DownloadTaskListener Event -----------
    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.d(TAG, "onPrepare: " + downloadTask.getCompletedSize());
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
            if (downloadTask.getId().equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
                mOperationView.refreshTextProgressbarTextStatus(R.string.active_null_str);
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
            mOperationView.refreshProgressbarProgress(downloadTask.getPercent());
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
    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {
        Log.d(TAG, "onError(downloadTask " + downloadTask.getCompletedSize() + ", errorCode)" + errorCode);
        if (downloadTask.getId().equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            switch (errorCode) {
                case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                    mOperationView.showToast(R.string.downlaod_error);
                    break;
                case DOWNLOAD_ERROR_IO_ERROR:
                    mOperationView.showToast(R.string.downlaod_error);
                    break;
                case DOWNLOAD_ERROR_NETWORK_ERROR:
                    mOperationView.showToast(R.string.network_connection_error);
                    break;
                case DOWNLOAD_ERROR_UNKONW_ERROR:
                    mOperationView.showToast(R.string.unkonw_error);
                    break;
            }
            if (errorCode != DOWNLOAD_ERROR_NETWORK_ERROR) {
                mOperationView.refreshProgressbarProgress(0);
                mOperationView.refreshTextProgressbarTextStatus(R.string.downlaod_restart);
            }
        }
    }

    //<!-----------------AppInstallListener--------------------->
    @Override
    public void onInstalling(DownloadTask downloadTask) {
        Log.d(TAG, "onInstalling(downloadTask " + downloadTask.getCompletedSize() + ")");
        if (downloadTask.getId().equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            mOperationView.refreshProgressbarProgress(0);
            mOperationView.refreshTextProgressbarTextStatus(active_app_installing);
        }
    }

    @Override
    public void onInstallSucess(String id) {
        Log.d(TAG, "onInstallSucess(id " + id + ")");
        if (id.equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            mOperationView.refreshTextProgressbarTextStatus(R.string.active_click_participate);
            mOperationView.showToast(R.string.install_success);
        }
    }

    @Override
    public void onInstallFail(String id) {
        Log.d(TAG, "onInstallFail(id " + id + ")");
        //调用重新安装
        if (id.equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            mOperationView.refreshTextProgressbarTextStatus(R.string.downlaod_restart);
            mOperationView.showToast(R.string.install_fail);
        }
    }

    @Override
    public void onUninstallSucess(String id) {

    }

    @Override
    public void onUninstallFail(String id) {

    }

    private boolean isFastContinueClickView() {
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - mLastClickTime < 1500) {
            return true;
        }
        mLastClickTime = curClickTime;
        return false;
    }

    private void removeAllListener() {
        if (mDownloadManager != null) {
            mDownloadManager.removeAppInstallListener(ActivePresenter.this);
            if (mDownloadTask != null) {
                mDownloadManager.removeDownloadListener(mDownloadTask, ActivePresenter.this);
                mDownloadTask = null;
            }
            mDownloadManager = null;
        }
    }

//    private void startApk() {
//        String packageName = mPkgName;
//        PackageManager pm = mContext.getPackageManager();
//        Intent intent = pm.getLaunchIntentForPackage(packageName);
//        if (intent == null) {
////            mOperationView.showToast("未找到该应用");
//            return;
//        }
//        mContext.startActivity(intent);
//    }

    /**
     * 静默安装
     *
     */
//    public void silentInstall(String taskId) {
//        Log.d(TAG, "silentInstall ");
////        if (SdcardUtils.getApkInstallDirSzie(mContext) <= (50 * 1024 * 1024 +  mAppSize * 1.5)) {  // 安装内存不足
////            mOperationView.showToast(mContext.getString(R.string.install_faild_memory_lack));
////            return;
////        }
//        mDownloadManager.install(mDownloadManager.getSigleTaskById(taskId));
//    }
}
