package com.can.appstore.active;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.widgets.TextProgressBar;

/**
 * Created by Atangs on 2016/11/1.
 */

public class ActiveActivity extends Activity implements ActiveContract.OperationView{
    private final static int REFRESH_PROGRESSBAR_PROGRESS = 0x11;
    private final static int SHOW_TOAST = 0x12;

    private WebView mActiveWebview;
    private TextProgressBar mActiveTextProgressBar;
    private ActivePresenter mActivePresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);

        initUI();
        mActivePresenter = new ActivePresenter(this,ActiveActivity.this);
        mActivePresenter.initDownloadTask();
    }

    private void initUI() {
        mActiveWebview = (WebView) findViewById(R.id.active_webview);
        mActiveTextProgressBar = (TextProgressBar) findViewById(R.id.active_textprogressbar);
        mActiveTextProgressBar.setTextSize(R.dimen.px24);
        mActiveTextProgressBar.requestFocus();
        addViewListener();
    }

    private void addViewListener() {
        mActiveTextProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivePresenter.startDownload();
            }
        });
        mActiveTextProgressBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
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
    public void refreshTextProgressbarTextStatus(String status) {
        mActiveTextProgressBar.setText(status);
    }

    @Override
    public void showToast(String toastContent) {
        mHandler.removeMessages(REFRESH_PROGRESSBAR_PROGRESS);
        Message msg = mHandler.obtainMessage();
        msg.what = SHOW_TOAST;
        msg.obj= toastContent;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_PROGRESSBAR_PROGRESS:
                    mActiveTextProgressBar.setProgress(msg.arg1);
                    break;
                case SHOW_TOAST:
                    ToastUtil.toastShort((String) msg.obj);
                    break;
            }
        }
    };
}
