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
import cn.can.tvlib.utils.ApkUtils;
import cn.can.tvlib.utils.StringUtils;
import retrofit2.Response;

import static com.can.appstore.R.string.active_app_installing;

/**
 * Created by Atangs on 2016/11/2.
 *
 * 未对数据接口
 * 缺少应用安装，以及安装是否成功状态未设置
 */

public class ActivePresenter implements ActiveContract.TaskPresenter, DownloadTaskListener, AppInstallListener{
    private final static String TAG = "ActivePresenter";
//    public final static String URL = "http://172.16.11.65:8080/download/20161018/F2_Launcher_V536_20161018191036.apk";
    private DownloadTask mDownloadTask;
    private ActiveContract.OperationView mOperationView;
    private DownloadManager mDownloadManger;
    private Context mContext;
    private long mLastClickTime;
    private String mPkgName;
    private String mDownloadUrl;

    public ActivePresenter(ActiveContract.OperationView operationView, Context context) {
        this.mOperationView = operationView;
        this.mContext = context.getApplicationContext();

    }

    private void initDownloadTask(String downloadUrl) {
        mDownloadManger = DownloadManager.getInstance(mContext);
        DownloadTask downloadTask = mDownloadManger.getCurrentTaskById(MD5.MD5(downloadUrl));
        mDownloadTask = downloadTask;
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();
            switch (status){
                case DownloadStatus.DOWNLOAD_STATUS_INIT:
                case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                case AppInstallListener.APP_INSTALL_SUCESS:
                    mOperationView.refreshTextProgressbarTextStatus(mContext.getString(R.string.active_click_participate));
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
                    mOperationView.refreshTextProgressbarTextStatus(mContext.getString(R.string.active_app_download_waiting));
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                    mOperationView.refreshProgressbarProgress(0);
                    mOperationView.refreshTextProgressbarTextStatus("重试");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                case AppInstallListener.APP_INSTALLING:
                    mOperationView.refreshTextProgressbarTextStatus(mContext.getString(active_app_installing));
                    break;
                case AppInstallListener.APP_INSTALL_FAIL:
                    mOperationView.refreshTextProgressbarTextStatus("重新安装");
                    break;
                default:
                    long completedSize = downloadTask.getCompletedSize();
                    long totalSize = downloadTask.getTotalSize();
                    mOperationView.refreshProgressbarProgress(calculatePercent(completedSize, totalSize));
                    break;
            }
            mDownloadManger.addDownloadListener(downloadTask, ActivePresenter.this);
            mDownloadManger.setAppInstallListener(ActivePresenter.this);
        } else {
            mOperationView.refreshTextProgressbarTextStatus(mContext.getString(R.string.active_click_participate));
        }
    }

    //---------------------------- ActiveContract.TaskPresenter ----------------------------------
    @Override
    public void requestActiveData(String activeId){
        final CanCall<Result<Activity>> mActiveData = HttpManager.getApiService().getActivityInfo(activeId);
        mActiveData.enqueue(new CanCallback<Result<Activity>>() {
            @Override
            public void onResponse(CanCall<Result<Activity>> call, Response<Result<Activity>> response) throws Exception {
                Result<Activity> info = response.body();
                if (info.getData() == null){
//                    mOperationView.showNetworkRetryView(true,false);
                    return;
                }
                Activity active = info.getData();
                boolean isWebView = StringUtils.isEmpty(active.getUrl());
                mOperationView.showNetworkRetryView(false,isWebView);
                if (isWebView) {
                    mPkgName = active.getRecommend().getPackageName();
                    mDownloadUrl = active.getRecommend().getUrl();
                    mOperationView.setNativeLayout(active.getBackground());
                    initDownloadTask(mDownloadUrl);
                } else {
                    mOperationView.loadwebview(active.getUrl());
                }
            }

            @Override
            public void onFailure(CanCall<Result<Activity>> call, CanErrorWrapper errorWrapper) {
                mOperationView.showNetworkRetryView(true,false);
            }
        });
    }

    @Override
    public void clickBtnDownload() {
        String downloadUrl = mDownloadUrl;
        if(TextUtils.isEmpty(downloadUrl)){
            mOperationView.showToast("下载地址异常");
        }
        //需做按钮连续点击限制
        if (isFastContinueClickView()) {
            return;
        }

        DownloadTask downloadTask = mDownloadManger.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();
            if(status == AppInstallListener.APP_INSTALL_SUCESS || ApkUtils.isAvailable(mContext,mPkgName)){
                mOperationView.showToast("安装成功");
                startApk();
                return;
            }
            if(status == AppInstallListener.APP_INSTALL_FAIL){
                mOperationView.showToast("安装失败");
                return;
            }
            if(status == AppInstallListener.APP_INSTALLING){
                mOperationView.showToast("正在安装,请稍后");
                return;
            }

            if (status == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING || status == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                mOperationView.showToast("已暂停下载");
                mDownloadManger.pause(downloadTask);
                return;
            }
            if (status == DownloadStatus.DOWNLOAD_STATUS_PAUSE || status == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                mDownloadManger.resume(downloadTask.getId());
                mOperationView.showToast("继续下载");
            }
        } else {
            downloadTask = new DownloadTask();
            String md5 = MD5.MD5(downloadUrl);
            downloadTask.setFileName(md5);
            downloadTask.setId(md5);
            downloadTask.setSaveDirPath(mContext.getExternalCacheDir()!=null?mContext.getExternalCacheDir().getPath()
                    +"/":"");
            downloadTask.setUrl(downloadUrl);
            mDownloadManger.addDownloadTask(downloadTask, ActivePresenter.this);
            mDownloadManger.setAppInstallListener(ActivePresenter.this);
        }
        mDownloadTask = downloadTask;
    }

    @Override
    public void removeAllListener() {
        if(mDownloadManger != null){
            mDownloadManger.removeAppInstallListener(ActivePresenter.this);
            if(mDownloadTask!=null){
                mDownloadManger.removeDownloadListener(mDownloadTask,ActivePresenter.this);
                mDownloadTask = null;
            }
            mDownloadManger=null;
        }
    }

    // -------------------------------- DownloadTaskListener Event -----------
    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.d(TAG, "onPrepare: " + downloadTask.getCompletedSize());
        if(downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PREPARE){
            mOperationView.refreshTextProgressbarTextStatus(mContext.getResources().getString(R.string
                    .active_app_download_waiting));
        }
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.d(TAG, "onStart: " + downloadTask.getCompletedSize());
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_START) {
            mOperationView.refreshTextProgressbarTextStatus("");
            float downloadProgress = calculatePercent(downloadTask.getCompletedSize(), downloadTask.getTotalSize());
            if(downloadProgress < 2){
               mOperationView.refreshProgressbarProgress(2);
           }
        }
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        Log.d(TAG, "onDownloading: " + downloadTask.getCompletedSize());
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

    //<!-----------------AppInstallListener--------------------->
    @Override
    public void onInstalling(DownloadTask downloadTask) {
        Log.d(TAG, "onInstalling(downloadTask " + downloadTask.getCompletedSize() +")");
        mOperationView.refreshProgressbarProgress(0);
        mOperationView.refreshTextProgressbarTextStatus(mContext.getString(active_app_installing));
    }

    @Override
    public void onInstallSucess(String id) {
        Log.d(TAG, "onInstallSucess(id " + id +")");
        mOperationView.refreshTextProgressbarTextStatus(mContext.getString(R.string.active_click_participate));
        mOperationView.showToast("安装成功");
    }

    @Override
    public void onInstallFail(String id) {
        Log.d(TAG, "onInstallFail(id " + id +")");
        mOperationView.refreshTextProgressbarTextStatus("重新安装");
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

    private float calculatePercent(long completedSize, long totalSize) {
        return totalSize == 0 ? 0 : (float) (completedSize * 100 / totalSize);
    }

    private void startApk(){
        String packageName = mPkgName;
        PackageManager pm = mContext.getPackageManager();
        Intent intent=pm.getLaunchIntentForPackage(packageName);
        if(intent == null){
            mOperationView.showToast("未找到该应用");
            return;
        }
        mContext.startActivity(intent);
    }
}
