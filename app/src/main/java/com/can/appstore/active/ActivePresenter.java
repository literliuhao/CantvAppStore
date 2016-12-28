package com.can.appstore.active;


import android.content.Context;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.entity.Activity;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Result;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.dataeye.sdk.api.app.channel.DCResource;
import com.dataeye.sdk.api.app.channel.DCResourceLocation;
import com.dataeye.sdk.api.app.channel.DCResourcePair;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.utils.ApkUtils;
import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PromptUtils;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;
import retrofit2.Response;

/**
 * Created by Fuwen on 2016/11/2.
 */
public class ActivePresenter implements ActiveContract.TaskPresenter, DownloadTaskListener, AppInstallListener {
    private final static String TAG = "ActivePresenter";
    private DownloadTask mDownloadTask;
    private ActiveContract.OperationView mOperationView;
    private DownloadManager mDownloadManger;
    private Context mContext;
    private long mLastClickTime;
    private String mDownloadUrl;
    private CanCall<Result<Activity>> mActiveData;
    private AppInfo mAppInfo;
    private String mActiveDetail;

    public ActivePresenter(ActiveContract.OperationView operationView, Context context) {
        this.mOperationView = operationView;
        this.mContext = context.getApplicationContext();
        mActiveDetail = mContext.getString(R.string.active_detail);
    }

