package com.can.appstore.upgrade.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.can.appstore.R;
import com.can.appstore.index.IndexActivity;
import com.can.appstore.upgrade.InstallApkListener;
import com.can.appstore.upgrade.UpgradeUtil;
import com.can.appstore.upgrade.service.UpgradeService;
import com.can.appstore.upgrade.widgets.UpgradeProgressBar;

/**
 * Created by syl on 2016/12/7.
 */

public class ProgressActivity extends Activity {
    private static final String TAG = "ProgressActivity";
    public static final String FAIL_REASON = "failReason";
    private UpgradeProgressBar mProgressBar;
    private int mProgress;
    private long mTime;
    private String mFileName;
    private String mFilePath;
    private long mUpgradeSize;
    private LinearLayout mView;
    //全局常量
    public static final int PROGRESS_MAX = 70;
    public static final int INSTALL_DELAY = 200;
    //message.what
    public static final int REFRESH_PROGRESS = 1;
    public static final int FINISH = 2;
    public static final int INSTALL = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case REFRESH_PROGRESS:
                    mProgressBar.setProgress(mProgress);
                    mProgress++;
                    if (mProgress <= PROGRESS_MAX) {
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 10);
                    } else {
                        mHandler.sendEmptyMessageDelayed(FINISH, INSTALL_DELAY);
                    }
                    break;
                case FINISH:
                    mView.setVisibility(View.INVISIBLE);
                    mHandler.sendEmptyMessageDelayed(INSTALL,100);
                    Intent intent = new Intent(ProgressActivity.this, IndexActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ProgressActivity.this.startActivity(intent);
                    finish();
                    break;
                case INSTALL:
                    Log.d(TAG, "handleMessage: " + (System.currentTimeMillis() - mTime));
                    install();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upgrade_progress_bar);
        mTime = System.currentTimeMillis();
        initView();
        parseIntentData();
    }

    private void parseIntentData() {
        Intent intent = getIntent();
        mFileName = intent.getStringExtra(UpgradeService.FILE_NAME);
        mUpgradeSize = intent.getLongExtra(UpgradeService.UPGRADE_SIZE, 0);
        mFilePath = intent.getStringExtra(UpgradeService.FILE_PATH);
    }

    private void initView() {
        setContentView(R.layout.layout_upgrade_progress_bar);
        mView = (LinearLayout) findViewById(R.id.ll_progress_bar);
        mProgressBar = (UpgradeProgressBar) findViewById(R.id.pb_upgrade);
        mProgressBar.setMax(PROGRESS_MAX);
        mHandler.sendEmptyMessage(REFRESH_PROGRESS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





    private void install() {
        UpgradeUtil.installApk(this, mFileName, mUpgradeSize, new InstallApkListener() {
            @Override
            public void onInstallSuccess() {
                Log.d(TAG, "onInstallSuccess: ");
            }

            @Override
            public void onInstallFail(String reason) {
                Log.d(TAG, "onInstallFail: ");
                //安装失败，清空文件夹
                UpgradeUtil.delAllDateFile(mFilePath);
                Intent intent = new Intent(ProgressActivity.this, UpgradeFailActivity.class);
                intent.putExtra(FAIL_REASON, reason);
                ProgressActivity.this.startActivity(intent);
                finish();
            }
        });
    }
}
