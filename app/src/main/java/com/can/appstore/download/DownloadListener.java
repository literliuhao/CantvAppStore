package com.can.appstore.download;


import cn.can.downloadlib.BuildConfig;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.tvlib.common.log.LogUtil;

/**
 * Created by laiforg on 2016/11/14.
 */

public abstract class DownloadListener implements DownloadTaskListener {

    private static final String TAG = "DownloadListener";

    @Override
    public void onPrepare(DownloadTask downloadTask) {
        if(BuildConfig.DEBUG){
            LogUtil.d(TAG, "onPrepare:" + downloadTask.toString());
        }
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        if(BuildConfig.DEBUG){
            LogUtil.d(TAG, "onPrepare:" + downloadTask.toString());
        }
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        if(BuildConfig.DEBUG){
            LogUtil.d(TAG, "onPrepare:" + downloadTask.toString());
        }
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onPause(DownloadTask downloadTask) {
        if(BuildConfig.DEBUG){
            LogUtil.d(TAG, "onPrepare:" + downloadTask.toString());
        }
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {
        if(BuildConfig.DEBUG){
            LogUtil.d(TAG, "onPrepare:" + downloadTask.toString());
        }
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        if(BuildConfig.DEBUG){
            LogUtil.d(TAG, "onPrepare:" + downloadTask.toString());
        }
        onDownloadStatusUpdate(downloadTask);
    }

    @Override
    public abstract void onError(DownloadTask downloadTask, int errorCode);

    public abstract void onDownloadStatusUpdate(DownloadTask downloadTask);
}
