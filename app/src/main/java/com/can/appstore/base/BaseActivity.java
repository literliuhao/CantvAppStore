package com.can.appstore.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.can.appstore.R;

import cn.can.tvlib.utils.PromptUtils;

public abstract class BaseActivity extends FragmentActivity {

    private Dialog mLoadingDialog;
    private Dialog mAppInfoLoadingDialog;
    private BroadcastReceiver mHomeKeyReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHomeKeyListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mHomeKeyReceiver);
    }

    @Override
    protected void onDestroy() {
        hideLoadingDialog();
        hideAppInfoLoadingDialog();
        super.onDestroy();
    }

    public void showToast(String msg) {
        PromptUtils.toastShort(this, msg);
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = PromptUtils.showLoadingDialog(this);
        } else if (mLoadingDialog.isShowing()) {
            return;
        } else {
            mLoadingDialog.show();
        }
    }

    public void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void showAppInfoLoadingDialog() {
        if (mAppInfoLoadingDialog == null) {
            mAppInfoLoadingDialog = PromptUtils.showLoadingDialog(this, -2, getResources().getDimensionPixelSize(R.dimen
                    .px132));
        } else if (mAppInfoLoadingDialog.isShowing()) {
            return;
        } else {
            mAppInfoLoadingDialog.show();
        }
    }

    public void hideAppInfoLoadingDialog() {
        if (mAppInfoLoadingDialog != null && mAppInfoLoadingDialog.isShowing()) {
            mAppInfoLoadingDialog.dismiss();
        }
    }

    public boolean isLoadingDialogShowing() {
        return mLoadingDialog != null && mLoadingDialog.isShowing();
    }

    public boolean isAppInfoLoadingDialogShowing() {
        return mAppInfoLoadingDialog != null && mAppInfoLoadingDialog.isShowing();
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
    protected void onHomeKeyListener() {
    }

    class HomeKeyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                onHomeKeyListener();
            }
        }
    }
}