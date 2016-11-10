package com.can.appstore.appdetail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Result;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.ui.widgets.LoadingDialog;
import cn.can.tvlib.utils.ApkUtils;
import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtils;
import cn.can.tvlib.utils.ToastUtils;
import retrofit2.Response;

/**
 * Created by JasonF on 2016/10/25.
 */
public class AppDetailPresenter implements AppDetailContract.Presenter, DownloadTaskListener {
    public final static String TAG = "AppDetailPresenter";
    public final static int DOWNLOAD_BUTTON_STATUS_PREPARE = 1;//下载
    public final static int DOWNLOAD_BUTTON_STATUS_WAIT = 2;//等待中
    public final static int DOWNLOAD_BUTTON_STATUS_DOWNLAODING = 3;//"点击暂停下载"
    public final static int DOWNLOAD_BUTTON_STATUS_PAUSE = 4;//点击继续下载
    public final static int DOWNLOAD_BUTTON_STATUS_RUN = 6;//运行
    public final static int DOWNLOAD_BUTTON_STATUS_INSTALLING = 5;//安装中
    public final static int DOWNLOAD_BUTTON_STATUS_RESTART = 6;//重试
    public final static float DOWNLOAD_INIT_PROGRESS = 0f;//初始时进度
    public final static float DOWNLOAD_FINISH_PROGRESS = 100f;//完成时进度
    public int downlaodErrorCode = 0;//下载错误
    private Context mContext;
    private AppDetailContract.View mView;
    private DownloadManager mDownloadManager;
    private BroadcastReceiver mHomeReceivcer;
    private AppDetailPresenter.AppInstallReceiver mInstalledReceiver;
    public static String Url = "";
    private boolean isShowUpdateButton = false;
    private LoadingDialog mLoadingDialog;
    private String mAppId = "1";
    private CanCall<Result<AppInfo>> mAppDetailCall;
    private AppInfo mAppInfo;
    private String mPackageName = "com.dangbeimarket";

    public AppDetailPresenter(AppDetailContract.View view, Context context, Intent intent) {
        this.mView = view;
        this.mContext = context;
        getData(intent);
        initDownloadManager();
    }

    public void getData(Intent intent) {
        if (intent != null) {
            mAppId = intent.getStringExtra("appID");
        }
    }

