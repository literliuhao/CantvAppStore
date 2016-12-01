package com.can.appstore.specialdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

/**
 * Created by Fuwen on 2016/11/s2.
 * 专题详情页
 */
public class SpecialDetailActivity extends BaseActivity {
    public static final String EXTRA_TOPIC_ID = "topicId";
    private static final int FOCUS_IMAGE = R.mipmap.image_focus;
    private CanRecyclerView mCanRecyclerView;
    private CanCall<Result<SpecialTopic>> mSpecialTopic;
    private ImageView mDetailImgBg;
    private View mCurrFocusView;
    private FocusMoveUtil mFocusMoveUtil;
    private Handler mHandler = new Handler();
    private List<AppInfo> mRecommdList;
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

        requestData();
    }

    private void getTopicId() {
        Intent intent = getIntent();
        if (intent != null) {
            mTopicId = intent.getStringExtra(EXTRA_TOPIC_ID);
        }
        mTopicId = TextUtils.isEmpty(mTopicId) ? "14" : mTopicId;
    }

    private void initView() {
        mDetailImgBg = (ImageView) findViewById(R.id.special_detail_img);
        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.special_detail_crview);
        mCanRecyclerView.addItemDecoration(new CanRecyclerViewDivider(32));
        CanRecyclerView.CanLinearLayoutManager layoutManager = new CanRecyclerView.CanLinearLayoutManager(SpecialDetailActivity.this, CanRecyclerView.HORIZONTAL, false);
        mCanRecyclerView.setLayoutManager(layoutManager);

        addViewListener();
    }

    private void requestData() {
        if (!NetworkUtils.isNetworkConnected(SpecialDetailActivity.this)) {
            loadDataFail(R.string.no_network);
            return;
        }
        mSpecialTopic = HttpManager.getApiService().getSpecialTopic(mTopicId);
        mSpecialTopic.enqueue(new CanCallback<Result<SpecialTopic>>() {
            @Override
            public void onResponse(CanCall<Result<SpecialTopic>> call, Response<Result<SpecialTopic>> response) throws Exception {
                Result<SpecialTopic> info = response.body();
                Log.d("SpecialDetailActivity", info.toString());
                if (info.getData() == null) {
                    loadDataFail(R.string.load_data_faild);
                    return;
                }
                ImageLoader.getInstance().load(SpecialDetailActivity.this, mDetailImgBg, info.getData().getBackground());
                mRecommdList.addAll(info.getData().getRecommend());
                showRecycleView();
            }

            @Override
            public void onFailure(CanCall<Result<SpecialTopic>> call, CanErrorWrapper errorWrapper) {
                loadDataFail(R.string.load_data_faild);
            }
        });
    }

    private void loadDataFail(int toastId) {
        if(toastId != R.string.no_network){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SpecialDetailActivity.this.finish();
                }
            },500);
        }else{
            SpecialDetailActivity.this.finish();
        }
        showToast(toastId);

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

    @Override
    protected void onHomeKeyDown() {
        finish();
        super.onHomeKeyDown();
    }
}
