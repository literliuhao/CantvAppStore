package com.can.appstore.active;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.widgets.TextProgressBar;
import com.dataeye.sdk.api.app.DCEvent;
import com.dataeye.sdk.api.app.channel.DCPage;

import cn.can.tvlib.imageloader.ImageLoader;

/**
 * Created by Fuwen on 2016/11/1.
 * 活动页
 */

public class ActiveActivity extends BaseActivity implements ActiveContract.OperationView, View.OnClickListener {
    private final static int ACTIVE_PARTICIPATE = R.mipmap.active_participate;
    private final static int ACTIVE_NORMAL = R.mipmap.active_normal;
    private final static int REFRESH_PROGRESSBAR_TEXT = 0x10;
    private final static int REFRESH_PROGRESSBAR_PROGRESS = 0x11;
    private final static int SHOW_TOAST = 0x12;
    private final static int LOAD_DATA_FAIL = 0x13;
    private final static int SHOW_WEBVIEW = 0x14;

    private WebView mActiveWebview;
    private TextProgressBar mActiveTextProgressBar;
    private RelativeLayout mActiveLayout;
    private ImageView mImgBg;
    private ActivePresenter mActivePresenter;
    private String mActiveId;
    private int mShowType;
    private LinearLayout mFocusLayout;

    public static void actionStart(Context context, String activeId) {
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

    @Override
    protected void onResume() {
        super.onResume();
        DCPage.onEntry(AppConstants.ACTIVITY_DETAIL);
        DCEvent.onEvent(AppConstants.ACTIVITY_DETAIL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DCPage.onExit(AppConstants.ACTIVITY_DETAIL);
        DCEvent.onEventDuration(AppConstants.ACTIVITY_DETAIL, mDuration);
    }

    private void initUI() {
        mActiveWebview = (WebView) findViewById(R.id.active_webview);
        mFocusLayout = (LinearLayout) findViewById(R.id.active_focus_layout);
        mImgBg = (ImageView) findViewById(R.id.active_native_imgbg);
        mActiveLayout = (RelativeLayout) findViewById(R.id.active_native_layout);
        mActiveTextProgressBar = (TextProgressBar) findViewById(R.id.active_textprogressbar);
        mActiveTextProgressBar.setTextSize(R.dimen.px40);
        mActiveTextProgressBar.setTextFakeBoldText(true);
        mActiveTextProgressBar.setTextColor(Color.WHITE);
        mActiveTextProgressBar.setOnClickListener(this);
    }

    @Override
    public void refreshProgressbarProgress(float progress) {
        mHandler.removeMessages(REFRESH_PROGRESSBAR_PROGRESS);
        Message msg = mHandler.obtainMessage();
        msg.what = REFRESH_PROGRESSBAR_PROGRESS;
        msg.arg1 = (int) progress;
        mHandler.sendMessage(msg);
    }

    @Override
    public void refreshTextProgressbarTextStatus(int status) {
        mHandler.removeMessages(REFRESH_PROGRESSBAR_TEXT);
        Message msg = mHandler.obtainMessage();
        msg.what = REFRESH_PROGRESSBAR_TEXT;
        msg.arg1 = status;
        mHandler.sendMessage(msg);
    }

    @Override
    public void showActiveToast(int toastStrId) {
        mHandler.removeMessages(SHOW_TOAST);
        Message msg = mHandler.obtainMessage();
        msg.what = SHOW_TOAST;
        msg.arg1 = toastStrId;
        mHandler.sendMessage(msg);
    }

    @Override
    public void loadwebview(String url) {
        mActiveWebview.setWebViewClient(new CanWebViewClient());
        mActiveWebview.setVisibility(View.VISIBLE);
        mActiveWebview.loadUrl(url);
    }

    @Override
    public void showBackground(String url) {
        mActiveLayout.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().load(ActiveActivity.this, mImgBg, url);
    }

    @Override
    public void showProgreessbar() {
        mFocusLayout.setVisibility(View.VISIBLE);
        mActiveTextProgressBar.requestFocus();
    }

    @Override
    public void loadDataFail(int toastId) {
        hideLoadingDialog();
        mHandler.removeMessages(LOAD_DATA_FAIL);
        Message msg = mHandler.obtainMessage();
        msg.what = LOAD_DATA_FAIL;
        msg.arg1 = toastId;
        if (toastId == R.string.no_network) {
            mHandler.sendMessage(msg);
        } else {
            mHandler.sendMessageDelayed(msg, 500);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESSBAR_TEXT:
                    int status = msg.arg1;
                    int showtype = status == R.string.active_click_participate ? ACTIVE_PARTICIPATE : ACTIVE_NORMAL;
                    if (showtype != mShowType) {
                        mFocusLayout.setBackgroundResource(showtype);
                        mShowType = showtype;
                    }
                    mActiveTextProgressBar.setText(getString(status));
                    break;
                case REFRESH_PROGRESSBAR_PROGRESS:
                    mActiveTextProgressBar.setProgress(msg.arg1);
                    break;
                case SHOW_TOAST:
                    showToast(msg.arg1);
                    break;
                case LOAD_DATA_FAIL:
                    showToast(msg.arg1);
                    ActiveActivity.this.finish();
                    break;
                case SHOW_WEBVIEW:
                    mActiveWebview.setWebViewClient(new CanWebViewClient());
                    mActiveWebview.setVisibility(View.VISIBLE);
                    mActiveWebview.loadUrl((String) msg.obj);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.active_textprogressbar:
                mActivePresenter.clickBtnDownload();
                break;
        }
    }

    private class CanWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (isLoadingDialogShowing()) {
                hideLoadingDialog();
            }
        }
    }

    @Override
    protected void onStop() {
        if (mActivePresenter != null) {
            mActivePresenter.release();
            mActivePresenter = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

}
