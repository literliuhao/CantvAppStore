package com.can.appstore.upgrade;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.tvlib.utils.ToastUtils;

/**
 * Created by syl on 2016/11/2.
 * 应用商城自更新服务
 */

public class UpgradeService extends IntentService {
    public static final String TAG = "UpgradeService";
    public static final int SHOW_UPGRADE_INFO_DIALOG = 1;
    public static final int SHOW_UPGRADE_INSTALL_COMPLETE = 2;
    public static final int SHOW_UPGRADE_PROGRESSBAR_DIALOG = 3;
    public static final int SHOW_UPGRADE_FAIL_DIALOG = 4;
    private UpgradeProgressBarDialog mProgressBarDialog;
    private int mLocalVersion;
    private UpgradeInfo mUpgradeInfo;
    private String mUpdatePath;
    private String mFileName;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPGRADE_INFO_DIALOG:
                    UpgradeInfo upgradeInfo = (UpgradeInfo) msg.obj;
                    UpgradeInFoDialog.OnUpgradeClickListener listener = new UpgradeInFoDialog.OnUpgradeClickListener() {
                        @Override
                        public void onClick() {
                            mHandler.sendEmptyMessage(SHOW_UPGRADE_PROGRESSBAR_DIALOG);
                        }
                    };
                    UpgradeInFoDialog dialog = new UpgradeInFoDialog(UpgradeService.this, "系统升级", upgradeInfo.getVersionName(), upgradeInfo.getUpdateLog(), "安装", listener);
                    dialog.show();
                    break;
                case SHOW_UPGRADE_INSTALL_COMPLETE:
                    if (mProgressBarDialog != null) {
                        mProgressBarDialog.dismiss();
                    }
                    UpgradeInfo installCompleteInfo = (UpgradeInfo) msg.obj;
                    UpgradeInFoDialog.OnUpgradeClickListener listener1 = new UpgradeInFoDialog.OnUpgradeClickListener() {
                        @Override
                        public void onClick() {
                            ToastUtils.showMessage(UpgradeService.this, "运行apk");
                        }
                    };
                    UpgradeInFoDialog upgradeInfoDialog = new UpgradeInFoDialog(UpgradeService.this, "系统升级", installCompleteInfo.getVersionName(), installCompleteInfo.getUpdateLog(), "运行", listener1);
                    upgradeInfoDialog.show();
                    break;
                case SHOW_UPGRADE_PROGRESSBAR_DIALOG:
                    mProgressBarDialog = new UpgradeProgressBarDialog(UpgradeService.this);
                    mProgressBarDialog.show();
                    Message msg1 = Message.obtain();
                    msg1.what = SHOW_UPGRADE_INSTALL_COMPLETE;
                    msg1.obj = mUpgradeInfo;
                    mHandler.sendMessageDelayed(msg1, 5000);
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
        ToastUtils.showMessage(this, "启动服务");
        requsetInfo();
    }

    private void requsetInfo() {
        // TODO: 2016/11/2  联网获取数据
        //假数据
        mUpgradeInfo = new UpgradeInfo();
        mUpgradeInfo.setVersionCode(2);
        mUpgradeInfo.setVersionName("CAN UI1.0");
        mUpgradeInfo.setMd5("");
        mUpgradeInfo.setUpdateLog("1、 解决已知bug \n2、 添加新功能 \n3、 添加新功能\n3、 添加新功能\n3、 添加新功能\n3、 添加新功能\n3、 添加新功能\n3、 添加新功能");
        //String url = "http://cdn.can.cibntv.net/02/mam/Public/Picture/moviepost/16-11/01/01/fuji.jpg";
        String url = "http://app.znds.com/down/20160909/dsj2.0-2.9.1-dangbei.apk ";
        mUpgradeInfo.setUrl(url);

        checkUpgradeInfo();
    }

    /**
     * 检测更新信息并作出逻辑判断
     */
    private void checkUpgradeInfo() {
        // 下载到本地的更新的APK的地址
        mUpdatePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/updateapk";
        if (!Util.isFileExist(mUpdatePath)) {
            Util.creatDir(mUpdatePath);
        }
        mFileName = mUpdatePath + "/" + mUpgradeInfo.getVersionCode() + ".apk";
        //获取本地的版本号
        try {
            mLocalVersion = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "获取到版本号=" + mLocalVersion);
        // TODO: 2016/11/3  对比版本号是否下载
        if (mUpgradeInfo.getVersionCode() <= mLocalVersion) {
            return;
        }
        if (checkIsExistApk()) {
            Message msg = Message.obtain();
            msg.what = SHOW_UPGRADE_INFO_DIALOG;
            msg.obj = mUpgradeInfo;
            mHandler.sendMessage(msg);
        } else {
            //先清空本地存放Apk的空间
            Util.delAllDateFile(mUpdatePath);
            downLoadApk(mUpgradeInfo.getUrl());
        }
    }

    /**
     * 检测是否存在已经下载完成但没有安装的最新版本
     */
    private boolean checkIsExistApk() {
        if (Util.isFileExist(mFileName)) {
            String localMD5 = Util.getFileMD5(mFileName);
            //if(mUpgradeInfo.getMd5().equalsIgnoreCase(localMD5)){
            if (true) {
                Log.d(TAG, "存在已下载未安装的apk");
                return true;
            }
        }
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
        task.setFileName("2.apk");
        task.setSaveDirPath(mUpdatePath);
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

    private void onLoadingCompleted() {
        String localMD5 = Util.getFileMD5(mFileName);
        //if(!mUpgradeInfo.getMd5().equalsIgnoreCase(localMD5)){
        if (false) {
            Util.delAllDateFile(mUpdatePath);
        } else {
            //显示安装apk对话框
            Message msg = Message.obtain();
            msg.what = SHOW_UPGRADE_INFO_DIALOG;
            msg.obj = mUpgradeInfo;
            mHandler.sendMessage(msg);
        }
    }

    private void onLoadingError() {
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_FAIL_DIALOG;
        msg.obj = "下载异常，升级失败";
        mHandler.sendMessage(msg);
    }

    private void onInstallComplete() {
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_INFO_DIALOG;
        mHandler.sendMessage(msg);
    }

    private void onInstallError() {
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_FAIL_DIALOG;
        msg.obj = "系统内存不足，升级失败";
        mHandler.sendMessage(msg);
    }
}
