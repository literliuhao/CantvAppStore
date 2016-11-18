package com.can.appstore.download;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.can.appstore.R;
import com.can.appstore.eventbus.dispatcher.DownloadDispatcher;

import java.util.List;
import java.util.ListIterator;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;

/**
 * Created by laiforg on 2016/10/31.
 */

public class DownloadPresenterImpl implements DownloadContract.DownloadPresenter {

    private static final String TAG = "DownloadPresenterImpl";
    public static final String TAG_DOWNLOAD_UPDATA__STATUS = "download_update_status";
    private DownloadContract.DownloadView mView;

    private DownloadManager mDownLoadManager;

    private List<DownloadTask> mTasks;

    private AppInstallListener mAppInstallListener;


    public DownloadPresenterImpl(DownloadContract.DownloadView view) {
        mView = view;
        mAppInstallListener=new AppInstallListenerImpl();
        mDownLoadManager = DownloadManager.getInstance(mView.getContext());
        mDownLoadManager.setAppInstallListener(mAppInstallListener);
        mView.setPresenter(this);
    }

    @Override
    public void loadData() {
        mTasks = loadDownloadTask();
        if (mTasks != null && mTasks.size() > 0) {
            mView.hideNoDataView();
            mView.onDataLoaded(mTasks);
        } else {
            mView.showNoDataView();
        }
    }

    /**
     * 加载未安装成功的task，并删除成功的
     *
     * @return
     */
    private List<DownloadTask> loadDownloadTask() {
        List<DownloadTask> tasks = mDownLoadManager.loadAllTask();
        ListIterator<DownloadTask> iterator = tasks.listIterator();
        while (iterator.hasNext()) {
            DownloadTask task = iterator.next();
            if (AppInstallListener.APP_INSTALL_SUCESS == task.getDownloadStatus()) {
                mDownLoadManager.cancel(task);
                iterator.remove();
            }
        }
        return tasks;
    }

    @Override
    public void release() {
        mDownLoadManager.removeAppInstallListener(mAppInstallListener);
        mView = null;
    }

    @Override
    public void caculateStorage() {
        long freeSize = SystemUtil.getSDCardAvailableSpace();
        long totalSize = SystemUtil.getSDCardTotalSpace();
        int progress = (int) ((totalSize - freeSize) * 100 / totalSize);
        String freeStorage = mView.getContext().getResources().getString(R.string.uninsatll_manager_free_storage) +
                StringUtils.formatFileSize(freeSize, false);
        mView.showStorageView(progress, freeStorage);
    }

    @Override
    public void calculateRowNum(int focusPos) {
        String rowFmt = String.format("%d/%d行", focusPos + 1, mTasks.size());
        int pos = rowFmt.indexOf("/");
        SpannableString ss = new SpannableString(rowFmt);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#EAEAEA")), 0, pos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.refreshRowNumber(ss);
    }

    @Override
    public void deleteAllTasks() {
        if (mTasks == null || mTasks.size() == 0) {
            mView.showToast(R.string.download_no_task);
            return;
        }
        DownloadManager downloadManager = DownloadManager.getInstance(mView.getContext().getApplicationContext());
        downloadManager.cancelAll();
        mTasks.clear();
        mView.showNoDataView();
    }

    @Override
    public boolean pauseAllTasks() {
        if (mTasks == null || mTasks.size() == 0) {
            mView.showToast(R.string.download_no_task);
            return false;
        }
        DownloadManager downloadManager = DownloadManager.getInstance(mView.getContext().getApplicationContext());
        int pauseSize = 0;
        for (DownloadTask task : mTasks) {
            if (DownloadStatus.DOWNLOAD_STATUS_COMPLETED == task.getDownloadStatus()
                    || DownloadStatus.DOWNLOAD_STATUS_CANCEL == task.getDownloadStatus()
                    || DownloadStatus.DOWNLOAD_STATUS_ERROR == task.getDownloadStatus()
                    || AppInstallListener.APP_INSTALL_FAIL == task.getDownloadStatus()
                    || AppInstallListener.APP_INSTALL_SUCESS == task.getDownloadStatus()
                    || AppInstallListener.APP_INSTALLING == task.getDownloadStatus()) {
                continue;
            }
            pauseSize++;
            downloadManager.pause(task);
        }

        if (pauseSize > 0) {
            DownloadDispatcher.getInstance().postDownloadStatusEvent(TAG, TAG_DOWNLOAD_UPDATA__STATUS);
            return true;
        }
        return false;
    }

    @Override
    public boolean resumeAllTasks() {
        if (mTasks == null || mTasks.size() == 0) {
            mView.showToast(R.string.download_no_task);
            return false;
        }
        int pauseSize = 0;
        DownloadManager downloadManager = DownloadManager.getInstance(mView.getContext().getApplicationContext());
        for (DownloadTask task : mTasks) {
            if (DownloadStatus.DOWNLOAD_STATUS_COMPLETED == task.getDownloadStatus()
                    || DownloadStatus.DOWNLOAD_STATUS_CANCEL == task.getDownloadStatus()
                    || DownloadStatus.DOWNLOAD_STATUS_ERROR == task.getDownloadStatus()
                    || AppInstallListener.APP_INSTALL_FAIL == task.getDownloadStatus()
                    || AppInstallListener.APP_INSTALLING == task.getDownloadStatus()
                    || AppInstallListener.APP_INSTALL_SUCESS == task.getDownloadStatus()) {
                continue;
            }
            downloadManager.resume(task.getId());
            pauseSize++;
        }
        if (pauseSize > 0) {
            DownloadDispatcher.getInstance().postDownloadStatusEvent(TAG, TAG_DOWNLOAD_UPDATA__STATUS);
            return true;
        }
        return false;
    }

    private class AppInstallListenerImpl implements AppInstallListener {

        @Override
        public void onInstalling(DownloadTask downloadTask) {
            DownloadDispatcher.getInstance().postInstallStatusEvent(downloadTask.getId(),TAG,TAG_DOWNLOAD_UPDATA__STATUS );
        }

        @Override
        public void onInstallSucess(String id) {
            DownloadDispatcher.getInstance().postInstallStatusEvent(id,TAG,TAG_DOWNLOAD_UPDATA__STATUS );
        }

        @Override
        public void onInstallFail(String id) {
            DownloadDispatcher.getInstance().postInstallStatusEvent(id,TAG,TAG_DOWNLOAD_UPDATA__STATUS );
        }

        @Override
        public void onUninstallSucess(String id) {

        }

        @Override
        public void onUninstallFail(String id) {

        }
    }

}