    private void initDownloadTask(String downloadUrl) {
        mDownloadManger = DownloadManager.getInstance(mContext.getApplicationContext());
        DownloadTask downloadTask = mDownloadManger.getCurrentTaskById(MD5.MD5(downloadUrl));
        mDownloadTask = downloadTask;
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();
            switch (status) {
                case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    mOperationView.refreshTextProgressbarTextStatus(R.string.active_null_str);
                    mOperationView.refreshProgressbarProgress(downloadTask.getPercent());
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
                    mOperationView.refreshTextProgressbarTextStatus(R.string.detail_app_download_wait);
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                case AppInstallListener.APP_INSTALLING:
                    mOperationView.refreshTextProgressbarTextStatus(R.string.detail_app_installing);
                    break;
                default:
                    mOperationView.refreshTextProgressbarTextStatus(R.string.active_click_participate);
                    break;
            }
            mDownloadManger.addDownloadListener(downloadTask, ActivePresenter.this);
            mDownloadManger.setAppInstallListener(ActivePresenter.this);
        } else {
            mOperationView.refreshTextProgressbarTextStatus(R.string.active_click_participate);
        }
    }

    //---------------------------- ActiveContract.TaskPresenter ----------------------------------
    @Override
    public void requestActiveData(String activeId) {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            mOperationView.loadDataFail(R.string.no_network);
        }
        mOperationView.showLoadingDialog();
        mActiveData = HttpManager.getApiService().getActivityInfo(activeId);
        mActiveData.enqueue(new CanCallback<Result<Activity>>() {
            @Override
            public void onResponse(CanCall<Result<Activity>> call, Response<Result<Activity>> response) throws Exception {
                Result<Activity> info = response.body();
                if (info.getData() == null) {
                    mOperationView.loadDataFail(R.string.load_data_faild);
                    return;
                }
                Activity active = info.getData();
                if (StringUtils.isEmpty(active.getUrl())) {
                    mOperationView.showBackground(active.getBackground());
                    mAppInfo = active.getRecommend();
                    if(mAppInfo != null){
                        mDownloadUrl = mAppInfo.getUrl();
                        mOperationView.showProgreessbar();
                        initDownloadTask(mDownloadUrl);
                    }
                    //统计活动详情页按钮曝光量
                    DCResourcePair pair = DCResourcePair.newBuilder().setResourceLocationId(mActiveDetail).setResourceId(mAppInfo.getName()).build();
                    DCResourceLocation.onShow(pair);
                    mOperationView.hideLoadingDialog();
                } else {
                    mOperationView.loadwebview(active.getUrl());
                }
            }

            @Override
            public void onFailure(CanCall<Result<Activity>> call, CanErrorWrapper errorWrapper) {
                mOperationView.loadDataFail(R.string.load_data_faild);
            }
        });
    }

    @Override
    public void clickBtnDownload() {
        String downloadUrl = mDownloadUrl;
        //需做按钮连续点击限制
        if (isFastContinueClickView()) {
            return;
        }
        String pkgName = mAppInfo.getPackageName();
        if (ApkUtils.isAvailable(mContext, pkgName)) {
            try {
                PackageUtil.openApp(mContext, pkgName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        DownloadTask downloadTask = mDownloadManger.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();
            if (status == AppInstallListener.APP_INSTALL_FAIL) {
                mDownloadManger.install(downloadTask);
                return;
            }
            if (status == AppInstallListener.APP_INSTALLING) {
                mOperationView.showActiveToast(R.string.installing);
                return;
            }

            if (status == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                if (!NetworkUtils.isNetworkConnected(mContext)) {
                    mOperationView.showActiveToast(R.string.no_network);
                } else {
                    downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
                    mDownloadManger.addDownloadTask(downloadTask, ActivePresenter.this);
                }
                return;
            }

            if (status == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING || status == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                mOperationView.showActiveToast(R.string.download_pause);
                mDownloadManger.pause(downloadTask);
                return;
            }
            if (status == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                mDownloadManger.resume(downloadTask.getId());
                mOperationView.showActiveToast(R.string.download_continue);
            }
        } else {
            if (!NetworkUtils.isNetworkConnected(mContext)) {
                mOperationView.showActiveToast(R.string.no_network);
                return;
            }
            downloadTask = new DownloadTask();
            String md5 = MD5.MD5(downloadUrl);
            downloadTask.setFileName(mAppInfo.getName());
            downloadTask.setId(md5);
            downloadTask.setUrl(downloadUrl);
            if(mAppInfo.getSize()> SystemUtil.getInternalAvailableSpace(mContext)-100*1024*1024){
                PromptUtils.toastShort(mContext,mContext.getString(R.string.error_msg));
                return;
            }
            mDownloadManger.addDownloadTask(downloadTask, ActivePresenter.this);
            mDownloadManger.setAppInstallListener(ActivePresenter.this);

            //统计活动详情页下载量
            DCResource.onDownloadFromResourceLocation(mAppInfo.getName(), mActiveDetail);
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
                mOperationView.refreshTextProgressbarTextStatus(R.string.detail_app_download_wait);
            }
        }
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.d(TAG, "onStart: " + downloadTask.getCompletedSize());
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_START) {
            if (downloadTask.getId().equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
                mOperationView.refreshTextProgressbarTextStatus(R.string.active_null_str);
            }

        }
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
                    mOperationView.showActiveToast(R.string.downlaod_error);
                    break;
                case DOWNLOAD_ERROR_IO_ERROR:
                    mOperationView.showActiveToast(R.string.downlaod_error);
                    break;
                case DOWNLOAD_ERROR_NETWORK_ERROR:
                    mOperationView.showActiveToast(R.string.network_connection_error);
                    break;
                case DOWNLOAD_ERROR_UNKONW_ERROR:
                    mOperationView.showActiveToast(R.string.unkonw_error);
                    break;
            }
            if (errorCode != DOWNLOAD_ERROR_NETWORK_ERROR) {
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
            mOperationView.refreshTextProgressbarTextStatus(R.string.detail_app_installing);
        }
    }

    @Override
    public void onInstallSucess(String id) {
        Log.d(TAG, "onInstallSucess(id " + id + ")");
        if (id.equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            mOperationView.refreshTextProgressbarTextStatus(R.string.active_click_participate);
            mOperationView.showActiveToast(R.string.install_success);
        }
    }

    @Override
    public void onInstallFail(String id) {
        Log.d(TAG, "onInstallFail(id " + id + ")");
        if (id.equalsIgnoreCase(MD5.MD5(mDownloadUrl))) {
            mOperationView.refreshTextProgressbarTextStatus(R.string.downlaod_restart);
            mOperationView.showActiveToast(R.string.install_fail);
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
        if (mDownloadManger != null) {
            mDownloadManger.removeAppInstallListener(ActivePresenter.this);
            if (mDownloadTask != null) {
                mDownloadManger.removeDownloadListener(mDownloadTask, ActivePresenter.this);
                mDownloadTask = null;
            }
            mDownloadManger = null;
        }
    }
}
