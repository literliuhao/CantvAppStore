package com.can.appstore.upgrade.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import com.can.appstore.upgrade.UpgradeUtil;
import com.can.appstore.upgrade.activity.UpgradeInfoActivity;
import com.google.gson.Gson;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import java.io.File;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;

/**
 * Created by syl on 2016/11/2.
 * 应用商城自更新服务
 */

public class UpgradeService extends IntentService {
    public static final String TAG = "UpgradeService";
    //常量
    public static final String UPGRADE_INFO = "upgrade";
    public static final String NO_UPGRADE_INFO = "no_upgrade";
    //更新信息常量
    public static final String VERSION_NAME = "VersionName";
    public static final String NEW_FEATURE = "NewFeature";
    public static final String FILE_NAME = "FileName";
    public static final String UPGRADE_SIZE = "UpgradeSize";
    public static final String FILE_PATH = "filepath";
    //全局变量
    private int mLocalVersion;
    private String mUpdatePath;
    private String mFileName;
    private UpgradeInfo mUpgradeInfo;
    //下载相关
    private DownloadManager mManager;

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

        mUpdatePath = mManager.getDownloadPath() + "/upgrade";
        File dir = new File(mUpdatePath);

        dir.mkdirs();
        dir.setWritable(true, false);
        dir.setReadable(true, false);
        dir.setExecutable(true, false);

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

        storeInfo();

        if (checkIsExistApk()) {
            showUpgradeInfo();
        } else {
            //先清空本地存放Apk的空间
            UpgradeUtil.delAllDateFile(mUpdatePath);
            downLoadApk(mUpgradeInfo.apkUrl);
        }
    }

    /**
     * 储存版本信息到sp中
     */
    private void storeInfo() {
        SharedPreferences sp = getSharedPreferences(UPGRADE_INFO, Activity.MODE_PRIVATE);
        String info = new Gson().toJson(mUpgradeInfo);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(UPGRADE_INFO, info);
        editor.commit();
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


    private void onLoadingCompleted() {
        //校验MD5
        String localMD5 = UpgradeUtil.getFileMD5(mFileName);
        if (!mUpgradeInfo.apkMd5.equalsIgnoreCase(localMD5)) {
            Log.d(TAG, "onLoadingCompleted: MD5error");
            UpgradeUtil.delAllDateFile(mUpdatePath);
        } else {
            Log.d(TAG, "onLoadingCompleted: MD5success");
            showUpgradeInfo();
        }
    }

    private void showUpgradeInfo() {
        Intent intent = new Intent(UpgradeService.this, UpgradeInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(VERSION_NAME, mUpgradeInfo.versionName);
        intent.putExtra(NEW_FEATURE, mUpgradeInfo.newFeature);
        intent.putExtra(FILE_NAME, mFileName);
        intent.putExtra(UPGRADE_SIZE, mUpgradeInfo.fileSize);
        intent.putExtra(FILE_PATH, mUpdatePath);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

}