    private void initUpdateButtonStatus() {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(Url));
        if (downloadTask != null) {
            int downloadStatus = downloadTask.getDownloadStatus();
            long completedSize = downloadTask.getCompletedSize();
            long totalSize = downloadTask.getTotalSize();
            float per = calculatorPercent(completedSize, totalSize);
            Log.d(TAG, "downloadStatus : " + downloadStatus + "  completedSize : " + completedSize + "  totalSize" + totalSize + "   per : " + per);
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
                mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, per);
            }
            addDownlaodListener();
        } else {
            mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_PREPARE, DOWNLOAD_INIT_PROGRESS);
        }
    }

    private void initDownloadButtonStatus() {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(Url));
        if (ApkUtils.isAvailable(mContext, mPackageName)) {
            mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_RUN, DOWNLOAD_INIT_PROGRESS);
            return;
        }
        if (downloadTask != null) {
            int downloadStatus = downloadTask.getDownloadStatus();
            long completedSize = downloadTask.getCompletedSize();
            long totalSize = downloadTask.getTotalSize();
            float per = calculatorPercent(completedSize, totalSize);
            Log.d(TAG, "downloadStatus : " + downloadStatus + "  completedSize : " + completedSize + "  totalSize" + totalSize + "   per : " + per);
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
            }
            addDownlaodListener();
        } else {
            mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_PREPARE, DOWNLOAD_INIT_PROGRESS);
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
        float per = totalSize == 0 ? 0 : (float) (completedSize * 100f / totalSize);
        return per;
    }

    public void initDownloadManager() {
        mDownloadManager = DownloadManager.getInstance(mContext);
    }

    @Override
    public void startLoad() {
        mView.showLoading();
        if (mAppDetailCall == null) {
            mAppDetailCall = HttpManager.getApiService().getAppInfo(mAppId, "54");
        } else {
            mAppDetailCall.clone();
        }
        mAppDetailCall.enqueue(new CanCallback<Result<AppInfo>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfo>> call, Response<Result<AppInfo>> response) throws Exception {
                mView.hideLoading();
                Result<AppInfo> info = response.body();
                if (info == null) {
                    //请求失败处理
                    mView.loadDataFail();
                    return;
                }
                if (info.getStatus() != 0 || info.getData() == null) {
                    //没有数据处理
                    mView.loadDataFail();
                    return;
                }
                mAppInfo = info.getData();
                mPackageName = mAppInfo.getPackageName();
                Url = mAppInfo.getUrl();
                initDownloadButtonStatus();
                mView.loadAppInfoOnSuccess(mAppInfo);
                if (mAppInfo.getVersionCode() > PackageUtils.getVersionCode(mContext, mPackageName) && ApkUtils.isAvailable(mContext, mPackageName)) {
                    isShowUpdateButton = true;
                    mView.refreshUpdateButton(true);
                    initUpdateButtonStatus();
                }
            }

            @Override
            public void onFailure(CanCall<Result<AppInfo>> call, CanErrorWrapper errorWrapper) {
                mView.loadDataFail();
                mView.hideLoading();
            }
        });
    }

    @Override
    public void clickStartDownload(boolean isClickUpdateButton) {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(Url));
        int downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;
        long completedSize = 0;
        long totalSize = 0;
        float per = 0;
        if (downloadTask != null) {
            completedSize = downloadTask.getCompletedSize();
            totalSize = downloadTask.getTotalSize();
            downloadStatus = downloadTask.getDownloadStatus();
            per = calculatorPercent(completedSize, totalSize);
            Log.d(TAG, "clickStartDownload completedSize: " + completedSize + "totalSize : " + totalSize + "  downloadStatus : " + downloadStatus);
        }
        if (AppUtils.isFastDoubleClick()) {//防止连续点击
            return;
        } else if (ApkUtils.isAvailable(mContext, mPackageName) && !isClickUpdateButton) {//应用已经安装
            PackageUtil.openApp(mContext, mPackageName);
            return;
        } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED && !ApkUtils.isAvailable(mContext, mPackageName)) {//完成 , 并且正在安装时不能点击
            return;
        } else if (!NetworkUtils.isNetworkConnected(mContext)) { // 网络连接断开时不能点击
            showToast(mContext.getResources().getString(R.string.network_connection_disconnect));
            return;
        } else if (downloadStatus != DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING && !ApkUtils.isAvailable(mContext, mPackageName) && !isClickUpdateButton) {//内存不足不能点击下载
            //判断内存是否充足
            //            if (!TextUtils.isEmpty("应用内存大小")) {
            //                long remainDownloadSize = totalSize - completedSize;// apk剩余下载的长度
            //                String ToastMsg = String.format(mContext.getResources().getString(R.string.download_memory_lack), "应用名称");
            //                if (AppUtils.getApkInstallDirSzie(mContext) <= 100 * 1024 * 1024) {//安装内存不足
            //                    showToast(ToastMsg);
            //                    return;
            //                } else if (AppUtils.getSDAvaliableSize() <= 100 * 1024 * 1024) {//sd卡内存不足
            //                    showToast(ToastMsg);
            //                    return;
            //                }
            //            }
        }
        if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_INIT || downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
            DownloadTask Task = new DownloadTask();
            String fileName = MD5.MD5(Url);
            Task.setFileName(fileName);
            Task.setId(fileName);
            Task.setSaveDirPath(mContext.getExternalCacheDir().getPath() + "/");
            Task.setUrl(Url);
            mDownloadManager.addDownloadTask(Task, AppDetailPresenter.this);
            clickRefreshButtonStatus(isClickUpdateButton, per);
        } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
            mDownloadManager.pause(downloadTask);
        } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
            clickRefreshButtonStatus(isClickUpdateButton, per);
            mDownloadManager.resume(MD5.MD5(Url));
        } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
            if (downlaodErrorCode != DownloadTaskListener.DOWNLOAD_ERROR_NETWORK_ERROR) {//重试
                clickRefreshButtonStatus(isClickUpdateButton, per);
                mDownloadManager.resume(MD5.MD5(Url));
            }
        }
    }

    public void clickRefreshButtonStatus(boolean isClickUpdateButton, float per) {
        if (isClickUpdateButton) {
            mView.refreshUpdateButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, per);
        } else {
            mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_WAIT, per);
        }
    }

    public void showToast(String msg) {
        ToastUtils.showMessage(mContext, msg);
    }

    @Override
    public void addBroadcastReceiverListener() {
        registerInstallReceiver();
        registHomeBoradCast();
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
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(Url));
        if (downloadTask != null) {
            mDownloadManager.addDownloadListener(downloadTask, AppDetailPresenter.this);
        }
    }

    @Override
    public void release() {
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(Url));
        if (downloadTask != null) {
            mDownloadManager.removeDownloadListener(downloadTask, this);
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
    public void onError(DownloadTask downloadTask, int errorCode) {
        Log.d(TAG, "onError CompletedSize: " + downloadTask.getCompletedSize() + " errorCode:" + errorCode);
        downlaodErrorCode = errorCode;
        float per = calculatorPercent(downloadTask.getCompletedSize(), downloadTask.getTotalSize());
        if (errorCode == DownloadTaskListener.DOWNLOAD_ERROR_FILE_NOT_FOUND) {
            showToast(mContext.getResources().getString(R.string.downlaod_error));
            errorRefreshButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, per);
        } else if (errorCode == DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR) {
            showToast(mContext.getResources().getString(R.string.downlaod_error));
            errorRefreshButtonStatus(DOWNLOAD_BUTTON_STATUS_RESTART, per);
        } else if (errorCode == DownloadTaskListener.DOWNLOAD_ERROR_NETWORK_ERROR) {
            showToast(mContext.getResources().getString(R.string.network_connection_error));
            errorRefreshButtonStatus(DOWNLOAD_BUTTON_STATUS_PAUSE, per);
        } else if (errorCode == DownloadTaskListener.DOWNLOAD_ERROR_UNKONW_ERROR) {
            showToast(mContext.getResources().getString(R.string.unkonw_error));
            errorRefreshButtonStatus(DOWNLOAD_BUTTON_STATUS_PREPARE, per);
        }
    }

    public void errorRefreshButtonStatus(int statusCode, float per) {
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
                if (packageName.equals(mPackageName)) {
                    mView.refreshDownloadButtonStatus(DOWNLOAD_BUTTON_STATUS_RUN, DOWNLOAD_INIT_PROGRESS);
                    if (isShowUpdateButton) {
                        mView.refreshUpdateButton(false);
                    }
                }
            }
        }
    }

    public void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public void showLoading(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.dimen_80px));
            mLoadingDialog.setMessage(msg);
            mLoadingDialog.show();
        }
    }

    /**
     * 根据操作类型选择图片
     *
     * @param conType
     * @return
     */
    public int getOperationPic(String conType) {
        int type = Integer.parseInt(conType);
        int drawableID = 0;
        switch (type) {
            case 1:
                drawableID = R.drawable.hand_shank; // 手柄
                break;
            case 2:
                drawableID = R.drawable.remote_control;// 遥控器
                break;
            case 3:
                drawableID = R.drawable.phone;// 手机
                break;
            case 4:
                drawableID = R.drawable.microphone;// 麦克风
                break;
            default:
                break;
        }
        return drawableID;
    }

    public void enterImageScaleActivity(int currentIndex) {// TODO  进入到图放大页面
        Intent intent = new Intent(mContext, ImageScaleActivity.class);
        //        intent.putExtra("imageUrl", (Serializable) mAppInfo.getThumbs());
        intent.putExtra("currentIndex", currentIndex);
        mContext.startActivity(intent);
    }

    public String getCurAppPackageName() {
        return mPackageName;
    }

    public boolean getUpdateButtonStatus() {
        return isShowUpdateButton;
    }

    /**
     * 注册按主页键的广播
     */
    private void registHomeBoradCast() {
        mHomeReceivcer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    mView.onClickHomeKey();
                    return;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.registerReceiver(mHomeReceivcer, filter);
    }

    public void unRegiestr() {
        if (mInstalledReceiver != null) {
            mContext.unregisterReceiver(mInstalledReceiver);
            mInstalledReceiver = null;
        }
        if (mHomeReceivcer != null) {
            mContext.unregisterReceiver(mHomeReceivcer);
            mHomeReceivcer = null;
        }
    }
}