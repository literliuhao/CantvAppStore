package com.can.appstore.active;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Result;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.widgets.TextProgressBar;

import cn.can.downloadlib.NetworkUtils;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.utils.PromptUtils;
import cn.can.tvlib.utils.StringUtils;
import retrofit2.Response;

import static com.can.appstore.MyApp.mContext;

/**
 * Created by Atangs on 2016/11/1.
 * 活动页
 */

public class ActiveActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "ActivePresenter";
    public static final String EXTRA_ACTIVE_ID = "activeId";
    private final static int ACTIVE_PARTICIPATE = R.mipmap.active_participate;
    private final static int ACTIVE_NORMAL = R.mipmap.active_normal;
    private final static int REFRESH_PROGRESSBAR_TEXT = 0x10;
    private final static int REFRESH_PROGRESSBAR_PROGRESS = 0x11;
    private final static int SHOW_TOAST = 0x12;

    private AppInstallService mInstallService;
    private WebView mActiveWebview;
    private TextProgressBar mActiveTextProgressBar;
    private RelativeLayout mActiveLayout;
    private RelativeLayout mNetworkLayout;
    private Button mRetryBtn;
    private ImageView mImgBg;
    private String mActiveId;
    private int mShowType;
    private LinearLayout mFocusLayout;

    private CanCall<Result<com.can.appstore.entity.Activity>> mActiveData;
    private AppInfo mAppInfo;


    public static void actionStart(Context context, String activeId) {
        Intent intent = new Intent(context, ActiveActivity.class);
        intent.putExtra(EXTRA_ACTIVE_ID, activeId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        Intent intent = getIntent();
        if (intent != null) {
            mActiveId = intent.getStringExtra(EXTRA_ACTIVE_ID);
        }
        mActiveId = TextUtils.isEmpty(mActiveId) ? "52" : mActiveId;
        initUI();

        this.bindService(new Intent(ActiveActivity.this,AppInstallService.class), mInstallServiceConnection,
                BIND_AUTO_CREATE);
    }

    private void initUI() {
        mActiveWebview = (WebView) findViewById(R.id.active_webview);
        mFocusLayout = (LinearLayout) findViewById(R.id.active_focus_layout);
        mImgBg = (ImageView) findViewById(R.id.active_native_imgbg);
        mActiveLayout = (RelativeLayout) findViewById(R.id.active_native_layout);
        mRetryBtn = (Button) findViewById(R.id.network_retry_btn);
        mNetworkLayout = (RelativeLayout) findViewById(R.id.network_retry_layout);
        mActiveTextProgressBar = (TextProgressBar) findViewById(R.id.active_textprogressbar);
        mActiveTextProgressBar.setTextSize(R.dimen.px40);
        mActiveTextProgressBar.setTextFakeBoldText(true);
        mActiveTextProgressBar.setTextColor(Color.WHITE);
        mActiveTextProgressBar.setText(getString(R.string.active_click_participate));
        mActiveTextProgressBar.setOnClickListener(this);
        mRetryBtn.setOnClickListener(this);
    }

    private ServiceConnection mInstallServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mInstallService = ((AppInstallService.InstallBinder) service).getInstallService();

            ((AppInstallService.InstallBinder) service).setActivity(ActiveActivity.this);
            requestActiveData(mActiveId);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mInstallService = null;
        }
    };

    public void requestActiveData(String activeId) {
        mActiveData = HttpManager.getApiService().getActivityInfo(activeId);
        mActiveData.enqueue(new CanCallback<Result<com.can.appstore.entity.Activity>>() {
            @Override
            public void onResponse(CanCall<Result<com.can.appstore.entity.Activity>> call, Response<Result<com.can.appstore.entity.Activity>> response) throws Exception {
                Result<com.can.appstore.entity.Activity> info = response.body();
                if (info == null) {
                    showNetworkRetryView(true, false);
                    return;
                }
                if (info.getData() == null) {
                    return;
                }
                com.can.appstore.entity.Activity active = info.getData();
                boolean isWebView = StringUtils.isEmpty(active.getUrl());
                showNetworkRetryView(false, isWebView);
                if (isWebView) {
                    mAppInfo = active.getRecommend();
                    setNativeLayout(active.getBackground());
                    mInstallService.initDownloadTask(mAppInfo.getUrl());
                } else {
                    loadwebview(active.getUrl());
                }
            }

            @Override
            public void onFailure(CanCall<Result<com.can.appstore.entity.Activity>> call, CanErrorWrapper errorWrapper) {
                if (!cn.can.tvlib.utils.NetworkUtils.isNetworkConnected(mContext.getApplicationContext())) {
                    showNetworkRetryView(true, false);
                }
            }
        });
    }

    public void refreshProgressbarProgress(float progress) {
        mHandler.removeMessages(REFRESH_PROGRESSBAR_PROGRESS);
        Message msg = mHandler.obtainMessage();
        msg.what = REFRESH_PROGRESSBAR_PROGRESS;
        msg.arg1 = (int) progress;
        mHandler.sendMessage(msg);
    }

    public void refreshTextProgressbarTextStatus(int status) {
        mHandler.removeMessages(REFRESH_PROGRESSBAR_TEXT);
        Message msg = mHandler.obtainMessage();
        msg.what = REFRESH_PROGRESSBAR_TEXT;
        msg.arg1 = status;
        mHandler.sendMessage(msg);
    }

    private void setTextProgressbarText(int status) {
        Log.d(TAG, getString(status));
        int curType = status == R.string.active_click_participate ? ACTIVE_PARTICIPATE : ACTIVE_NORMAL;
        if (curType != mShowType) {
            mFocusLayout.setBackgroundResource(curType);
            mShowType = curType;
        }
        mActiveTextProgressBar.setText(getString(status));
    }

    @Override
    public void showToast(int toastStrId) {
        mHandler.removeMessages(SHOW_TOAST);
        Message msg = mHandler.obtainMessage();
        msg.what = SHOW_TOAST;
        msg.arg1 = toastStrId;
        mHandler.sendMessage(msg);
    }

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
    public void setNativeLayout(String url) {
        mActiveLayout.setVisibility(View.VISIBLE);
        mActiveTextProgressBar.requestFocus();
        ImageLoader.getInstance().load(ActiveActivity.this, mImgBg, url);
    }

    public void showNetworkRetryView(boolean isRetry, boolean isWebView) {
        mActiveWebview.setVisibility(!isRetry && isWebView ? View.VISIBLE : View.GONE);
        mActiveLayout.setVisibility(!isRetry && !isWebView ? View.VISIBLE : View.GONE);
        mNetworkLayout.setVisibility(isRetry ? View.VISIBLE : View.GONE);
        if (isRetry) {
            mRetryBtn.requestFocus();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESSBAR_TEXT:
                    setTextProgressbarText(msg.arg1);
                    break;
                case REFRESH_PROGRESSBAR_PROGRESS:
                    mActiveTextProgressBar.setProgress(msg.arg1);
                    break;
                case SHOW_TOAST:
                    ToastUtil.toastShort(getString(msg.arg1));
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.active_textprogressbar:
                mInstallService.clickBtnDownload(mAppInfo);
                break;
            case R.id.network_retry_btn:
                if (!NetworkUtils.isNetworkConnected(ActiveActivity.this.getApplicationContext())) {
                    showToast(R.string.network_connection_disconnect);
                    return;
                }
                requestActiveData(mActiveId);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (mActiveData != null && !mActiveData.isCanceled()) {
            mActiveData.cancel();
            mActiveData = null;
        }

        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        unbindService(mInstallServiceConnection);
        super.onDestroy();
    }
}
