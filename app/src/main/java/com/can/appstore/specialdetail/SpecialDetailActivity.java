package com.can.appstore.specialdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Result;
import com.can.appstore.entity.SpecialTopic;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.NetworkUtils;
import retrofit2.Response;

public class SpecialDetailActivity extends BaseActivity {
    public static final String EXTRA_TOPIC_ID = "topicId";
    public static final int FOCUS_IMAGE = R.mipmap.image_focus;
    private CanRecyclerView mCanRecyclerView;
    private CanCall<Result<SpecialTopic>> mSpecialTopic;
    private ImageView mDetailImgBg;
    private View mCurrFocusView;
    private FocusMoveUtil mFocusMoveUtil;
    private Handler mHandler = new Handler();
    private List<AppInfo> mRecommdList;
    private RelativeLayout mNetworkLayout;
    private Button mRetryBtn;
    private String mTopicId;

    private Runnable mfocusMoveRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrFocusView != null && mCurrFocusView.isFocused()) {
                mFocusMoveUtil.startMoveFocus(mCurrFocusView);
            }
        }
    };

    public static void actionStart(Context context, String topicId) {
        Intent intent = new Intent(context, SpecialDetailActivity.class);
        intent.putExtra(EXTRA_TOPIC_ID, topicId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_detail);
        mRecommdList = new ArrayList<>();
        getTopicId();
        initView();

        //焦点工具初始化
        mFocusMoveUtil = new FocusMoveUtil(SpecialDetailActivity.this, getWindow().getDecorView(), FOCUS_IMAGE);

        loadData();
    }

    private void getTopicId() {
        Intent intent = getIntent();
        if (intent != null) {
            mTopicId = intent.getStringExtra(EXTRA_TOPIC_ID);
        }
        mTopicId = TextUtils.isEmpty(mTopicId) ? "14" : mTopicId;
    }

    private void initView() {
        mNetworkLayout = (RelativeLayout) findViewById(R.id.network_retry_layout);
        mDetailImgBg = (ImageView) findViewById(R.id.special_detail_img);
        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.special_detail_crview);
        mCanRecyclerView.addItemDecoration(new CanRecyclerViewDivider(32));
        CanRecyclerView.CanLinearLayoutManager layoutManager = new CanRecyclerView.CanLinearLayoutManager(SpecialDetailActivity.this, CanRecyclerView.HORIZONTAL, false);
        mCanRecyclerView.setLayoutManager(layoutManager);
        mRetryBtn = (Button) findViewById(R.id.network_retry_btn);

        addViewListener();
    }

    private void loadData() {
        showNetworkRetryView(false);
        mSpecialTopic = HttpManager.getApiService().getSpecialTopic(mTopicId);
        mSpecialTopic.enqueue(new CanCallback<Result<SpecialTopic>>() {
            @Override
            public void onResponse(CanCall<Result<SpecialTopic>> call, Response<Result<SpecialTopic>> response) throws Exception {
                Result<SpecialTopic> info = response.body();
                Log.d("SpecialDetailActivity", info.toString());
                if (info == null) {
                    showNetworkRetryView(true);
                    return;
                }
                if (info.getData() == null) {
                    return;
                }
                ImageLoader.getInstance().load(SpecialDetailActivity.this, mDetailImgBg, info.getData().getBackground());
                mRecommdList.addAll(info.getData().getRecommend());
                showRecycleView();
            }

            @Override
            public void onFailure(CanCall<Result<SpecialTopic>> call, CanErrorWrapper errorWrapper) {
                Log.d("SpecialDetailActivity", errorWrapper.getReason());
                if(!NetworkUtils.isNetworkConnected(SpecialDetailActivity.this.getApplicationContext())){
                    showToast(R.string.network_connection_disconnect);
                    showNetworkRetryView(true);
                }
            }
        });
    }

    private void showNetworkRetryView(boolean isRetry) {
        mCanRecyclerView.setVisibility(isRetry ? View.GONE : View.VISIBLE);
        mDetailImgBg.setVisibility(isRetry ? View.GONE : View.VISIBLE);
        mNetworkLayout.setVisibility(isRetry ? View.VISIBLE : View.GONE);
        if (isRetry) {
            mRetryBtn.requestFocus();
            mFocusMoveUtil.hideFocus();
            mHandler.removeCallbacks(mfocusMoveRunnable);
        }
    }

    private void showRecycleView() {
        //设置Adapter
        SpecialDetailAdapter adapter = new SpecialDetailAdapter(mRecommdList, this);
        mCanRecyclerView.setAdapter(adapter);
        addAdapterListener(adapter);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCanRecyclerView.getChildAt(0).requestFocus();
                mFocusMoveUtil.hideFocusForShowDelay(500);
            }
        }, 100);
    }

    private void addAdapterListener(SpecialDetailAdapter adapter) {
        adapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                if (!NetworkUtils.isNetworkConnected(SpecialDetailActivity.this.getApplicationContext())) {
                    showToast(R.string.network_connection_disconnect);
                    return;
                }
                AppInfo appInfo = (AppInfo) data;
                if (appInfo != null && appInfo.getId() != null) {
                    AppDetailActivity.actionStart(SpecialDetailActivity.this, appInfo.getId());
                } else {
                    showToast(R.string.data_error);
                }
            }
        });
        adapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mCurrFocusView = view;
                    mHandler.removeCallbacks(mfocusMoveRunnable);
                    mHandler.postDelayed(mfocusMoveRunnable, 50);
                }
                view.setSelected(hasFocus);
            }
        });
    }

    private void addViewListener() {
        mCanRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mHandler.removeCallbacks(mfocusMoveRunnable);
                mHandler.postDelayed(mfocusMoveRunnable, 50);
            }
        });
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isNetworkConnected(SpecialDetailActivity.this.getApplicationContext())) {
                    showToast(R.string.network_connection_disconnect);
                    return;
                }
                loadData();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (mSpecialTopic != null) {
            mSpecialTopic.cancel();
            mSpecialTopic = null;
        }
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
            mFocusMoveUtil = null;
        }
    }
}
