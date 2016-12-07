package com.can.appstore.upgrade.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.upgrade.InstallApkListener;
import com.can.appstore.upgrade.UpgradeUtil;
import com.can.appstore.upgrade.view.UpgradeFailDialog;
import com.can.appstore.upgrade.view.UpgradeInFoDialog;
import com.can.appstore.upgrade.view.UpgradeProgressBarDialog;
import com.google.gson.Gson;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;

/**
 * Created by syl on 2016/11/2.
 * 应用商城自更新服务
 */

public class UpgradeService extends IntentService {
    public static final String TAG = "UpgradeService";
    //message.what
    public static final int SHOW_UPGRADE_INFO_DIALOG = 1;
    public static final int SHOW_UPGRADE_FAIL_DIALOG = 2;
    public static final int SHOW_PROGRESS_DIALOG = 3;
    public static final int INIT_SERVICE = 4;
    //常量
    public static final int INSTALL_DELAY = 3000;
    public static final String UPGRADE_INFO = "upgrade";
    public static final String NO_UPGRADE_INFO = "no_upgrade";
    //全局变量
    private int mLocalVersion;
    private String mUpdatePath;
    private String mFileName;
    private UpgradeInfo mUpgradeInfo;
    //dialog
    private UpgradeInFoDialog mUpgradeInFoDialog;
    private UpgradeProgressBarDialog mProgressDialog;
    private UpgradeFailDialog mFailDialog;

    private BroadcastReceiver mHomeKeyReceiver;
    private DownloadManager mManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPGRADE_INFO_DIALOG:
                    UpgradeInFoDialog.OnUpgradeClickListener listener = new UpgradeInFoDialog
                            .OnUpgradeClickListener() {
                        @Override
                        public void onClick() {
                            clickInstall();
                        }
                    };

                    mUpgradeInFoDialog = new UpgradeInFoDialog(UpgradeService.this, getResources().getString(R
                        .string.app_upgrade), mUpgradeInfo
                        .versionName, mUpgradeInfo.newFeature, getResources().getString(R.string.install),
                        listener);
                    mUpgradeInFoDialog.show();

                    break;
                case SHOW_UPGRADE_FAIL_DIALOG:
                    String content = (String) msg.obj;
                    mFailDialog = new UpgradeFailDialog(UpgradeService.this, content);
                    mFailDialog.show();
                    break;

