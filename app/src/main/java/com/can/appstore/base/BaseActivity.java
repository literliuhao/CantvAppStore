package com.can.appstore.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.dataeye.sdk.api.app.DCAgent;

import cn.can.tvlib.utils.PromptUtils;

public abstract class BaseActivity extends FragmentActivity {

    private Dialog mLoadingDialog;
    private Dialog mOffsetLoadingDialog;
    private BroadcastReceiver mHomeKeyReceiver;
    private int loadingSize;
    private int mMsgTextSize = 35;
    private int mMsgTextColor = 0xccffffff;
    private int spaceInPixels = 40;
    public long mDuration = 0;
    private long mEnter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingSize = getResources().getDimensionPixelSize(R.dimen.px136);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHomeKeyListener();
        DCAgent.resume(MyApp.getContext());
        mEnter = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mHomeKeyReceiver);
        DCAgent.pause(MyApp.getContext());
        mDuration = (System.currentTimeMillis() - mEnter) / 1000;
    }

    @Override
    protected void onDestroy() {
        hideLoadingDialog();
        super.onDestroy();
    }

    public void showToast(String msg) {
        PromptUtils.toast(this, msg);
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = PromptUtils.showLoadingDialog(this, loadingSize, getString(R.string.loading),
                    mMsgTextSize, mMsgTextColor, spaceInPixels);
        } else if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show();
        }
    }

    public void showLoadingDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = PromptUtils.showLoadingDialog(this, loadingSize, msg, mMsgTextSize, mMsgTextColor, spaceInPixels);
        } else if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show();
        }
    }

    public void showLoadingDialog(int offsetX) {
        if (mOffsetLoadingDialog == null) {
            mOffsetLoadingDialog = PromptUtils.showLoadingDialog(this, loadingSize, offsetX, getString(R.string.loading),
                    mMsgTextSize, mMsgTextColor, spaceInPixels);
        } else if (!mOffsetLoadingDialog.isShowing()) {
            mOffsetLoadingDialog.show();
        }
    }

    public void showLoadingDialog(String msg, int offsetX) {
        if (mOffsetLoadingDialog == null) {
            mOffsetLoadingDialog = PromptUtils.showLoadingDialog(this, loadingSize, offsetX, msg, mMsgTextSize, mMsgTextColor, spaceInPixels);
        } else if (!mOffsetLoadingDialog.isShowing()) {
            mOffsetLoadingDialog.show();
        }
    }

    public void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        } else if (mOffsetLoadingDialog != null && mOffsetLoadingDialog.isShowing()) {
            mOffsetLoadingDialog.dismiss();
        }
    }

    public boolean isLoadingDialogShowing() {
        return mLoadingDialog != null && mLoadingDialog.isShowing();
    }

    public boolean isAppInfoLoadingDialogShowing() {
        return mOffsetLoadingDialog != null && mOffsetLoadingDialog.isShowing();
    }

    public Context getContext() {
        return this;
    }

    private void startHomeKeyListener() {
        if (mHomeKeyReceiver == null) {
            mHomeKeyReceiver = new HomeKeyReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyReceiver, intentFilter);
    }

    /**
     * Home键监听
     */
    protected void onHomeKeyDown() {
        finish();
    }

    class HomeKeyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                onHomeKeyDown();
            }
        }
    }
}