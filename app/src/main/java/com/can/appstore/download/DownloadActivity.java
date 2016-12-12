package com.can.appstore.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.appdetail.custom.TextProgressBar;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.download.adapter.DownloadAdapter;
import com.dataeye.sdk.api.app.DCEvent;
import com.dataeye.sdk.api.app.channel.DCPage;

import java.util.List;

import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

public class DownloadActivity extends BaseActivity implements DownloadContract.DownloadView {

    private static final String TAG = "DownloadActivity";
    public static final int DELAY_MILLIS_MOVE_FOCUS = 50;
    public static final int DELAY_MILLIS_RESOLVE_FOCUS = 500;
    public static final int DELAY_MILLIS_REFRESH_STORAGE = 5000; //刷新可用空间进度条时间间隔
    public static final int MSG_REFRESH_STORAGE = 0x1;//刷新可用空间进度条


    private TextView mRowTv, mNoDataTv, mPauseAllBtn, mDeleteAllBtn;
    private CanRecyclerView mCanRecyclerView;
    private TextProgressBar mStorageProgressBar;

    private DownloadContract.DownloadPresenter mPresenter;
    private DownloadAdapter mAdapter;
    private CanRecyclerView.CanLinearLayoutManager mLayoutManager;

    private FocusMoveUtil mFocusMoveUtil;
    private Runnable mFocusMoveRunnable, mFocusResolveRunnable;
    private Handler hanlder;
    private View mFocusView;

    private String pauseAllTaskString;
    private String resumeAllTaskString;

    private int lastFocusPos = 0;
    private boolean focusMoveEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
        initHandler();
        initData();
        setListener();
    }

    private void initView() {
        CanRecyclerViewDivider itemDecoration = new CanRecyclerViewDivider(40);

        mLayoutManager = new CanRecyclerView.CanLinearLayoutManager(this);

        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView().findViewById(android.R.id.content),
                R.mipmap.btn_focus);
        mStorageProgressBar = (TextProgressBar) findViewById(R.id.download_storage_progress);
        mStorageProgressBar.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_18px));

        mNoDataTv = (TextView) findViewById(R.id.download_no_data_tv);
        mRowTv = (TextView) findViewById(R.id.download_row_tv);

        mPauseAllBtn = (TextView) findViewById(R.id.download_pause_all_btn);
        mDeleteAllBtn = (TextView) findViewById(R.id.download_delete_all_btn);

        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.download_recyclerview);
        mCanRecyclerView.addItemDecoration(itemDecoration);
        mCanRecyclerView.setLayoutManager(mLayoutManager);

        resolveFirstFocus();
    }

    private void initHandler() {
        hanlder = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_REFRESH_STORAGE:
                        if (mPresenter != null) {
                            mPresenter.caculateStorage();
                        }
                        sendEmptyMessageDelayed(MSG_REFRESH_STORAGE, DELAY_MILLIS_REFRESH_STORAGE);
                        break;
                }
            }
        };

        mFocusMoveRunnable = new Runnable() {
            @Override
            public void run() {
                mFocusMoveUtil.startMoveFocus(mFocusView);
            }
        };

        mFocusResolveRunnable = new Runnable() {
            @Override
            public void run() {
                focusItemContentView();
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
        hanlder.sendEmptyMessage(MSG_REFRESH_STORAGE);
    }

    @Override
    public void showStorageView(int progress, String storage) {
        mStorageProgressBar.setProgress(progress);
        mStorageProgressBar.setText(storage);
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
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    ImageLoader.getInstance().resumeTask(DownloadActivity.this);
                } else {
                    ImageLoader.getInstance().pauseTask(DownloadActivity.this);
                }
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
                        focusMoveDelay(DELAY_MILLIS_RESOLVE_FOCUS - 10);
                    } else {
                        focusMoveDelay();
                    }
                    mPresenter.calculateRowNum(pos);
                    lastFocusPos = pos;
                }
            }

            @Override
            public void onItemContentClick(View view, int pos, DownloadTask downloadTask) {
                // TODO: 2016/11/25  进入详情页
                Log.i(TAG, "onItemContentClick: downloadTAsk=" + downloadTask.toString());
            }

            @Override
            public void onControlButtonClick(View view, int pos, DownloadTask downloadTask) {

            }

            @Override
            public void onDeleteButtonClick(View view, int pos, DownloadTask downloadTask) {
                if (tasks.size() <= 1) {
                    showNoDataView();
                    focusMoveEnable = true;
                } else {
                    if (pos != 0 && pos != mLayoutManager.findFirstVisibleItemPosition()) {
                        mFocusMoveUtil.hideFocusForShowDelay(DELAY_MILLIS_RESOLVE_FOCUS);
                        if (pos == tasks.size() - 1) {
                            focusMoveEnable = false;
                            hanlder.postDelayed(mFocusResolveRunnable, DELAY_MILLIS_RESOLVE_FOCUS - 10);
                        }
                    }
                }
                mAdapter.notifyItemRemoved(pos);
                tasks.remove(downloadTask);
                int itemPos = pos == 0 ? 0 : pos - 1;
                mPresenter.calculateRowNum(itemPos);
            }

            @Override
            public boolean onItemContentKeyListener(View view, int keyCode, KeyEvent event, int pos, DownloadTask downloadTask) {
                if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
                    mPauseAllBtn.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void focusItemContentView() {
        focusMoveEnable = true;
        DownloadAdapter.DownloadViewHolder holder = (DownloadAdapter.DownloadViewHolder) mCanRecyclerView.findViewHolderForAdapterPosition(mAdapter.getItemCount() - 1);
        if (holder != null) {
            holder.appContentLayout.requestFocus();
        }
    }

    @Override
    public void setPresenter(DownloadContract.DownloadPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshRowNumber(CharSequence formatRow) {
        mRowTv.setText(formatRow);
    }

    private void focusMoveDelay() {
        if (focusMoveEnable) {
            focusMoveDelay(DELAY_MILLIS_MOVE_FOCUS);
        }
    }

    private void focusMoveDelay(int delayMillis) {
        if (mFocusMoveUtil != null) {
            hanlder.removeCallbacks(mFocusMoveRunnable);
            hanlder.postDelayed(mFocusMoveRunnable, delayMillis);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DCPage.onEntry(AppConstants.DOWNLOAD_MANAGE);
        DCEvent.onEvent(AppConstants.DOWNLOAD_MANAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DCPage.onExit(AppConstants.DOWNLOAD_MANAGE);
        DCEvent.onEventDuration(AppConstants.DOWNLOAD_MANAGE, mDuration);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hanlder.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFocusMoveUtil.release();
        mPresenter.release();
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

    public void resolveFirstFocus() {
        mPauseAllBtn.post(new Runnable() {
            @Override
            public void run() {
                hanlder.removeCallbacks(mFocusMoveRunnable);
                mFocusMoveUtil.setFocusView(mPauseAllBtn);
            }
        });
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DownloadActivity.class);
        context.startActivity(intent);
    }

/*    @Override
    protected void onHomeKeyDown() {
        this.finish();
    }*/
}
