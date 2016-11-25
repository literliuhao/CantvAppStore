package com.can.appstore.active;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.widgets.TextProgressBar;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.utils.PromptUtils;

/**
 * Created by Atangs on 2016/11/1.
 * 活动页
 */

public class ActiveActivity extends Activity implements ActiveContract.OperationView, View.OnClickListener {
    private final static int REFRESH_PROGRESSBAR_TEXT = 0x10;
    private final static int REFRESH_PROGRESSBAR_PROGRESS = 0x11;
    private final static int SHOW_TOAST = 0x12;

    private WebView mActiveWebview;
    private TextProgressBar mActiveTextProgressBar;
    private RelativeLayout mActiveLayout;
    private RelativeLayout mNetworkLayout;
    private Button mRetryBtn;
    private ImageView mImgBg;
    private ActivePresenter mActivePresenter;
    private FocusMoveUtil mFocusMoveUtil;
    private String mActiveId;

    public static void actionStart(Context context, String activeId){
        Intent intent = new Intent(context, ActiveActivity.class);
        intent.putExtra("activeId", activeId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        Intent intent = getIntent();
        if (intent != null) {
            mActiveId = intent.getStringExtra("activeId");
        }
        mActiveId = TextUtils.isEmpty(mActiveId) ? "52" : mActiveId;
        initUI();
        mActivePresenter = new ActivePresenter(this, ActiveActivity.this);
        mActivePresenter.requestActiveData(mActiveId);
    }

    private void initUI() {
        mActiveWebview = (WebView) findViewById(R.id.active_webview);
        mImgBg = (ImageView) findViewById(R.id.active_native_imgbg);
        mActiveTextProgressBar = (TextProgressBar) findViewById(R.id.active_textprogressbar);
        mActiveLayout = (RelativeLayout) findViewById(R.id.active_native_layout);
        mRetryBtn = (Button) findViewById(R.id.network_retry_btn);
        mNetworkLayout = (RelativeLayout) findViewById(R.id.network_retry_layout);
        mActiveTextProgressBar.setTextSize(R.dimen.px24);

        mActiveTextProgressBar.setOnClickListener(this);
        mRetryBtn.setOnClickListener(this);
    }

    private Runnable mfocusMoveRunnable = new Runnable() {
        @Override
        public void run() {
            if (mActiveTextProgressBar != null && mActiveTextProgressBar.isFocused()) {
                mFocusMoveUtil.startMoveFocus(mActiveTextProgressBar, 1.03f);
            }
        }
    };

    @Override
    public void refreshProgressbarProgress(float progress) {
        mHandler.removeMessages(REFRESH_PROGRESSBAR_PROGRESS);
        Message msg = mHandler.obtainMessage();
        msg.what = REFRESH_PROGRESSBAR_PROGRESS;
        msg.arg1 = (int) progress;
        mHandler.sendMessage(msg);
    }

    @Override
    public void refreshTextProgressbarTextStatus(String status) {
        mHandler.removeMessages(REFRESH_PROGRESSBAR_TEXT);
        Message msg = mHandler.obtainMessage();
        msg.what = REFRESH_PROGRESSBAR_TEXT;
        msg.obj = status;
        mHandler.sendMessage(msg);
    }

    @Override
    public void showToast(String toastContent) {
        mHandler.removeMessages(SHOW_TOAST);
        Message msg = mHandler.obtainMessage();
        msg.what = SHOW_TOAST;
        msg.obj = toastContent;
        mHandler.sendMessage(msg);
    }

    @Override
    public void loadwebview(String url) {
        mActiveWebview.setWebViewClient(new CanWebViewClient());
        mActiveWebview.setVisibility(View.VISIBLE);
        mActiveWebview.loadUrl(url);
    }

    /**
     * 需要考虑背景图片位加载成功的情况
     *
     * @param url
     */
    @Override
    public void setNativeLayout(String url) {
        mActiveLayout.setVisibility(View.VISIBLE);
        mActiveTextProgressBar.requestFocus();
        ImageLoader.getInstance().load(ActiveActivity.this, mImgBg, url);
        mFocusMoveUtil = new FocusMoveUtil(ActiveActivity.this.getApplicationContext(), R
                .drawable.btn_focus, getWindow().getDecorView(), false);
        mHandler.postDelayed(mfocusMoveRunnable, 10);
    }

    @Override
    public void showNetworkRetryView(boolean isRetry, boolean isWebView) {
        mActiveWebview.setVisibility(!isRetry && isWebView ? View.VISIBLE : View.GONE);
        mActiveLayout.setVisibility(!isRetry && !isWebView ? View.VISIBLE : View.GONE);
        mNetworkLayout.setVisibility(isRetry ? View.VISIBLE : View.GONE);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESSBAR_TEXT:
                    mActiveTextProgressBar.setText((String) msg.obj);
                    mActiveTextProgressBar.invalidate();
                    break;
                case REFRESH_PROGRESSBAR_PROGRESS:
                    mActiveTextProgressBar.setProgress(msg.arg1);
                    break;
                case SHOW_TOAST:
                    ToastUtil.toastShort((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        if (mActivePresenter != null) {
            mActivePresenter.removeAllListener();
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.active_textprogressbar:
                mActivePresenter.clickBtnDownload();
                break;
            case R.id.network_retry_btn:
                mActivePresenter.requestActiveData(mActiveId);
                break;
        }
    }

    private class CanWebViewClient extends WebViewClient {
        Dialog loadingDialog;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadingDialog = PromptUtils.showLoadingDialog(ActiveActivity.this);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            if (loadingDialog != null) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }

    }
}