                case SHOW_PROGRESS_DIALOG:
                    mProgressDialog = new UpgradeProgressBarDialog(UpgradeService.this);
                    mProgressDialog.show();
                    break;
                case INIT_SERVICE:
                    install();
                    break;
            }

        }
    };

    public UpgradeService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: 启动更新服务");
        getInfo();
    }

    private void getInfo() {
        mUpgradeInfo = Beta.getUpgradeInfo();
        if (mUpgradeInfo == null) {
            return;
        }
        checkUpgradeInfo();
    }

    /**
     * 检测更新信息并作出逻辑判断
     */
    private void checkUpgradeInfo() {

        mManager = DownloadManager.getInstance(this);
        mManager.getDownloadPath();

        mUpdatePath = mManager.getDownloadPath() + "/upgrade";
        if (!UpgradeUtil.isFileExist(mUpdatePath)) {
            UpgradeUtil.creatDir(mUpdatePath);
        }
        mFileName = mUpdatePath + "/" + mUpgradeInfo.versionCode + ".apk";
        //获取本地的版本号
        try {
            mLocalVersion = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext()
                    .getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "LocalVersionCode=" + mLocalVersion + ",UpGradeVersionCode=" + mUpgradeInfo.versionCode);

        if (mUpgradeInfo.versionCode <= mLocalVersion) {
            return;
        }

        //注册home键广播
        if (mHomeKeyReceiver == null) {
            mHomeKeyReceiver = new HomeKeyReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyReceiver, intentFilter);

        //储存版本信息到sp中
        SharedPreferences sp = getSharedPreferences(UPGRADE_INFO, Activity.MODE_PRIVATE);
        String info = new Gson().toJson(mUpgradeInfo);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(UPGRADE_INFO, info);
        editor.commit();


        if (checkIsExistApk()) {
            Message msg = Message.obtain();
            msg.what = SHOW_UPGRADE_INFO_DIALOG;
            mHandler.sendMessage(msg);
        } else {
            //先清空本地存放Apk的空间
            UpgradeUtil.delAllDateFile(mUpdatePath);
            downLoadApk(mUpgradeInfo.apkUrl);
        }
    }

    /**
     * 检测是否存在已经下载完成但没有安装的最新版本
     */
    private boolean checkIsExistApk() {
        if (UpgradeUtil.isFileExist(mFileName)) {
            String localMD5 = UpgradeUtil.getFileMD5(mFileName);
            if (mUpgradeInfo.apkMd5.equalsIgnoreCase(localMD5)) {
                Log.d(TAG, "checkIsExistApk: true");
                return true;
            }
        }
        Log.d(TAG, "checkIsExistApk: false");
        return false;
    }

    /**
     * 下载自升级的APK
     *
     * @param url 下载地址
     */
    private void downLoadApk(String url) {
        mManager = DownloadManager.getInstance(this);
        mManager.getDownloadPath();
        DownloadTask task = new DownloadTask(url);
        task.setFileName(mUpgradeInfo.versionCode + ".apk");
        task.setSaveDirPath(mUpdatePath);

        mManager.singleTask(task, new DownloadTaskListener() {
            @Override
            public void onPrepare(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onPrepare");
            }

            @Override
            public void onStart(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onStart");
            }

            @Override
            public void onDownloading(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onDownloading----" + downloadTask.getPercent());
            }

            @Override
            public void onPause(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onPause");
            }

            @Override
            public void onCancel(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onCancel");
            }

            @Override
            public void onCompleted(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onCompleted");
                onLoadingCompleted();
            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {
                Log.d(TAG, "DownloadManager=onError===" + errorCode);
            }
        }, mUpdatePath);
    }


    private void clickInstall() {
        //显示正在安装对话框
        mHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
        //安装应用
        mHandler.sendEmptyMessageDelayed(INIT_SERVICE, INSTALL_DELAY);
    }

    private void onLoadingCompleted() {
        //校验MD5
        String localMD5 = UpgradeUtil.getFileMD5(mFileName);
        if (!mUpgradeInfo.apkMd5.equalsIgnoreCase(localMD5)) {
            Log.d(TAG, "onLoadingCompleted: MD5error");
            UpgradeUtil.delAllDateFile(mUpdatePath);
        } else {
            Log.d(TAG, "onLoadingCompleted: MD5success");
            //显示安装apk对话框
            Message msg = Message.obtain();
            msg.what = SHOW_UPGRADE_INFO_DIALOG;
            mHandler.sendMessage(msg);
        }
    }


    private void install() {
        UpgradeUtil.installApk(this, mFileName, mUpgradeInfo.fileSize, new InstallApkListener() {
            @Override
            public void onInstallSuccess() {
                Log.d(TAG, "onInstallSuccess: ");
            }

            @Override
            public void onInstallFail(String reason) {
                Log.d(TAG, "onInstallFail: ");
                onInstallError(reason);
            }
        });
    }

    private void onInstallError(String reason) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_FAIL_DIALOG;
        msg.obj = reason;
        mHandler.sendMessage(msg);
        //安装失败，清空文件夹
        UpgradeUtil.delAllDateFile(mUpdatePath);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    class HomeKeyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                Log.d(TAG, "ACTION_CLOSE_SYSTEM_DIALOGS: ");
                if (mUpgradeInFoDialog != null && mUpgradeInFoDialog.isShowing()) {
                    mUpgradeInFoDialog.dismiss();
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (mFailDialog != null && mFailDialog.isShowing()) {
                    mFailDialog.dismiss();
                }
                if (mHomeKeyReceiver != null) {
                    Log.d(TAG, "unregisterReceiver: ");
                    unregisterReceiver(mHomeKeyReceiver);
                }

            }
        }
    }

}
