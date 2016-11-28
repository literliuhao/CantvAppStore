package com.can.appstore.upgrade.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.upgrade.view.UpgradeFailDialog;
import com.can.appstore.upgrade.view.UpgradeInFoDialog;
import com.can.appstore.upgrade.UpgradeUtil;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;

import java.io.File;


/**
 * Created by syl on 2016/11/17.
 * Bugly应用商城自更新服务
 */

public class BuglyUpgradeService extends IntentService {
    private static final String TAG = "BuglyUpgradeService";
    public static final int SHOW_UPGRADE_INFO_DIALOG = 1;
    public static final int SHOW_UPGRADE_FAIL_DIALOG = 2;
    private int mLocalVersion;
    private String mUpdatePath;
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
                            //先清空本地存放Apk的空间
                            mUpdatePath = "/storage/emulated/0/Download/com.can.appstore";
                            UpgradeUtil.delAllDateFile(mUpdatePath);
                            DownloadTask downloadTask = Beta.startDownload();
                            Log.d(TAG, "checkUpgradeInfo: " + downloadTask.getSaveFile());
                        }
                    };
                    UpgradeInFoDialog dialog = new UpgradeInFoDialog(BuglyUpgradeService.this,getResources().getString(R.string.system_upgrade), mUpgradeInfo
                            .versionName, mUpgradeInfo.newFeature, getResources().getString(R.string.download), listener);
                    dialog.show();
                    break;
                case SHOW_UPGRADE_FAIL_DIALOG:
                    String content = (String) msg.obj;
                    UpgradeFailDialog failDialog = new UpgradeFailDialog(BuglyUpgradeService.this, content);
                    failDialog.show();
                    break;
            }

        }
    };

    public BuglyUpgradeService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (requestInfo()) {
            return;
        }
        initDownLoadListener();
        checkUpgradeInfo();
    }

    private void initDownLoadListener() {
        Beta.registerDownloadListener(new DownloadListener() {
            @Override
            public void onReceive(com.tencent.bugly.beta.download.DownloadTask downloadTask) {
                Log.d(TAG, "onReceive: ");
            }

            @Override
            public void onCompleted(com.tencent.bugly.beta.download.DownloadTask downloadTask) {
                File saveFile = downloadTask.getSaveFile();
                Log.d(TAG, "onCompleted: " + saveFile);
            }

            @Override
            public void onFailed(com.tencent.bugly.beta.download.DownloadTask downloadTask, int i, String s) {
                Log.d(TAG, "onFailed: ");
                onLoadingError();
            }
        });

    }

    private boolean requestInfo() {
        mUpgradeInfo = Beta.getUpgradeInfo();
        if (mUpgradeInfo == null) {
            return true;
        }
        Log.d(TAG, "requestInfo: " + mUpgradeInfo.newFeature);
        return false;
    }

    /**
     * 检测更新信息并作出逻辑判断
     */
    private void checkUpgradeInfo() {
        try {
            mLocalVersion = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext()
                    .getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "LocalVersionCode=" + mLocalVersion + ",UpGradeVersionCode=" + mUpgradeInfo.versionCode);
        Log.d(TAG, "url=" + mUpgradeInfo.apkUrl);
        if (mUpgradeInfo.versionCode <= mLocalVersion) {
            return;
        }
        mHandler.sendEmptyMessage(SHOW_UPGRADE_INFO_DIALOG);
    }

    private void onLoadingError() {
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_FAIL_DIALOG;
        msg.obj = getResources().getString(R.string.load_error);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Beta.unregisterDownloadListener();
    }
}
