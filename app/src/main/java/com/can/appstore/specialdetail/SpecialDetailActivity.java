package com.can.appstore.specialdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Result;
import com.can.appstore.entity.SpecialTopic;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.search.SearchActivity;
import java.util.List;
import com.can.appstore.specialtopic.SpecialActivity;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import retrofit2.Response;

public class SpecialDetailActivity extends Activity {
    public static final String EXTRA_TOPIC_ID="topicId";
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

    public static void actionStart(Context context, String topicId){
        Intent intent = new Intent(context, SpecialActivity.class);
        intent.putExtra("topicId", topicId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_detail);
        Intent intent = getIntent();
        if(intent != null){
            mTopicId = intent.getStringExtra(EXTRA_TOPIC_ID);
        }
        mTopicId = TextUtils.isEmpty(mTopicId) ? "14" : mTopicId;
        initView();

        //焦点工具初始化
        mFocusMoveUtil = new FocusMoveUtil(SpecialDetailActivity.this, getWindow().getDecorView(), R.mipmap.btn_focus);

        requestTopicDetail();
    }

    private void initView() {
        mNetworkLayout = (RelativeLayout) findViewById(R.id.network_retry_layout);
        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.special_detail_crview);
        mDetailImgBg = (ImageView) findViewById(R.id.special_detail_img);
        mRetryBtn = (Button) findViewById(R.id.network_retry_btn);
        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.network_retry_btn) {
                    requestTopicDetail();
                }
            }
        });
    }

    private Runnable mfocusMoveRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrFocusView != null && mCurrFocusView.isFocused()) {
                mFocusMoveUtil.startMoveFocus(mCurrFocusView);
            }
        }
    };

    /**
     * 为 CanRecycleView 设置数据，适配器，布局
     */
    private void setRecyclerViewData() {
        showNetworkRetryView(false);
        //设置 LayoutManager
        mCanRecyclerView.addItemDecoration(new CanRecyclerViewDivider(32));
        CanRecyclerView.CanLinearLayoutManager layoutManager = new CanRecyclerView.CanLinearLayoutManager(SpecialDetailActivity.this, CanRecyclerView.HORIZONTAL, false);
        mCanRecyclerView.setLayoutManager(layoutManager);

        //设置Adapter
        SpecialDetailAdapter adapter = new SpecialDetailAdapter(mRecommdList, this);
        addListener(adapter);
        mCanRecyclerView.setAdapter(adapter);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCanRecyclerView.getChildAt(0).requestFocus();
            }
        }, 500);

    }

    private void requestTopicDetail() {
        mSpecialTopic = HttpManager.getApiService().getSpecialTopic(mTopicId);
        mSpecialTopic.enqueue(new CanCallback<Result<SpecialTopic>>() {
            @Override
            public void onResponse(CanCall<Result<SpecialTopic>> call, Response<Result<SpecialTopic>> response) throws Exception {
                Result<SpecialTopic> info = response.body();
                if (info.getData() == null) {
//                    showNetworkRetryView(true);
                    return;
                }
                ImageLoader.getInstance().load(SpecialDetailActivity.this, mDetailImgBg, info.getData().getBackground());
                mRecommdList = info.getData().getRecommend();
                setRecyclerViewData();
            }

            @Override
            public void onFailure(CanCall<Result<SpecialTopic>> call, CanErrorWrapper errorWrapper) {
                if (mRecommdList != null) {
                    mRecommdList.clear();
                    mRecommdList = null;
                }
                //添加网络重试
                showNetworkRetryView(true);
            }
        });
    }

    private void showNetworkRetryView(boolean isRetry) {
        mCanRecyclerView.setVisibility(isRetry ? View.GONE : View.VISIBLE);
        mDetailImgBg.setVisibility(isRetry ? View.GONE : View.VISIBLE);
        mNetworkLayout.setVisibility(isRetry ? View.VISIBLE : View.GONE);

    }

    /**
     * 添加 Adapter 的监听事件（ItemClick，FocusChange）
     * 添加 RecycleView 的监听事件（Scroll）
     *
     * @param adapter
     */
    private void addListener(SpecialDetailAdapter adapter) {
        adapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                SearchActivity.startAc(SpecialDetailActivity.this);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSpecialTopic!=null){
            mSpecialTopic.cancel();
            mSpecialTopic = null;
        }
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
            mFocusMoveUtil = null;
        }
    }
}
