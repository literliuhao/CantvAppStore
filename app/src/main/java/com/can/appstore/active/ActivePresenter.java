package com.can.appstore.active;


import android.content.Context;
import android.util.Log;

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
 */

public class ActivePresenter implements ActiveContract.TaskPresenter, DownloadTaskListener {
    public final static String URL = "http://app.znds.com/down/20160909/dsj2.0-2.9.1-dangbei.apk";

    private ActiveContract.OperationView mOperationView;
    private DownloadManager mDownloadManger;
    private Context mContext;
    private long mLastClickTime;

    public ActivePresenter(ActiveContract.OperationView operationView, Context context) {
        this.mOperationView = operationView;
        this.mContext = context;
    }

    //---------------------------- ActiveContract.TaskPresenter ----------------------------------
    @Override
    public void initDownloadTask() {
        String downloadUrl = URL;
        mDownloadManger = DownloadManager.getInstance(mContext);
        DownloadTask downloadTask = mDownloadManger.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();
            if (status == DownloadStatus.DOWNLOAD_STATUS_INIT || status == DownloadStatus.DOWNLOAD_STATUS_CANCEL) {
                mOperationView.refreshTextProgressbarTextStatus("点击参与活动");
            } else if (status == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                mOperationView.refreshTextProgressbarTextStatus("等待中");
            } else if (status == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                mOperationView.refreshTextProgressbarTextStatus("安装中");
            } else {
                long completedSize = downloadTask.getCompletedSize();
                long totalSize = downloadTask.getTotalSize();
                mOperationView.refreshProgressbarProgress(calculatePercent(completedSize, totalSize));
            }
            mDownloadManger.addDownloadListener(downloadTask, ActivePresenter.this);
        } else {
            mOperationView.refreshTextProgressbarTextStatus("点击参与活动");
        }
    }

    @Override
    public void startDownload() {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            mOperationView.showToast("网络未连接");
        }
        long apkSize = (long) 11.9 * 1024 * 1024;
        String downloadUrl = URL;
        //需做按钮连续点击限制
        if (isFastContinueClickView()) {
            return;
        }

        //剩余内存大小判断
        if (!isMemoryAvailableSapce(apkSize)) {
            mOperationView.showToast("内存不足，无法进行下载");
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
            downloadTask.setSaveDirPath(mContext.getExternalCacheDir().getPath() + "/");
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
        return totalSize == 0 ? 0 : (float) (completedSize * 100f / totalSize);
    }


    // -------------------------------- DownloadTaskListener Event -----------
    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.d("fu", "onPrepare: " + downloadTask.getCompletedSize());
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
            mOperationView.refreshTextProgressbarTextStatus("等待中");
        }
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.d("fu", "onStart: " + downloadTask.getCompletedSize());
        if (downloadTask.getCompletedSize() == 0) {
            mOperationView.refreshTextProgressbarTextStatus("等待中");
        }
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        Log.d("fu", "onDownloading: " + downloadTask.getCompletedSize());
        mOperationView.refreshTextProgressbarTextStatus("");
        mOperationView.refreshProgressbarProgress(calculatePercent(downloadTask.getCompletedSize(), downloadTask.getTotalSize()));
    }

    @Override
    public void onPause(DownloadTask downloadTask) {
//        if (downloadTask!=null){
//            mOperationView.updateProgressbarProgress(calculatePercent(downloadTask.getCompletedSize(),downloadTask.getTotalSize()));
//            mOperationView.showToast("已暂停下载");
//        }
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {

    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        if (downloadTask != null) {
            mOperationView.refreshProgressbarProgress(0);
            mOperationView.refreshTextProgressbarTextStatus("安装中");
        }
    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {
        Log.d("fu", "onError(downloadTask " + downloadTask.getCompletedSize() + ", errorCode)" + errorCode);
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
                mOperationView.showToast("未知错误");
                break;
        }
    }
}
