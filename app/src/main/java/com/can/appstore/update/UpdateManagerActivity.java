package com.can.appstore.update;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.appdetail.custom.TextProgressBar;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.model.UpdateApkInstallModel;
import com.dataeye.sdk.api.app.DCEvent;
import com.dataeye.sdk.api.app.channel.DCPage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.downloadlib.NetworkUtils;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * 更新管理
 * Created by shenpx on 2016/10/12 0012.
 */

public class UpdateManagerActivity extends BaseActivity implements UpdateContract.View {

    private static final String TAG = "updateManagerActivity";
    private CanRecyclerView mRecyclerView;
    private UpdateManagerAdapter mRecyclerAdapter;
    private TextView mDetectionButton;
    private TextView mReminder;
    private TextView mTotalnum;
    private TextView mCurrentnum;
    private TextProgressBar mSizeProgressBar;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mFocusScaleUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;
    private UpdatePresenter mPresenter;
    private List<AppInfoBean> mUpdateList;
    private cn.can.downloadlib.DownloadManager mDownloadManager;
    private int mWinH;
    private int mWinW;
    private Context mContext;
    private long mLastClickTime;
    private long moveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatemanager);
        mContext = this;
        EventBus.getDefault().register(this);
        mPresenter = new UpdatePresenter(this, UpdateManagerActivity.this);
        //获取到屏幕的宽高
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWinH = outMetrics.heightPixels;
        mWinW = outMetrics.widthPixels;
        initView();
        initData();
        initFocusChange();
        initClick();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DCPage.onEntry(AppConstants.UPDATE_MANAGE);
        DCEvent.onEvent(AppConstants.UPDATE_MANAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DCPage.onExit(AppConstants.UPDATE_MANAGE);
        DCEvent.onEventDuration(AppConstants.UPDATE_MANAGE, mDuration);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.release();
        if(mDownloadManager!=null){
            mDownloadManager.removeAllAppInstallListener();
            for (AppInfoBean appInfo: mUpdateList ) {
                DownloadTask downloadTask=mDownloadManager.getCurrentTaskById(MD5.MD5(appInfo.getDownloadUrl()));
                if(downloadTask!=null){
                    downloadTask.removeAllDownloadListener();
                }
            }
        }
    }

    private void initFocusChange() {

        mDetectionButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mFocusMoveUtil.startMoveFocus(mDetectionButton, 1.0f);
                    boolean isNull = mPresenter.isNull(0);
                    if (isNull) {
                        mCurrentnum.setVisibility(View.INVISIBLE);
                        mTotalnum.setVisibility(View.INVISIBLE);
                    } else {
                        mPresenter.setNum(0);
                    }
                } else {
                    mFocusScaleUtil.scaleToNormal(mDetectionButton);
                }
            }
        });

        mRecyclerAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mRecyclerView.postDelayed(myFocusRunnable, 50);
                    mPresenter.setNum(position);

                } else {
                    mFocusScaleUtil.scaleToNormal();
                }
                view.setSelected(hasFocus);
            }

        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == CanRecyclerView.SCROLL_STATE_SETTLING) {
                    //限制移动区域
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            int[] posi = new int[2];
                            mRecyclerView.getLocationInWindow(posi);
                            mFocusMoveUtil.setFocusActiveRegion(posi[0], posi[1] + mRecyclerView.getPaddingTop(),
                                    posi[0] + mRecyclerView.getWidth(),
                                    posi[1] + mRecyclerView.getHeight() - mRecyclerView.getPaddingBottom() - getResources().getDimensionPixelSize(R.dimen.px40));
                        }
                    });
                    setLeftLayoutFocus(false);
                } else if (newState == CanRecyclerView.SCROLL_STATE_IDLE) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mFocusMoveUtil.setFocusActiveRegion(0, 0, mWinW, mWinH);
                        }
                    });
                    setLeftLayoutFocus(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                myFocusRunnable.run();
            }
        });
    }

    /**
     * 设置左侧焦点
     *
     * @param focusable
     */
    private void setLeftLayoutFocus(boolean focusable) {
        mDetectionButton.setFocusable(focusable);
    }

    private void initClick() {

        mDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFastContinueClickView()) {
                    return;
                }
                mPresenter.getInstallPkgList();
                if (!NetworkUtils.isNetworkConnected(UpdateManagerActivity.this)) {
                    mCurrentnum.setVisibility(View.INVISIBLE);
                    mTotalnum.setVisibility(View.INVISIBLE);
                }

            }
        });


        mRecyclerAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onClick(View view, final int position, Object data) {
                if (!NetworkUtils.isNetworkConnected(UpdateManagerActivity.this)) {
                    showToast(getResources().getString(R.string.no_network));
                    return;
                }
                if (mUpdateList.get(position).getUpdated()) {
                    showToast(R.string.update_already_updated);
                    return;
                }
                String downloadUrl = mUpdateList.get(position).getDownloadUrl();

                final ProgressBar progress = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);
                final TextView status = (TextView) view.findViewById(R.id.tv_updateapp_downloading);
                final ImageView updatedIcon = (ImageView) view.findViewById(R.id.iv_updateapp_updatedicon);
                if (mDownloadManager == null) {
                    mDownloadManager = cn.can.downloadlib.DownloadManager.getInstance(UpdateManagerActivity.this);
                }
                DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
                if (downloadTask != null) {
                    int taskstatus = downloadTask.getDownloadStatus();
                    final String saveDirPath = downloadTask.getSaveDirPath();
                    if (taskstatus == AppInstallListener.APP_INSTALL_FAIL) {
                        status.setText(getResources().getString(R.string.update_download_installing));
                        status.setVisibility(View.VISIBLE);
                        installUpdateApk(progress, status, updatedIcon, saveDirPath, position, downloadTask.getUrl());
                    }
                } else {
                    downloadTask = new DownloadTask();
                    String md5 = MD5.MD5(downloadUrl);
                    downloadTask.setFileName(mUpdateList.get(position).getAppName());
                    downloadTask.setPkg(mUpdateList.get(position).getPackageName());
                    downloadTask.setId(md5);
                    downloadTask.setUrl(downloadUrl);
                    downloadTask.setIcon(mUpdateList.get(position).getIconUrl());
                    status.setText(getResources().getString(R.string.update_download_waitting));
                    status.setVisibility(View.VISIBLE);
                    mDownloadManager.addDownloadTask(downloadTask, new DownloadTaskListener() {
                        @Override
                        public void onPrepare(DownloadTask downloadTask) {
                            Log.d(TAG, "onPrepare: ");
                            refreshStatus(downloadTask, status, progress, updatedIcon);
                        }

                        @Override
                        public void onStart(DownloadTask downloadTask) {
                            Log.d(TAG, "onStart: ");
                            refreshStatus(downloadTask, status, progress, updatedIcon);
                        }

                        @Override
                        public void onDownloading(final DownloadTask downloadTask) {
                            Log.d(TAG, "onDownloading: ");
                            refreshStatus(downloadTask, status, progress, updatedIcon);
                        }

                        @Override
                        public void onPause(DownloadTask downloadTask) {
                            Log.d(TAG, "onPause: ");
                            refreshStatus(downloadTask, status, progress, updatedIcon);
                        }

                        @Override
                        public void onCancel(DownloadTask downloadTask) {
                            Log.d(TAG, "onCancel: ");
                        }

                        @Override
                        public void onCompleted(final DownloadTask downloadTask) {
                            Log.d(TAG, "onCompleted: ");
                            progress.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.VISIBLE);
                                    status.setText(getResources().getString(R.string.update_download_installing));
                                    progress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onError(DownloadTask downloadTask, int errorCode) {
                            Log.d(TAG, "onError: ");
                            status.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.VISIBLE);
                                    status.setText(getResources().getString(R.string.update_download_false));
                                    progress.setVisibility(View.INVISIBLE);
                                }
                            });
                            switch (errorCode) {
                                case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                                    showToast(getResources().getString(R.string.downlaod_error));
                                    break;
                                case DOWNLOAD_ERROR_IO_ERROR:
                                    showToast(getResources().getString(R.string.downlaod_error));
                                    break;
                                case DOWNLOAD_ERROR_NETWORK_ERROR:
                                    showToast(getResources().getString(R.string.network_connection_error));
                                    break;
                                case DOWNLOAD_ERROR_UNKONW_ERROR:
                                    showToast(getResources().getString(R.string.unkonw_error));
                                    break;
                                case DOWNLOAD_ERROR_NO_SPACE_ERROR:
                                    showToast(R.string.space_inequacy);
                                    break;
                                default:
                                    break;
                            }
                            mDownloadManager.cancel(downloadTask);
                        }
                    });
                    mDownloadManager.setAppInstallListener(new AppInstallListener() {
                        @Override
                        public void onInstalling(DownloadTask downloadTask) {
                            String url = downloadTask.getUrl();
                            //获取position
                            int itemPosition = mPresenter.getItemPosition(url);
                            View childAt = mRecyclerView.getChildAt(itemPosition);
                            final TextView status = (TextView) childAt.findViewById(R.id.tv_updateapp_downloading);
                            final ProgressBar progressBar = (ProgressBar) childAt.findViewById(R.id.pb_updateapp_progressbar);
                            status.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText(getResources().getString(R.string.update_download_installing));
                                    status.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onInstallSucess(String id) {
                            int itemPosition = mPresenter.getItemPosition(id);
                            mUpdateList.get(itemPosition).setUpdated(true);
                            View childAt = mRecyclerView.getChildAt(itemPosition);
                            final TextView status = (TextView) childAt.findViewById(R.id.tv_updateapp_downloading);
                            final ImageView updatedicon = (ImageView) childAt.findViewById(R.id.iv_updateapp_updatedicon);
                            status.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.INVISIBLE);
                                    updatedicon.setVisibility(View.VISIBLE);
                                    mPresenter.getUpdateApkNum(position);
                                }
                            });
                        }

                        @Override
                        public void onInstallFail(String id) {
                            int itemPosition = mPresenter.getItemPosition(id);
                            View childAt = mRecyclerView.getChildAt(itemPosition);
                            final TextView status = (TextView) childAt.findViewById(R.id.tv_updateapp_downloading);
                            final ImageView updatedicon = (ImageView) childAt.findViewById(R.id.iv_updateapp_updatedicon);
                            status.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.VISIBLE);
                                    status.setText(getResources().getString(R.string.update_download_installfalse));
                                    updatedicon.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onUninstallSucess(String id) {

                        }

                        @Override
                        public void onUninstallFail(String id) {

                        }
                    });
                }

            }
        });

    }

    /**
     * 安装状态刷新
     *
     * @param progress
     * @param status
     * @param updatedIcon
     * @param saveDirPath
     */
    private void installUpdateApk(ProgressBar progress, final TextView status, final ImageView updatedIcon, final String saveDirPath, final int position, final String url) {
        status.setVisibility(View.VISIBLE);
        status.setText(getResources().getString(R.string.update_download_installing));
        progress.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPresenter.installApp(saveDirPath, position, url);
            }
        }).start();
    }

    /**
     * 下载监听刷新
     *
     * @param downloadTask
     * @param status
     * @param progress
     * @param updateicon
     */
    private void refreshStatus(final DownloadTask downloadTask, final TextView status, final ProgressBar progress, final ImageView updateicon) {
        progress.post(new Runnable() {
            @Override
            public void run() {
                refreshDownliadStatus(downloadTask.getDownloadStatus(), status);
                if (downloadTask.getCompletedSize() > 0
                        && downloadTask.getCompletedSize() < downloadTask.getTotalSize()
                        && downloadTask.getDownloadStatus() != DownloadStatus.DOWNLOAD_STATUS_ERROR
                        && downloadTask.getDownloadStatus() != DownloadStatus.DOWNLOAD_STATUS_SPACE_NOT_ENOUGH) {
                    progress.setVisibility(View.VISIBLE);
                } else {
                    progress.setVisibility(View.INVISIBLE);
                }
                progress.setProgress((int) downloadTask.getPercent());
                updateicon.setVisibility(View.INVISIBLE);
            }
        });

    }


    public void refreshDownliadStatus(int downloadStatus, TextView status) {
        switch (downloadStatus) {
            case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                status.setVisibility(View.VISIBLE);
                status.setText(getResources().getString(R.string.update_downloading));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_INIT:
            case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
            case DownloadStatus.DOWNLOAD_STATUS_START:
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                status.setVisibility(View.VISIBLE);
                status.setText(getResources().getString(R.string.update_download_waitting));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
            case AppInstallListener.APP_INSTALLING:
                status.setVisibility(View.VISIBLE);
                status.setText(getResources().getString(R.string.update_download_installing));
                break;
            case AppInstallListener.APP_INSTALL_FAIL:
                status.setVisibility(View.VISIBLE);
                status.setText(getResources().getString(R.string.update_download_installfalse));
                break;
            case AppInstallListener.APP_INSTALL_SUCESS:
                status.setVisibility(View.INVISIBLE);
                break;
            default:
                status.setVisibility(View.INVISIBLE);
                break;
        }
    }

    protected void initData() {
        mPresenter.getSDInfo();
        mPresenter.getInstallPkgList();
        mPresenter.setNum(0);
        mUpdateList = mPresenter.getList();
    }

    private void initView() {
        mTotalnum = (TextView) findViewById(R.id.tv_update_totalnum);
        mCurrentnum = (TextView) findViewById(R.id.tv_update_currentnum);
        mDetectionButton = (TextView) findViewById(R.id.bt_update_detection);
        mRecyclerView = (CanRecyclerView) findViewById(R.id.rv_update_recyclerview);
        mReminder = (TextView) findViewById(R.id.tv_update_reminder);
        mSizeProgressBar = (TextProgressBar) findViewById(R.id.pb_update_progressbar);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();
        mFocusMoveUtil.hideFocusForShowDelay(500);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        CanRecyclerViewDivider canRecyclerViewDivider = new CanRecyclerViewDivider(0, getResources().getDimensionPixelSize(R.dimen.px38), 0);
        mRecyclerView.addItemDecoration(canRecyclerViewDivider);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDetectionButton.post(new Runnable() {
            @Override
            public void run() {
                mDetectionButton.setFocusable(true);
                mDetectionButton.requestFocus();
            }
        });

    }

    @Override
    public void showLoading() {
        showLoadingDialog(getResources().getDimensionPixelSize(R.dimen.px150));
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showNoData() {
        mReminder.setText(getResources().getString(R.string.update_updateall));
        mReminder.setVisibility(View.VISIBLE);
        mCurrentnum.setVisibility(View.INVISIBLE);
        mTotalnum.setVisibility(View.INVISIBLE);

    }

    @Override
    public void hideNoData() {
        mReminder.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showStartAutoUpdate() {
        mReminder.setVisibility(View.VISIBLE);
        mReminder.setText(R.string.update_start_autoupdate);
    }

    @Override
    public void showSDProgressbar(int currentsize, String sdinfo) {
        mSizeProgressBar.setProgress(currentsize);
        mSizeProgressBar.setTextSize(getResources().getDimensionPixelOffset(R.dimen.px18));
        mSizeProgressBar.setText(sdinfo);
    }

    @Override
    public void refreshItem(int position) {
        mRecyclerAdapter.notifyItemChanged(position);
    }

    @Override
    public void refreshAll() {
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCurrentNum(int current, int total) {
        mCurrentnum.setVisibility(View.VISIBLE);
        mTotalnum.setVisibility(View.VISIBLE);
        mCurrentnum.setText(current + "");
        mTotalnum.setText("/" + total + "行");
    }

    @Override
    public void showInstallPkgList(List<AppInfoBean> mDatas) {
        if (mRecyclerAdapter == null) {
            mRecyclerAdapter = new UpdateManagerAdapter(mDatas);
        }
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.notifyDataSetChanged();
        if (mDatas.size() > 0) {
            mDetectionButton.setNextFocusRightId(R.id.rv_update_recyclerview);
        } else {
            mDetectionButton.setNextFocusRightId(R.id.bt_update_detection);
        }
    }

    @Override
    public void showInternetError() {
        mReminder.setVisibility(View.VISIBLE);
        mReminder.setText(R.string.no_network);
        mCurrentnum.setVisibility(View.INVISIBLE);
        mTotalnum.setVisibility(View.INVISIBLE);
    }

    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f);
            }
        }
    }

    /**
     * 按键事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UpdateManagerActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 启动更新管理
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UpdateManagerActivity.class);
        context.startActivity(intent);
    }

    /**
     * 限制点击频率
     *
     * @return
     */
    private boolean isFastContinueClickView() {
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - mLastClickTime < 2000) {
            return true;
        }
        mLastClickTime = curClickTime;
        return false;
    }

    /**
     * 限制移动速度
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            //控制按键响应的速度
            case KeyEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - moveTime > 200) {
                    moveTime = System.currentTimeMillis();
                } else {
                    return true;
                }
        }
        return super.dispatchKeyEvent(event);

    }

    @Override
    protected void onHomeKeyDown() {
        mPresenter.release();
        //finish();
        super.onHomeKeyDown();
    }

    /**
     * 使用eventbus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateApkInstallModel model) {
        //mRecyclerAdapter.notifyDataSetChanged();
        int number = model.getNumber();
        String url = model.getUrl();
        int itemPosition = mPresenter.getItemPosition(url);
        View childAt = mRecyclerView.getChildAt(itemPosition);
        final TextView status = (TextView) childAt.findViewById(R.id.tv_updateapp_downloading);
        final ImageView updateIcon = (ImageView) childAt.findViewById(R.id.iv_updateapp_updatedicon);
        if (number == 0) {
            status.setVisibility(View.INVISIBLE);
            updateIcon.setVisibility(View.VISIBLE);
            mPresenter.getUpdateApkNum(itemPosition);
            showToast(model.getAppname() + getResources().getString(R.string.install_success));
        } else if (number == 1) {
            status.setVisibility(View.VISIBLE);
            status.setText(getResources().getString(R.string.update_download_installfalse));
            updateIcon.setVisibility(View.INVISIBLE);
            showToast(model.getAppname() + getResources().getString(R.string.install_fail));
        }
    }


}
