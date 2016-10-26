package com.can.appstore.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

import cn.can.tvlib.utils.PromptUtils;

public abstract class BaseActivity extends FragmentActivity {

    private Dialog mLoadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onDestroy() {
        hideLoadingDialog();
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

    public Context getContext() {
        return this;
    }
}