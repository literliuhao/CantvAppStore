package com.can.appstore.download;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.download.adapter.DownloadAdapter;

import java.util.List;

import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

public class DownloadActivity extends BaseActivity implements DownloadContract.DownloadView {

    private static final String TAG = "DownloadActivity";
    public static final int DELAY_MILLIS = 30;

    private TextView mRowTv, mNoDataTv, mPauseAllBtn, mDeleteAllBtn;
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

    private int lastFocusPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
        initData();
        initHandler();
        setListener();
    }

    private void initView() {
        CanRecyclerViewDivider itemDecoration = new CanRecyclerViewDivider(40);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView().findViewById(android.R.id.content),
                R.mipmap.btn_focus);
        mNoDataTv = (TextView) findViewById(R.id.download_no_data_tv);
        mRowTv = (TextView) findViewById(R.id.download_row_tv);
        mPauseAllBtn = (TextView) findViewById(R.id.download_pause_all_btn);
        mDeleteAllBtn = (TextView) findViewById(R.id.download_delete_all_btn);
        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.download_recyclerview);
        mCanRecyclerView.addItemDecoration(itemDecoration);

        mLayoutManager = new CanRecyclerView.CanLinearLayoutManager(this);
        mCanRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void initHandler() {
        hanlder = new Handler();
        mFocusMoveRunnable = new Runnable() {
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
                if (hasFocus) {
                    mFocusView = v;
                    focusMoveDelay();
                }
            }
        });
        mPauseAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pauseAllTaskString.equals(mPauseAllBtn.getText())) {
                    if (mPresenter.pauseAllTasks()) {
                        mPauseAllBtn.setText(resumeAllTaskString);
                    }
                } else {
                    if (mPresenter.resumeAllTasks()) {
                        mPauseAllBtn.setText(pauseAllTaskString);
                    }
                }
            }
        });
        mDeleteAllBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFocusView = v;
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
        pauseAllTaskString = getString(R.string.download_pause_all);
        resumeAllTaskString = getString(R.string.download_resume_all);
        DownloadContract.DownloadPresenter downloadPresenter = new DownloadPresenterImpl(this);
        downloadPresenter.loadData();
    }


    @Override
    public void onDataLoaded(final List<DownloadTask> tasks) {
        refreshControlAllBtn(tasks);
        mPresenter.calculateRowNum(0);
        mAdapter = new DownloadAdapter(tasks);
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
                if (hasFocus) {
                    mFocusView = view;
                    focusMoveDelay();
                    mPresenter.calculateRowNum(pos);
                    lastFocusPos = -1;
                }
            }

            @Override
            public void onItemControlButtonFocusChanged(View view, boolean hasFocus, int pos, DownloadTask downloadTask) {
                if (hasFocus) {
                    mFocusView = view;
                    focusMoveDelay();
                    mPresenter.calculateRowNum(pos);
                    lastFocusPos = -1;
                }

            }

            @Override
            public void onItemDeleteButtonFocusChanged(View view, boolean hasFocus, int pos, DownloadTask downloadTask) {
                if (hasFocus) {
                    mFocusView = view;
                    if (lastFocusPos == pos) {
                        focusMoveDelay(500);
                    } else {
                        focusMoveDelay();
                    }
                    mPresenter.calculateRowNum(pos);
                    lastFocusPos = pos;
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
                if (tasks.size() == 0) {
                    showNoDataView();
                } else {
                    mPresenter.calculateRowNum(pos);
                }
            }

            @Override
            public boolean onItemContentKeyListener(View view, int keyCode, KeyEvent event, int pos, DownloadTask downloadTask) {
                return false;
            }
        });
    }

    @Override
    public void setPresenter(DownloadContract.DownloadPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshRowNumber(String formatRow) {
        mRowTv.setText(formatRow);
    }

    private void focusMoveDelay() {
        focusMoveDelay(DELAY_MILLIS);
    }

    private void focusMoveDelay(int delayMillis) {
        if (mFocusMoveUtil != null) {
            hanlder.removeCallbacks(mFocusMoveRunnable);
            hanlder.postDelayed(mFocusMoveRunnable, delayMillis);
        }
    }

    @Override
    protected void onDestroy() {
        hanlder.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void showNoDataView() {
        if (mNoDataTv != null) {
            mNoDataTv.setVisibility(View.VISIBLE);
        }
        if (mRowTv != null) {
            mRowTv.setText("");
        }
        if (mCanRecyclerView != null) {
            mCanRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void hideNoDataView() {
        if (mNoDataTv != null) {
            mNoDataTv.setVisibility(View.INVISIBLE);
        }
        if (mCanRecyclerView != null) {
            mCanRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    //只有有一个未完成的不是pause状态，则按钮为全部暂停
    private void refreshControlAllBtn(List<DownloadTask> list) {
        String text = pauseAllTaskString;
        for (DownloadTask task : list) {
            if (DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING == task.getDownloadStatus()
                    || DownloadStatus.DOWNLOAD_STATUS_INIT == task.getDownloadStatus()
                    || DownloadStatus.DOWNLOAD_STATUS_PREPARE == task.getDownloadStatus()
                    || DownloadStatus.DOWNLOAD_STATUS_START == task.getDownloadStatus()) {
                text = pauseAllTaskString;
                break;
            } else {
                text = resumeAllTaskString;
            }
        }
        mPauseAllBtn.setText(text);
    }
}
