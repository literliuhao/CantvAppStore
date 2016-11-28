package com.can.appstore.download;


import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.tvlib.utils.LogUtil;

/**
 * Created by laiforg on 2016/11/14.
 */

public abstract class DownloadListener implements DownloadTaskListener {

    private static final String TAG = "DownloadListener";

    @Override
    public void onPrepare(DownloadTask downloadTask) {
        LogUtil.d(TAG, "onPrepare:" + downloadTask.toString());
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        LogUtil.d(TAG, "onStart:" + downloadTask.toString());
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        LogUtil.d(TAG, "onDownloading:" + downloadTask.toString());
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onPause(DownloadTask downloadTask) {
        LogUtil.d(TAG, "onPause:" + downloadTask.toString());
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {
        LogUtil.d(TAG, "onCancel:" + downloadTask.toString());
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        LogUtil.d(TAG, "onCompleted:" + downloadTask.toString());
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public abstract void onError(DownloadTask downloadTask, int errorCode);

    public abstract void onDownloadStatusUpdate(DownloadTask downloadTask);
}
