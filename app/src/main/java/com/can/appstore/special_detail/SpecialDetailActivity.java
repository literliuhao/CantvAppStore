package com.can.appstore.special_detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.can.appstore.R;
import com.can.appstore.search.SearchActivity;
import com.can.appstore.special_detail.adapter.SpecialDetailAdapter;
import com.can.appstore.special_detail.bean.AppDetail;
import com.can.appstore.special_detail.bean.AppDetailUtils;

import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

public class SpecialDetailActivity extends Activity {
    private CanRecyclerView mCanRecyclerView;
    private View mCurrFocusView;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mFocusScaleUtil;
    private Handler mHandler = new Handler();

    public static void startAc(Context context) {
        Intent intent = new Intent(context, SpecialDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_detail);
        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.special_detail_crview);

        setRecyclerViewData();
    }


    private Runnable mfocusMoveRunnable = new Runnable() {
        @Override
        public void run() {
            if(mCurrFocusView != null && mCurrFocusView.isFocused()){
                mFocusMoveUtil.startMoveFocus(mCurrFocusView,1.1f);
                mFocusScaleUtil.scaleToLarge(mCurrFocusView);
            }
        }
    };

    /**
     * 为 CanRecycleView 设置数据，适配器，布局
     */
    private void setRecyclerViewData() {
        //获取数据，待删除
        List<AppDetail> appDetails = AppDetailUtils.getAppData();
        if (appDetails == null || appDetails.size() == 0) {
            return;
        }

        //设置Adapter
        SpecialDetailAdapter adapter = new SpecialDetailAdapter(appDetails, this);
        mCanRecyclerView.setAdapter(adapter);

        //焦点工具初始化
        mFocusMoveUtil = new FocusMoveUtil(SpecialDetailActivity.this, getWindow().getDecorView(), R.mipmap.image_focus);
        mFocusScaleUtil = new FocusScaleUtil();

        addListener(adapter);

        //设置 LayoutManager
        mCanRecyclerView.addItemDecoration(new CanRecyclerViewDivider(32));
        CanRecyclerView.CanLinearLayoutManager layoutManager = new CanRecyclerView.CanLinearLayoutManager(SpecialDetailActivity.this, CanRecyclerView.HORIZONTAL, false);
        mCanRecyclerView.setLayoutManager(layoutManager);

    }

    /**
     * 添加 Adapter 的监听事件（ItemClick，FocusChange）
     * 添加 RecycleView 的监听事件（Scroll）
     * @param adapter
     */
    private void addListener(SpecialDetailAdapter adapter){
        adapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                SearchActivity.startAc(SpecialDetailActivity.this);
            }
        });
        adapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if(hasFocus){
                    mCurrFocusView = view;
                    mHandler.removeCallbacks(mfocusMoveRunnable);
                    mHandler.postDelayed(mfocusMoveRunnable, 50);
                }else{
                    mFocusScaleUtil.scaleToNormal(view);
                }
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
    protected void onStop() {
        super.onStop();
    }
}
