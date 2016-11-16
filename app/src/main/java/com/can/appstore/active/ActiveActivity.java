package com.can.appstore.active;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.widgets.TextProgressBar;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;

/**
 * Created by Atangs on 2016/11/1.
 * 活动页
 */

public class ActiveActivity extends Activity implements ActiveContract.OperationView {
    private final static int REFRESH_PROGRESSBAR_TEXT = 0x10;
    private final static int REFRESH_PROGRESSBAR_PROGRESS = 0x11;
    private final static int SHOW_TOAST = 0x12;

    private WebView mActiveWebview;
    private TextProgressBar mActiveTextProgressBar;
    private RelativeLayout mActiveLayout;
    private ImageView mImgBg;
    private ActivePresenter mActivePresenter;
    private FocusMoveUtil mFocusMoveUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);

        initUI();
        mActivePresenter = new ActivePresenter(this, ActiveActivity.this);
    }

    private void initUI() {
        mActiveWebview = (WebView) findViewById(R.id.active_webview);
        mImgBg = (ImageView) findViewById(R.id.active_native_imgbg);
        mActiveTextProgressBar = (TextProgressBar) findViewById(R.id.active_textprogressbar);
        mActiveLayout = (RelativeLayout) findViewById(R.id.active_native_layout);
        mActiveTextProgressBar.setTextSize(R.dimen.px24);
        addViewListener();
    }

    private Runnable mfocusMoveRunnable = new Runnable() {
        @Override
        public void run() {
            if(mActiveTextProgressBar != null && mActiveTextProgressBar.isFocused()){
                mFocusMoveUtil.startMoveFocus(mActiveTextProgressBar,1.03f);
            }
        }
    };

    private void addViewListener() {
        mActiveTextProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivePresenter.clickBtnDownload();
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
        mActiveWebview.setVisibility(View.VISIBLE);
        mActiveWebview.loadUrl(url);
    }

    /**
     * 需要考虑背景图片位加载成功的情况
     * @param url
     */
    @Override
    public void setNativeLayout(String url) {
        mActiveLayout.setVisibility(View.VISIBLE);
        mActiveTextProgressBar.requestFocus();
        ImageLoader.getInstance().load(ActiveActivity.this,mImgBg,url);
        mFocusMoveUtil = new FocusMoveUtil(ActiveActivity.this.getApplicationContext(), R
                .drawable.btn_focus, getWindow().getDecorView(), false);
        mHandler.postDelayed(mfocusMoveRunnable,10);
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
    protected void onDestroy() {
        if(mActivePresenter != null){
            mActivePresenter.removeAllListener();
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
