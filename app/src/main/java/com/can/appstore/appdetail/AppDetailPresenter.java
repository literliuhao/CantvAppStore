package com.can.appstore.appdetail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.appdetail.custom.CustomDialog;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Result;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.widgets.CanDialog;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

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
import cn.can.tvlib.utils.PackageUtils;
import cn.can.tvlib.utils.ToastUtils;
import retrofit2.Response;

/**
 * Created by JasonF on 2016/10/25.
 */
public class AppDetailPresenter implements AppDetailContract.Presenter, DownloadTaskListener, AppInstallListener {
    public final static String TAG = "AppDetailPresenter";
    public final static int DOWNLOAD_BUTTON_STATUS_PREPARE = 1;//下载
    public final static int DOWNLOAD_BUTTON_STATUS_WAIT = 2;//等待中
    public final static int DOWNLOAD_BUTTON_STATUS_DOWNLAODING = 3;//"点击暂停下载"
    public final static int DOWNLOAD_BUTTON_STATUS_PAUSE = 4;//点击继续下载
    public final static int DOWNLOAD_BUTTON_STATUS_INSTALLING = 5;//安装中
    public final static int DOWNLOAD_BUTTON_STATUS_RUN = 6;//运行
    public final static int DOWNLOAD_BUTTON_STATUS_RESTART = 7;//重试
    public final static int DOWNLOAD_BUTTON_STATUS_UPDATE = 8;//更新
    public final static float DOWNLOAD_INIT_PROGRESS = 0f;//初始时进度
    public final static float DOWNLOAD_FINISH_PROGRESS = 100f;//完成时进度
    public final static String ARGUMENT_APPID = "appID";
    public final static String ARGUMENT_TOPICID = "topicid";
    private final static String INSTALL_PATH = "";
    public int downlaodErrorCode = 0;//下载错误
    private Context mContext;
    private AppDetailContract.View mView;
    private DownloadManager mDownloadManager;
    private AppDetailPresenter.AppInstallReceiver mInstalledReceiver;
    public static String Url = "";
    private boolean isShowUpdateButton = false;
    public String mAppId = "";
    public String mTaskId = "";
    private String mTopicId = "";
    private CanCall<Result<AppInfo>> mAppDetailCall;
    private AppInfo mAppInfo;
    private String mPackageName = "";
    private CustomDialog mCustomDialog;
    private int mLimitInstallSace = 50 * 1024 * 1024;
    private String mInstallApkFileMD5 = "";
    private String mInstallApkPath = "";
    private boolean isInstalling = false;
    private CanDialog mCanDialog;

    public AppDetailPresenter(AppDetailContract.View view, Context context, Intent intent) {
        this.mView = view;
        this.mContext = context;
        getData(intent);
        initDownloadManager();
    }

    public void getData(Intent intent) {
        if (intent != null) {
            mAppId = intent.getStringExtra(ARGUMENT_APPID);
            mTopicId = intent.getStringExtra(ARGUMENT_TOPICID);
        }
    }

