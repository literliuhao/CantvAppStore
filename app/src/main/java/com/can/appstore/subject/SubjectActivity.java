package com.can.appstore.subject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.subject.adapter.SubjectAdapter;
import com.can.appstore.subject.model.SubjectInfo;

import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

public class SubjectActivity extends BaseActivity implements SubjectContract.SubjectView{


    private static final float FOCUS_SCALE=1.1f;
    public static final int  COLUME_COUNT=4;

    private TextView mRowTv;

    private CanRecyclerView mRecyclerView;
    private SubjectAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    private FocusMoveUtil mFocusMoveUtils;
    private FocusScaleUtil mFocusScaleUtils;

    private SubjectContract.SubjectPresenter mPresenter;

    private Runnable mFocusMoveRunnable;
    private Handler mHandler;
    private View mCurrFocusView;
    private static final String TAG = "SubjectActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        initView();
        setListener();
        initData();
    }

    private void initView() {
        CanRecyclerViewDivider itemDecoration=new CanRecyclerViewDivider(Color.TRANSPARENT,
                getResources().getDimensionPixelSize(R.dimen.px24),
                getResources().getDimensionPixelSize(R.dimen.px40));

        mRowTv= (TextView) findViewById(R.id.subject_row_tv);

        mLayoutManager=new GridLayoutManager(this,COLUME_COUNT);
        mRecyclerView= (CanRecyclerView) findViewById(R.id.subject_recyclerview);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFocusMoveRunnable=new Runnable() {
            @Override
            public void run() {
                mFocusMoveUtils.startMoveFocus(mCurrFocusView,FOCUS_SCALE);
                mFocusScaleUtils.scaleToLarge(mCurrFocusView);
            }
        };
        mHandler=new Handler();
    }

    private void setListener() {

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(RecyclerView.SCROLL_STATE_IDLE==newState){
                    ImageLoader.getInstance().resumeTask(SubjectActivity.this);
                    int lastPos= mLayoutManager.findLastVisibleItemPosition();
                    mPresenter.loadMore(lastPos);
                }else{
                    ImageLoader.getInstance().pauseTask(SubjectActivity.this);
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mHandler.removeCallbacks(mFocusMoveRunnable);
                mHandler.postDelayed(mFocusMoveRunnable, 30);
            }
        });
    }

    private void initData() {
        mFocusMoveUtils=new FocusMoveUtil(this,getWindow().getDecorView().findViewById(android.R.id.content),R.mipmap.image_focus);
        mFocusScaleUtils=new FocusScaleUtil();
        mFocusScaleUtils.setFocusScale(1.1f);
        SubjectContract.SubjectPresenter presenter=new SubjectPresenterImpl(this);
        presenter.startLoad();
    }

    @Override
    public void refreshData(List<SubjectInfo> datas) {
      if(mAdapter==null){
          mAdapter=new SubjectAdapter(datas,this);
          mRecyclerView.setAdapter(mAdapter);
          mAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
              @Override
              public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                  if(hasFocus){
                      view.setSelected(true);
                      mCurrFocusView=view;
                      mHandler.removeCallbacks(mFocusMoveRunnable);
                      mHandler.postDelayed(mFocusMoveRunnable,30);
                      mPresenter.onItemFocused(position);
                  }else{
                      mFocusScaleUtils.scaleToNormal();
                      view.setSelected(false);
                  }
              }
          });
      }else{
          mAdapter.notifyDataSetChanged();
      }
    }

    @Override
    public void refreshRowNum(String formatRow) {
        mRowTv.setText(formatRow);
    }

    @Override
    protected void onDestroy() {
        mPresenter=null;
        super.onDestroy();
    }

    @Override
    public void setPresenter(SubjectContract.SubjectPresenter presenter) {
        mPresenter=presenter;
    }
    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }


}
