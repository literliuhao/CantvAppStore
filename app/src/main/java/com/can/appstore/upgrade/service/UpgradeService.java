package com.can.appstore.upgrade.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.can.appstore.R;
import com.can.appstore.upgrade.InstallApkListener;
import com.can.appstore.upgrade.UpgradeUtil;
import com.can.appstore.upgrade.view.UpgradeFailDialog;
import com.can.appstore.upgrade.view.UpgradeInFoDialog;
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
    public static final int SHOW_UPGRADE_INFO_DIALOG = 1;
    public static final int SHOW_UPGRADE_FAIL_DIALOG = 2;
    private int mLocalVersion;
    private String mUpdatePath;
    private String mFileName;
    private UpgradeInfo mUpgradeInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPGRADE_INFO_DIALOG:
                    UpgradeInFoDialog.OnUpgradeClickListener listener = new UpgradeInFoDialog.OnUpgradeClickListener() {
                        @Override
                        public void onClick() {
                            installApk();
                        }
                    };
                    UpgradeInFoDialog dialog = new UpgradeInFoDialog(UpgradeService.this, getResources().getString(R
                            .string.system_upgrade), mUpgradeInfo
                            .versionName, mUpgradeInfo.newFeature, getResources().getString(R.string.install),
                            listener);
                    dialog.show();
                    break;
                case SHOW_UPGRADE_FAIL_DIALOG:
                    String content = (String) msg.obj;
                    UpgradeFailDialog failDialog = new UpgradeFailDialog(UpgradeService.this, content);
                    failDialog.show();
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
        mUpdatePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/updateapk";
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
        if (checkIsExistApk()) {
            Message msg = Message.obtain();
            msg.what = SHOW_UPGRADE_INFO_DIALOG;
            mHandler.sendMessage(msg);
        } else {
            //先清空本地存放Apk的空间
            // TODO: 2016/11/16 要不要清空
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
        DownloadManager manager = DownloadManager.getInstance(this);
        DownloadTask task = new DownloadTask(url);
        task.setFileName(mUpgradeInfo.versionCode + ".apk");
        task.setSaveDirPath(mUpdatePath);

        /**
         * 调用监听，阻止下载完成自动静默安装，不可删除
         */
        task.setAppListener(new AppInstallListener() {
            @Override
            public void onInstalling(DownloadTask downloadTask) {
            }

            @Override
            public void onInstallSucess(String id) {
            }

            @Override
            public void onInstallFail(String id) {
            }

            @Override
            public void onUninstallSucess(String id) {

            }

            @Override
            public void onUninstallFail(String id) {

            }
        });

        manager.singleTask(task, new DownloadTaskListener() {
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
                onLoadingError();
            }

        });
    }


    private void installApk() {
        UpgradeUtil.install(this, mFileName, mUpgradeInfo.fileSize, new InstallApkListener() {
            @Override
            public void onInstallFail(String reason) {
                Log.d(TAG, "onInstallFail: ");
                onInstallError(reason);
            }
        });
    }

    private void onLoadingCompleted() {
        String localMD5 = UpgradeUtil.getFileMD5(mFileName);
        if (!mUpgradeInfo.apkMd5.equalsIgnoreCase(localMD5)) {
            UpgradeUtil.delAllDateFile(mUpdatePath);
        } else {
            //显示安装apk对话框
            Message msg = Message.obtain();
            msg.what = SHOW_UPGRADE_INFO_DIALOG;
            mHandler.sendMessage(msg);
        }
    }

    private void onLoadingError() {
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_FAIL_DIALOG;
        msg.obj = getResources().getString(R.string.load_error);
        mHandler.sendMessage(msg);
    }


    private void onInstallError(String reason) {
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_FAIL_DIALOG;
        msg.obj = reason;
        mHandler.sendMessage(msg);
        //安装失败，清空文件夹
        UpgradeUtil.delAllDateFile(mUpdatePath);
    }
}
