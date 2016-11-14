package com.can.appstore.active;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.can.appstore.R;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.utils.MD5Util;
import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.SystemUtil;

/**
 * Created by Atangs on 2016/11/2.
 *
 * 未对数据接口
 * 缺少应用安装，以及安装是否成功状态未设置
 */

public class ActivePresenter implements ActiveContract.TaskPresenter, DownloadTaskListener {
    private final static String TAG = "ActivePresenter";
    public final static String URL = "http://172.16.11.65:8080/download/20161018/F2_Launcher_V536_20161018191036.apk";

    private ActiveContract.OperationView mOperationView;
    private DownloadManager mDownloadManger;
    private Context mContext;
    private long mLastClickTime;

    public ActivePresenter(ActiveContract.OperationView operationView, Context context) {
        this.mOperationView = operationView;
        this.mContext = context;

        if (true) {
            initDownloadTask();
        } else {
            mOperationView.loadwebview("");
        }
    }

    //---------------------------- ActiveContract.TaskPresenter ----------------------------------
    private void initDownloadTask() {
        String downloadUrl = URL;
        mDownloadManger = DownloadManager.getInstance(mContext);
        DownloadTask downloadTask = mDownloadManger.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();
            if (status == DownloadStatus.DOWNLOAD_STATUS_INIT || status == DownloadStatus.DOWNLOAD_STATUS_CANCEL) {
                mOperationView.refreshTextProgressbarTextStatus(mContext.getString(R.string.active_click_participate));
            } else if (status == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                mOperationView.refreshTextProgressbarTextStatus(mContext.getString(R.string.active_app_download_waiting));
            } else if (status == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                mOperationView.refreshTextProgressbarTextStatus(mContext.getString(R.string.active_app_installing));
            } else {
                long completedSize = downloadTask.getCompletedSize();
                long totalSize = downloadTask.getTotalSize();
                mOperationView.refreshProgressbarProgress(calculatePercent(completedSize, totalSize));
            }
            mDownloadManger.addDownloadListener(downloadTask, ActivePresenter.this);
        } else {
            mOperationView.refreshTextProgressbarTextStatus(mContext.getString(R.string.active_click_participate));
        }
    }

    @Override
    public void clickBtnDownload() {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            mOperationView.showToast(mContext.getString(R.string.network_connection_disconnect));
        }
        long apkSize = (long) 11.9 * 1024 * 1024;
        String downloadUrl = URL;
        //需做按钮连续点击限制
        if (isFastContinueClickView()) {
            return;
        }

        //剩余内存大小判断
        if (!isMemoryAvailableSapce(apkSize)) {
            mOperationView.showToast(mContext.getString(R.string.download_faild_memory_lack));
            return;
        }

        DownloadTask downloadTask = mDownloadManger.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            //安装完成

            //安装中

            int status = downloadTask.getDownloadStatus();
            if (status == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                mOperationView.showToast("下载完成");
                return;
            }
            if (status == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING || status == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                mOperationView.showToast("已暂停下载");
                mDownloadManger.pause(downloadTask);
                return;
            }
            if (status == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                mDownloadManger.resume(downloadTask.getId());
                mOperationView.showToast("继续下载");
            }
        } else {
            downloadTask = new DownloadTask();
            String md5 = MD5.MD5(downloadUrl);
            downloadTask.setFileName(md5);
            downloadTask.setId(md5);
            downloadTask.setSaveDirPath(mContext.getExternalCacheDir()!=null?mContext.getExternalCacheDir().getPath()
                     + "/":"");
            downloadTask.setUrl(downloadUrl);
            mDownloadManger.addDownloadTask(downloadTask, ActivePresenter.this);
        }
    }

    private boolean isFastContinueClickView() {
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - mLastClickTime < 1500) {
            return true;
        }
        mLastClickTime = curClickTime;
        return false;
    }

    private boolean isMemoryAvailableSapce(long apkSize) {
        return SystemUtil.getSDCardAvailableSpace() - apkSize >= 100 * 1024 * 1024 && SystemUtil
                .getApkInstallDirUsableSpaceSzie(mContext) - apkSize >= 150 * 1024 * 1024;
    }

    private boolean isInstallAvailableSpace(long apkSize) {
        return SystemUtil.getApkInstallDirUsableSpaceSzie(mContext) - apkSize >= 150 * 1024 * 1024;
    }

    private float calculatePercent(long completedSize, long totalSize) {
        return totalSize == 0 ? 0 : (float) (completedSize * 100 / totalSize);
    }


    // -------------------------------- DownloadTaskListener Event -----------
    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.d(TAG, "onPrepare: " + downloadTask.getCompletedSize());
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
            mOperationView.refreshTextProgressbarTextStatus(mContext.getResources().getString(R.string
                    .active_app_download_waiting));
        }
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.d(TAG, "onStart: " + downloadTask.getCompletedSize());
        if (downloadTask.getCompletedSize() == 0) {
            mOperationView.refreshTextProgressbarTextStatus(mContext.getResources().getString(R.string
                    .active_app_download_waiting));
        }
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        Log.d(TAG, "onDownloading: " + downloadTask.getCompletedSize());
        if (downloadTask.getCompletedSize() == 0) {
            mOperationView.refreshTextProgressbarTextStatus(mContext.getResources().getString(R.string
                    .active_app_download_waiting));
            return;
        }
        mOperationView.refreshTextProgressbarTextStatus("");
        mOperationView.refreshProgressbarProgress(calculatePercent(downloadTask.getCompletedSize(), downloadTask.getTotalSize()));
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
            //调用安装

            mOperationView.refreshProgressbarProgress(0);
            mOperationView.refreshTextProgressbarTextStatus(mContext.getResources().getString
                    (R.string.active_app_installing));
    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {
        Log.d(TAG, "onError(downloadTask " + downloadTask.getCompletedSize() + ", errorCode)" + errorCode);
        switch (errorCode) {
            case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                mOperationView.showToast("未找到下载文件");
                break;
            case DOWNLOAD_ERROR_IO_ERROR:
                mOperationView.showToast("IO异常");
                break;
            case DOWNLOAD_ERROR_NETWORK_ERROR:
                mOperationView.showToast("网络异常，请重试！");
                break;
            case DOWNLOAD_ERROR_UNKONW_ERROR:
                mOperationView.showToast(mContext.getString(R.string.unkonw_error));
                break;
        }
    }
}
