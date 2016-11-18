package com.can.appstore.update;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.can.appstore.MyApp;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.utils.UpdateUtils;

import java.util.ArrayList;
import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;

/**
 * Created by shenpx on 2016/11/10 0010.
 */

public class UpdateProPresenter implements UpdateContract.Presenter, DownloadTaskListener {

    private static final String TAG = "updatePresenter";
    private UpdateContract.View mView;
    private UpdateContract.UpdateView mUpdateView;
    private DownloadManager mDownloadManager;
    private Context mContext;
    private String url;
    private int mSdTotalSize;
    private int mSdSurplusSize;
    private String mSdAvaliableSize;

    public UpdateProPresenter(UpdateContract.UpdateView mView, Context mContext) {
        this.mUpdateView = mView;
        this.mContext = mContext;
    }

    @Override
    public void getInstallPkgList(boolean isAutoUpdate) {}

    @Override
    public void getSDInfo() {}

    @Override
    public void refreshInstallPkgList() {

    }

    @Override
    public void getListSize() {}

    @Override
    public void clearList() {}

    @Override
    public void release() {}

    /**
     * 行数提示
     *
     * @param position
     */
    public void setNum(int position) {}

    /**
     * 初始化更新状态
     */
    public void initUpdateStatus(int position) {
        String downloadUrl = UpdateAppList.list.get(position).getDownloadUrl();
        mDownloadManager = DownloadManager.getInstance(MyApp.mContext);
        DownloadTask curDownloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (curDownloadTask != null) {
            int downloadStatus = curDownloadTask.getDownloadStatus();
            //获取更新状态
            if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE || downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                mUpdateView.refreshUpdateButton("等待中", true);
                mUpdateView.refreshUpdateProgress((int) curDownloadTask.getPercent(), false);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                mUpdateView.refreshUpdateButton("下载中", true);
                mUpdateView.refreshUpdateProgress((int) curDownloadTask.getPercent(), true);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                mUpdateView.refreshUpdateButton("安装中", true);
                mUpdateView.refreshUpdateProgress((int) curDownloadTask.getTotalSize(), false);
            }
            mDownloadManager.addDownloadListener(curDownloadTask, UpdateProPresenter.this);
        } else {
            mUpdateView.refreshUpdateButton("", false);
            mUpdateView.refreshUpdateProgress(0, false);
        }
    }

    /**
     * 添加更新任务
     */
    public void addUpdateTask(int position) {
        String downloadUrl = UpdateAppList.list.get(position).getDownloadUrl();
        mDownloadManager = DownloadManager.getInstance(mContext);
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();

            if (status == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING
                    || status == DownloadStatus.DOWNLOAD_STATUS_PREPARE
                    || status == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                //mDownloadManager.addDownloadListener(downloadTask, UpdatePresenter.this);
                return;
            } else if (status == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                mDownloadManager.resume(downloadTask.getId());
            }
        } else {
            downloadTask = new DownloadTask();
            String md5 = MD5.MD5(downloadUrl);
            downloadTask.setFileName(md5);
            downloadTask.setId(md5);
            downloadTask.setSaveDirPath(mContext.getExternalCacheDir().getPath() + "/");
            downloadTask.setUrl(downloadUrl);
            mDownloadManager.addDownloadTask(downloadTask, UpdateProPresenter.this);
        }
    }


    /**
     * 更新进度监听
     *
     * @param downloadTask
     */
    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.i(TAG, "onPrepare");
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
            mUpdateView.refreshUpdateButton("等待中", true);
            mUpdateView.refreshUpdateProgress(0, false);
        }
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.i(TAG, "onStart");
        if (downloadTask.getDownloadStatus() == 0) {
            mUpdateView.refreshUpdateButton("等待中", true);
            mUpdateView.refreshUpdateProgress(0, false);
        }
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        Log.i(TAG, "onDownloading");
        mUpdateView.refreshUpdateButton("下载中", true);
        mUpdateView.refreshUpdateProgress((int) downloadTask.getPercent(), true);
    }

    @Override
    public void onPause(DownloadTask downloadTask) {
        Log.i(TAG, "onPause");
        mUpdateView.refreshUpdateButton("等待中", true);
        mUpdateView.refreshUpdateProgress((int) downloadTask.getPercent(), false);
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {
        Log.i(TAG, "onCancel");
//        mUpdateView.refreshUpdateButton("等待中", true);
//        mUpdateView.refreshUpdateProgress((int) downloadTask.getPercent(), false);
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        Log.i(TAG, "onCompleted");
        if (downloadTask != null) {
            mUpdateView.refreshUpdateButton("安装", true);
            mUpdateView.refreshUpdateProgress((int) downloadTask.getTotalSize(), false);
            mUpdateView.showCompleted("下载完成");
        }
    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {
        Log.i(TAG, "onError");
        switch (errorCode) {
            case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                mUpdateView.showCompleted("未找到下载文件");
                break;
            case DOWNLOAD_ERROR_IO_ERROR:
                mUpdateView.showCompleted("IO异常");
                break;
            case DOWNLOAD_ERROR_NETWORK_ERROR:
                mUpdateView.showCompleted("网络异常，请重试！");
                break;
            /*case DOWNLOAD_ERROR_UNKONW_ERROR:
                mUpdateView.showCompleted("未知错误");
                break;*/
            default:
                break;
        }
    }
}
