package com.can.appstore.download;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.download.adapter.DownloadAdapter;

import java.util.List;

import cn.can.downloadlib.DownloadTask;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

public class DownloadActivity extends BaseActivity implements DownloadContract.DownloadView {

    private TextView mRowTv, mNoDataTv;
    private Button mPauseAllBtn,mDeleteAllBtn;
    private CanRecyclerView mCanRecyclerView;

    private DownloadContract.DownloadPresenter mPresenter;
    private DownloadAdapter mAdapter;
    private CanRecyclerView.CanLinearLayoutManager mLayoutManager;

    private FocusMoveUtil mFocusMoveUtil;
    private Runnable mFocusMoveRunnable;
    private Handler hanlder;
    private View mFocusView;

    private String pauseAllTaskString;
    private String resumeAllTaskString;

    private static final String TAG = "DownloadActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
        initData();
        initHandler();
        setListener();
    }
    private void initHandler() {
        hanlder=new Handler();
        mFocusMoveRunnable=new Runnable() {
            @Override
            public void run() {
                mFocusMoveUtil.startMoveFocus(mFocusView);
            }
        };
    }

    private void setListener() {
        mPauseAllBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mFocusView=v;
                    focusMoveDelay();
                }
            }
        });
        mPauseAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(pauseAllTaskString.equals(mPauseAllBtn.getText())){
                  mPresenter.pauseAllTasks();
                  mPauseAllBtn.setText(resumeAllTaskString);
              }else{
                  mPresenter.resumeAllTasks();
                  mPauseAllBtn.setText(pauseAllTaskString);
              }
            }
        });
        mDeleteAllBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mFocusView=v;
                    focusMoveDelay();
                }
            }
        });
        mDeleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteAllTasks();
            }
        });

    }
    private void initData() {
        pauseAllTaskString=getString(R.string.download_pause_all);
        resumeAllTaskString=getString(R.string.download_resume_all);
        DownloadContract.DownloadPresenter downloadPresenter=new DownloadPresenterImpl(this);
        downloadPresenter.loadData();
    }

    private void initView() {
        CanRecyclerViewDivider itemDecoration=new CanRecyclerViewDivider(40);
        mFocusMoveUtil=new FocusMoveUtil(this,getWindow().getDecorView().findViewById(android.R.id.content),
                R.mipmap.image_focus);
        mNoDataTv= (TextView) findViewById(R.id.download_no_data_tv);
        mRowTv= (TextView) findViewById(R.id.download_row_tv);
        mPauseAllBtn= (Button) findViewById(R.id.download_pause_all_btn);
        mDeleteAllBtn= (Button) findViewById(R.id.download_delete_all_btn);
        mCanRecyclerView= (CanRecyclerView) findViewById(R.id.download_recyclerview);
        mCanRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager=new CanRecyclerView.CanLinearLayoutManager(this);
        mCanRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onDataLoaded(List<DownloadTask> tasks) {
        //FIXME 删除 只是为了增加条目，测试Item多的情况 start
        //tasks.addAll(tasks);
        //FIXME 删除 只是为了增加条目，测试Item多的情况 end
        if(mAdapter==null){
            mPresenter.onItemFocused(0);
            mAdapter=new DownloadAdapter(tasks);
            mCanRecyclerView.setAdapter(mAdapter);
            mCanRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    focusMoveDelay();
                }
            });
            mAdapter.setOnItemEventListener(new DownloadAdapter.OnItemEventListener() {
                @Override
                public void onItemContentFocusChanged(View view, boolean hasFocus, int pos) {
                    if(hasFocus){
                        mFocusView=view;
                        focusMoveDelay();
                        mPresenter.onItemFocused(pos);
                    }
                }

                @Override
                public void onItemControlButtonFocusChanged(View view, boolean hasFocus, int pos, DownloadTask downloadTask) {
                    if(hasFocus){
                        mFocusView=view;
                        focusMoveDelay();
                    }
                }

                @Override
                public void onItemDeleteButtonFocusChanged(View view, boolean hasFocus, int pos, DownloadTask downloadTask) {
                    if(hasFocus){
                        mFocusView=view;
                        focusMoveDelay();
                    }
                }

                @Override
                public void onItemContentClick(View view, int pos, DownloadTask downloadTask) {
                    }
                    @Override
                    public void onControlButtonClick(View view, int pos, DownloadTask downloadTask) {

                    }

                    @Override
                    public void onDeleteButtonClick(View view, int pos, DownloadTask downloadTask) {

                    }
                });
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setPresenter(DownloadContract.DownloadPresenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void refreshRowNumber(String formatRow) {
        mRowTv.setText(formatRow);
    }
    private void focusMoveDelay(){
        if(mFocusMoveUtil!=null){
            hanlder.removeCallbacks(mFocusMoveRunnable);
            hanlder.postDelayed(mFocusMoveRunnable,30);
        }
    }
    @Override
    protected void onDestroy() {
        hanlder.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void showNoDataView() {
        if(mNoDataTv!=null){
            mNoDataTv.setVisibility(View.VISIBLE);
        }
        if(mCanRecyclerView!=null){
            mCanRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideNoDataView() {
        if(mNoDataTv!=null){
            mNoDataTv.setVisibility(View.GONE);
        }
        if(mCanRecyclerView!=null){
            mCanRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