    @Override
    public void startLoad() {
        mView.showLoadingDialog();
        mAppDetailCall = HttpManager.getApiService().getAppInfo(mAppId, mTopicId);
        mAppDetailCall.enqueue(new CanCallback<Result<AppInfo>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfo>> call, Response<Result<AppInfo>> response) throws Exception {
                mView.hideLoadingDialog();
                Result<AppInfo> info = response.body();
                if (info == null) {
                    mView.loadDataFail();
                    return;
                }
                if (info.getData() == null) {
                    mView.loadDataFail();
                    return;
                }
                mAppInfo = info.getData();
                mPackageName = mAppInfo.getPackageName();
                Url = mAppInfo.getUrl();
                mTaskId = MD5.MD5(Url);
                initDownloadButtonStatus();
                if (mAppInfo.getVersionCode() > PackageUtils.getVersionCode(mContext, mPackageName) && ApkUtils.isAvailable(mContext, mPackageName)) {
                    isShowUpdateButton = true;
                    initUpdateButtonStatus();
                    mView.refreshUpdateButton(true);
                } else {
                    mView.refreshUpdateButton(false);
                }
                mView.loadAppInfoOnSuccess(mAppInfo);
            }

            @Override
            public void onFailure(CanCall<Result<AppInfo>> call, CanErrorWrapper errorWrapper) {
                Log.d(TAG, "onFailure: " + errorWrapper.getReason());
                ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.load_data_faild));
                mView.hideLoadingDialog();
                mView.loadDataFail();
            }
        });
    }

    private void initDownloadButtonStatus() {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(Url));
        if (ApkUtils.isAvailable(mContext, mPackageName)) {
            mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_RUN, DOWNLOAD_INIT_PROGRESS);
            return;
        }
        mInstallApkPath = INSTALL_PATH + mTaskId;
        if (new File(mInstallApkPath).exists()) {
            mInstallApkFileMD5 = MD5Util.getFileMD5(mInstallApkPath);
        }
        if (downloadTask != null) {
            int downloadStatus = downloadTask.getDownloadStatus();
            long completedSize = downloadTask.getCompletedSize();
            long totalSize = downloadTask.getTotalSize();
            float per = calculatorPercent(completedSize, totalSize);
            Log.d(TAG, "DownloadStatus : " + downloadStatus + "  completedSize : " + completedSize + "  totalSize" + totalSize + "   per : " + per +
                    "  mInstallApkFileMD5 : " + mInstallApkFileMD5 + "   mAppInfo : " + mAppInfo.getMd5());
            if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_INIT || downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, DOWNLOAD_INIT_PROGRESS);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_PAUSE, per);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_DOWNLAODING, per);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_CANCEL) {
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_PREPARE, DOWNLOAD_INIT_PROGRESS);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, per);
            } else if (downloadStatus == AppInstallListener.APP_INSTALLING) {
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
            } else if (downloadStatus == AppInstallListener.APP_INSTALL_FAIL) {  //安装失败  重试
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, DOWNLOAD_INIT_PROGRESS);
            }
            addDownlaodListener();
        } else {
            mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_PREPARE, DOWNLOAD_INIT_PROGRESS);
        }
    }

    private void initUpdateButtonStatus() {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(Url));
        mInstallApkPath = INSTALL_PATH + mTaskId;
        if (new File(mInstallApkPath).exists()) {
            mInstallApkFileMD5 = MD5Util.getFileMD5(mInstallApkPath);
        }
        if (downloadTask != null) {
            int downloadStatus = downloadTask.getDownloadStatus();
            long completedSize = downloadTask.getCompletedSize();
            long totalSize = downloadTask.getTotalSize();
            float per = calculatorPercent(completedSize, totalSize);
            Log.d(TAG, "UpdateStatus : " + downloadStatus + "  completedSize : " + completedSize + "  totalSize" + totalSize + "   per : " + per +
                    "  mInstallApkFileMD5 : " + mInstallApkFileMD5 + "   mAppInfo : " + mAppInfo.getMd5());
            if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_INIT || downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, DOWNLOAD_INIT_PROGRESS);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_PAUSE, per);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_DOWNLAODING, per);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_CANCEL) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_PREPARE, per);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, per);
            } else if (downloadStatus == AppInstallListener.APP_INSTALLING) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
            } else if (downloadStatus == AppInstallListener.APP_INSTALL_FAIL) {//安装失败  重试
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, DOWNLOAD_INIT_PROGRESS);
                addDownlaodListener();
            } else {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_UPDATE, DOWNLOAD_INIT_PROGRESS);
            }
        }
    }

    /**
     * 计算下载的百分比
     *
     * @param completedSize
     * @param totalSize
     * @return
     */

    public float calculatorPercent(long completedSize, long totalSize) {
        return totalSize == 0 ? 0 : (float) (completedSize * 100f / totalSize);
    }

    public void initDownloadManager() {
        mDownloadManager = DownloadManager.getInstance(mContext);
        INSTALL_PATH = mDownloadManager.getDownloadPath();
    }

    @Override
    public void clickStartDownload(boolean isClickUpdateButton) {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(mTaskId);
        int downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;
        long completedSize = 0;
        long totalSize = 0;
        float per = 0;
        if (downloadTask != null) {
            completedSize = downloadTask.getCompletedSize();
            totalSize = downloadTask.getTotalSize();
            downloadStatus = downloadTask.getDownloadStatus();
            per = calculatorPercent(completedSize, totalSize);
        }
        Log.d(TAG, "clickStartDownload completedSize: " + completedSize + "totalSize : " + totalSize + "  downloadStatus : " + downloadStatus + "  isInstalling : " + isInstalling
                + "clickStartDownload: mInstallApkFileMD5 : " + mInstallApkFileMD5 + "   mAppInfo : " + mAppInfo.getMd5());
        if (Utils.isFastDoubleClick()) {//防止连续点击
            return;
        } else if (ApkUtils.isAvailable(mContext, mPackageName) && !isClickUpdateButton) {//应用已经安装
            PackageUtil.openApp(mContext, mPackageName);
            return;
        } else if (isInstalling) { //任务不存在，但安装包还存在，任务正在安装
            return;
        } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED || downloadStatus == AppInstallListener.APP_INSTALLING) {//完成 , 并且正在安装时不能点击
            return;
        } else if (!NetworkUtils.isNetworkConnected(mContext)) { // 网络连接断开时不能点击
            ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.network_connection_disconnect));
            return;
        } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_ERROR) {   // 下载错误 , 设置取消,重新添加任务
            downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
            mDownloadManager.addDownloadTask(downloadTask, AppDetailPresenter.this);
            clickRefreshButtonStatus(isClickUpdateButton, DOWNLOAD_INIT_PROGRESS);
            return;
        } else if (downloadStatus == AppInstallListener.APP_INSTALL_FAIL) {   //安装失败,可能内存不足，安装包出现问题,删除安装包重新下载
            if (mInstallApkFileMD5.equals(mAppInfo.getMd5())) {  // MD5值相同  安装
                silentInstall(mAppInfo.getName());
                return;
            } else {
                ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.pares_install_apk_fail));
                //                mDownloadManager.removeTask(mTaskId);  // 应该需要从任务中移除
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;
            }
        }

        if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_INIT || downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
            DownloadTask Task = new DownloadTask();
            String fileName = mTaskId;
            Task.setFileName(fileName);
            Task.setId(fileName);
            Task.setUrl(Url);
            Task.setIcon(mAppInfo.getIcon());
            mDownloadManager.addDownloadTask(Task, AppDetailPresenter.this);
            clickRefreshButtonStatus(isClickUpdateButton, per);
        } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
            mDownloadManager.pause(downloadTask);
        } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
            clickRefreshButtonStatus(isClickUpdateButton, per);
            mDownloadManager.resume(mTaskId);
        }
    }

    public void clickRefreshButtonStatus(boolean isClickUpdateButton, float per) {
        if (isClickUpdateButton) {
            mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, per);
        } else {
            mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, per);
        }
    }

    @Override
    public void addBroadcastReceiverListener() {
        registerInstallReceiver();
    }

    /**
     * 注册应用安装卸载的广播
     */
    private void registerInstallReceiver() {
        if (mInstalledReceiver == null) {
            mInstalledReceiver = new AppInstallReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_VIEW);
            filter.addDataScheme("package");
            mContext.registerReceiver(mInstalledReceiver, filter);
        }
    }

    @Override
    public void addDownlaodListener() {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(mTaskId);
        if (downloadTask != null) {
            mDownloadManager.addDownloadListener(downloadTask, AppDetailPresenter.this);
            mDownloadManager.setAppInstallListener(AppDetailPresenter.this);
        }
    }

    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.d(TAG, "onPrepare CompletedSize: " + downloadTask.getCompletedSize() + " TotalSize" + downloadTask.getTotalSize());
        if (downloadTask.getCompletedSize() == 0) {
            mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, DOWNLOAD_INIT_PROGRESS);
            if (isShowUpdateButton) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, DOWNLOAD_INIT_PROGRESS);
            }
        }
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.d(TAG, "onStart CompletedSize: " + downloadTask.getCompletedSize() + " TotalSize" + downloadTask.getTotalSize());
        if (downloadTask.getCompletedSize() == 0) {
            mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, DOWNLOAD_INIT_PROGRESS);
            if (isShowUpdateButton) {
                mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, DOWNLOAD_INIT_PROGRESS);
            }
        }
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        Log.d(TAG, "onDownloading CompletedSize: " + downloadTask.getCompletedSize() + " TotalSize" + downloadTask.getTotalSize());
        float per = calculatorPercent(downloadTask.getCompletedSize(), downloadTask.getTotalSize());
        mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_DOWNLAODING, per);
        if (isShowUpdateButton) {
            mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_DOWNLAODING, per);
        }
    }

    @Override
    public void onPause(DownloadTask downloadTask) {
        Log.d(TAG, "onPause CompletedSize: " + downloadTask.getCompletedSize() + " TotalSize" + downloadTask.getTotalSize());
        float per = calculatorPercent(downloadTask.getCompletedSize(), downloadTask.getTotalSize());
        mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_PAUSE, per);
        if (isShowUpdateButton) {
            mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_PAUSE, per);
        }
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        String saveDirPath = downloadTask.getSaveDirPath() + downloadTask.getFileName();
        Log.d(TAG, "onCompleted CompletedSize: " + downloadTask.getCompletedSize() + " TotalSize" + downloadTask.getTotalSize() + " getSaveDirPath : " + saveDirPath);
        mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
        if (isShowUpdateButton) {
            mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
        }
    }

    @Override
    public void onInstalling(DownloadTask downloadTask) {
        Log.d(TAG, "onInstalling: " + downloadTask);
        mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
        if (isShowUpdateButton) {
            mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
        }
    }

    @Override
    public void onInstallSucess(String id) {
        Log.d(TAG, "onInstallSucess: " + id);
    }

    @Override
    public void onInstallFail(String id) {
        Log.d(TAG, "onInstallFail: " + id);
    }

    @Override
    public void onUninstallSucess(String id) {
    }

    @Override
    public void onUninstallFail(String id) {
    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {
        Log.d(TAG, "onError CompletedSize: " + downloadTask.getCompletedSize() + " errorCode:" + errorCode);
        downlaodErrorCode = errorCode;
        float per = calculatorPercent(downloadTask.getCompletedSize(), downloadTask.getTotalSize());
        if (errorCode == DownloadTaskListener.DOWNLOAD_ERROR_FILE_NOT_FOUND) {
            ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.downlaod_error));
            refreshButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, per);
        } else if (errorCode == DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR) {
            ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.downlaod_error));
            refreshButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, per);
        } else if (errorCode == DownloadTaskListener.DOWNLOAD_ERROR_NETWORK_ERROR) {
            ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.network_connection_error));
            refreshButtonStatus(DOWNLOAD_BUTTON_STATUS_PAUSE, per);
        } else if (errorCode == DownloadTaskListener.DOWNLOAD_ERROR_UNKONW_ERROR) {
            ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.unkonw_error));
            refreshButtonStatus(DOWNLOAD_BUTTON_STATUS_PREPARE, per);
        }
    }

    public void refreshButtonStatus(int statusCode, float per) {
        if (isShowUpdateButton) {
            mView.refreshUpdateButtonStatus(statusCode, per);
        } else {
            mView.refreshDownloadButtonStatus(statusCode, per);
        }
    }

    class AppInstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
                Log.d(TAG, "install packageName : " + packageName);
                isInstalling = false;
                if (packageName.equals(mPackageName)) {
                    mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_RUN, DOWNLOAD_INIT_PROGRESS);
                    if (isShowUpdateButton) {
                        mView.refreshUpdateButton(false);
                    }
                }
            }
        }
    }

    /**
     * 进入到图片放大页面
     *
     * @param currentIndex
     */
    public void enterImageScaleActivity(int currentIndex) {
        Intent intent = new Intent(mContext, ImageScaleActivity.class);
        intent.putExtra("imageUrl", (Serializable) mAppInfo.getThumbs());
        intent.putExtra("currentIndex", currentIndex);
        mContext.startActivity(intent);
    }

    /**
     * 获取当前apk的包名
     *
     * @return
     */
    public String getCurAppPackageName() {
        return mPackageName;
    }

    /**
     * 显示推荐对话框
     */
    public void showIntroduceDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        String updateLog = mAppInfo.getUpdateLog();
        if (!TextUtils.isEmpty(updateLog)) {
            builder.setUpdatelogText(updateLog);
        } else {
            builder.setUpdatelogText(mContext.getResources().getString(R.string.not_update_log));
        }
        builder.setAboutText(mAppInfo.getAbout());
        mCustomDialog = builder.create();
        mCustomDialog.show();
    }

    /**
     * 隐藏推荐对话框
     */
    public void dismissIntroduceDialog() {
        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
        }
    }

    /**
     * 取消注册监听
     */
    public void unRegiestr() {
        if (mInstalledReceiver != null) {
            mContext.unregisterReceiver(mInstalledReceiver);
            mInstalledReceiver = null;
        }
    }

    @Override
    public void release() {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(mTaskId);
        if (downloadTask != null) {
            mDownloadManager.removeDownloadListener(downloadTask, this);
            mDownloadManager.removeAppInstallListener(this);
        }
        if (mAppDetailCall != null) {
            mAppDetailCall.cancel();
        }
        dismissIntroduceDialog();
        dismissInsufficientStorageSpaceDialog();
        unRegiestr();
    }

    /**
     * 静默安装
     *
     * @param appName
     */
    public void silentInstall(String appName) {
        Log.d(TAG, "silentInstall ");
        if (SdcardUtils.getApkInstallDirSzie(mContext) <= (mLimitInstallSace + mAppInfo.getSize() * 1.5)) {  // 安装内存不足
            showInsufficientStorageSpaceDialog();
            return;
        }
        isInstalling = true;
        refreshButtonStatus(DOWNLOAD_BUTTON_STATUS_INSTALLING, DOWNLOAD_FINISH_PROGRESS);
        mDownloadManager.install(mDownloadManager.getCurrentTaskById(mTaskId));
    }

    /**
     * 显示内存不足安装失败的提示框
     */
    public void showInsufficientStorageSpaceDialog() {
        String title = mContext.getResources().getString(R.string.space_inequacy);
        String ok = mContext.getResources().getString(R.string.ok);
        String hint = mContext.getResources().getString(R.string.space_inequacy_hint);
        mCanDialog = new CanDialog(mContext);
        mCanDialog.setTitle(title).setTitleMessage(hint).setPositiveButton(ok).setOnCanBtnClickListener(new CanDialog.OnClickListener() {
            @Override
            public void onClickPositive() {
                dismissInsufficientStorageSpaceDialog();
            }

        });
        mCanDialog.show();
    }

    public void dismissInsufficientStorageSpaceDialog() {
        if (mCanDialog != null) {
            mCanDialog.dismiss();
        }
    }
}