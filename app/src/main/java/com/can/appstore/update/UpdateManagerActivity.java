package com.can.appstore.update;

import android.app.Activity;
import android.app.Dialog;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.R;

import com.can.appstore.installpkg.utils.InstallPkgUtils;
import com.can.appstore.installpkg.view.LoadingDialog;
import com.can.appstore.update.model.AppInfoBean;

import com.can.appstore.wights.CanDialog;


import java.util.List;


import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.PreferencesUtils;

/**
 * 更新管理
 * Created by shenpx on 2016/10/12 0012.
 */

public class UpdateManagerActivity extends Activity implements UpdateContract.View,DownloadTaskListener {

    private CanRecyclerView mRecyclerView;
    private UpdateManagerAdapter mRecyclerAdapter;
    private Button mDetectionButton;
    private Button mAutoButton;
    private TextView mReminder;
    private int mCurrentPositon;
    private TextView mRoomSize;
    private boolean mAutoUpdate;
    private TextView mTotalnum;
    private TextView mCurrentnum;
    private ProgressBar mSizeProgressBar;
    private int i;
    private Dialog mLoadingDialog;
    FocusMoveUtil mFocusMoveUtil;
    FocusScaleUtil mFocusScaleUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;
    private CanDialog canDialog;
    private cn.can.downloadlib.DownloadManager manager;
    private UpdatePresenter mPresenter;
    private List<AppInfoBean> mUpdateList;
    private cn.can.downloadlib.DownloadManager mDownloadManager;
    private ProgressBar mUpdatePro;
    private String mCurrentId;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (msg.arg1 > 0) {
                        mUpdatePro.setVisibility(View.VISIBLE);
                        mUpdatePro.setProgress(msg.arg1);
                    } else {
                        mUpdatePro.setVisibility(View.INVISIBLE);
                    }
                    Toast.makeText(MyApp.mContext, msg.arg1 + "", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatemanager);
        mPresenter = new UpdatePresenter(this, UpdateManagerActivity.this);
        mAutoUpdate = PreferencesUtils.getBoolean(MyApp.mContext, "AUTO_UPDATE", false);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.release();
    }

    private void initFocusChange() {

        mDetectionButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(mDetectionButton, 1.0f);
                    //mFocusScaleUtil.scaleToLarge(mDetectionButton);
                } else {
                    mFocusScaleUtil.scaleToNormal(mDetectionButton);
                }
            }
        });

        mAutoButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(mAutoButton, 1.0f);
                    //mFocusScaleUtil.scaleToLarge(mAutoButton);
                } else {
                    mFocusScaleUtil.scaleToNormal(mAutoButton);
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
            }

        });
        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                myFocusRunnable.run();
            }
        });*/
    }

    private void initClick() {

        mDetectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(UpdateManagerActivity.this, "点击检测更新,item总数：" + mDatas.size(), Toast.LENGTH_SHORT).show();
                mAutoUpdate = PreferencesUtils.getBoolean(MyApp.mContext, "AUTO_UPDATE", false);
                if (mAutoUpdate) {
                    mPresenter.clearList();
                    mPresenter.setNum(0);
                    mReminder.setVisibility(View.VISIBLE);
                    mReminder.setText(R.string.update_start_autoupdate);
                    Toast.makeText(MyApp.mContext, R.string.update_start_autoupdate, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    mPresenter.getInstallPkgList(mAutoUpdate);
                }
            }
        });

        mAutoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAutoUpdate) {
                    PreferencesUtils.putBoolean(MyApp.mContext, "AUTO_UPDATE", false);
                    mAutoUpdate = false;
                    //initDialog("已开启");
                    mReminder.setVisibility(View.INVISIBLE);
                    Toast.makeText(UpdateManagerActivity.this, R.string.update_end_autoupdate, Toast.LENGTH_SHORT).show();
                } else {
                    PreferencesUtils.putBoolean(MyApp.mContext, "AUTO_UPDATE", true);
                    mAutoUpdate = true;
                    //initDialog("未开启");
                    mPresenter.getListSize();
                    Toast.makeText(UpdateManagerActivity.this, R.string.update_start_autoupdate, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRecyclerAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onClick(View view, int position, Object data) {
                mCurrentPositon = position;
                String downloadUrl = mUpdateList.get(position).getDownloadUrl();

                final ProgressBar progress = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);
                final TextView status = (TextView) view.findViewById(R.id.tv_updateapp_downloading);
                final ImageView updatedIcon = (ImageView) view.findViewById(R.id.iv_updateapp_updatedicon);
                if (mDownloadManager == null) {
                    mDownloadManager = cn.can.downloadlib.DownloadManager.getInstance(UpdateManagerActivity.this);
                }
                DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
                if (downloadTask == null) {
                    /*int status = downloadTask.getDownloadStatus();

                    if (status == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING
                            || status == DownloadStatus.DOWNLOAD_STATUS_PREPARE
                            || status == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                        //mDownloadManager.addDownloadListener(downloadTask, UpdatePresenter.this);
                        return;
                    } else if (status == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                        mDownloadManager.resume(downloadTask.getId());
                    }
                } else {*/
                    downloadTask = new DownloadTask();
                    String md5 = MD5.MD5(downloadUrl);
                    downloadTask.setFileName(md5);
                    downloadTask.setId(md5);
                    downloadTask.setSaveDirPath(MyApp.mContext.getExternalCacheDir().getPath() + "/");
                    downloadTask.setUrl(downloadUrl);
                    Toast.makeText(MyApp.mContext, downloadUrl, Toast.LENGTH_SHORT).show();
                    mDownloadManager.addDownloadTask(downloadTask, new DownloadTaskListener() {
                        @Override
                        public void onPrepare(DownloadTask downloadTask) {
                            Log.d("shen", "onPrepare: ");
                            progress.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.VISIBLE);
                                    status.setText("等待中");
                                    progress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onStart(DownloadTask downloadTask) {
                            Log.d("shen", "onStart: ");
                            progress.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.VISIBLE);
                                    status.setText("等待中");
                                    //refreshDownliadStatus(downloadTask.getDownloadStatus(), status);
                                    progress.setVisibility(View.INVISIBLE);
                                    //progress.setProgress((int) downloadTask.getPercent());
                                }
                            });
                        }

                        @Override
                        public void onDownloading(final DownloadTask downloadTask) {
                            Log.d("shen", "onDownloading: ");
                            progress.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.VISIBLE);
                                    status.setText("下载中");
                                    //refreshDownliadStatus(downloadTask.getDownloadStatus(), status);
                                    progress.setVisibility(View.VISIBLE);
                                    progress.setProgress((int) downloadTask.getPercent());
                                }
                            });
                        }

                        @Override
                        public void onPause(DownloadTask downloadTask) {
                            Log.d("shen", "onPause: ");
                            progress.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.VISIBLE);
                                    status.setText("等待中");
                                    //refreshDownliadStatus(downloadTask.getDownloadStatus(), status);
                                    progress.setVisibility(View.INVISIBLE);
                                    //progress.setProgress((int) downloadTask.getPercent());
                                }
                            });
                        }

                        @Override
                        public void onCancel(DownloadTask downloadTask) {
                            Log.d("shen", "onCancel: ");
                        }

                        @Override
                        public void onCompleted(final DownloadTask downloadTask) {
                            Log.d("shen", "onCompleted: ");
                            progress.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setVisibility(View.VISIBLE);
                                    status.setText("安装中");
                                    progress.setVisibility(View.INVISIBLE);
                                    int result = InstallPkgUtils.installApp(downloadTask.getSaveDirPath());
                                    if(result == 0){
                                        //status.setText("安装成功");
                                        updatedIcon.setVisibility(View.VISIBLE);
                                    }else{
                                        status.setText("安装失败");
                                        updatedIcon.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(DownloadTask downloadTask, int errorCode) {
                            Log.d("shen", "onError: ");
                            switch (errorCode) {
                                case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                                    Log.i("shen", "未找到下载文件: ");
                                    break;
                                case DOWNLOAD_ERROR_IO_ERROR:
                                    Log.i("shen", "IO异常: ");
                                    break;
                                case DOWNLOAD_ERROR_NETWORK_ERROR:
                                    Log.i("shen", "网络异常，请重试！");
                                    break;
                                case DOWNLOAD_ERROR_UNKONW_ERROR:
                                    Log.i("shen", "未知错误: ");
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

                    /*mDownloadManager.setAppInstallListener(new AppInstallListener() {
                        @Override
                        public void onInstalling(DownloadTask downloadTask) {
                            String crurrentId = downloadTask.getId();
                            if (mCurrentId.equals(crurrentId)) {
                                progress.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        status.setVisibility(View.VISIBLE);
                                        status.setText("安装中");
                                        progress.setVisibility(View.INVISIBLE);
                                        updatedIcon.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onInstallSucess(String id) {
                            if (mCurrentId.equals(id)) {
                                progress.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        status.setVisibility(View.INVISIBLE);
                                        progress.setVisibility(View.INVISIBLE);
                                        updatedIcon.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onInstallFail(String id) {
                            if (mCurrentId.equals(id)) {
                                progress.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        status.setVisibility(View.VISIBLE);
                                        status.setText("安装失败");
                                        progress.setVisibility(View.INVISIBLE);
                                        updatedIcon.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });*/
                }

            }
        });

    }

    private void initDialog(String str) {
        canDialog = new CanDialog(UpdateManagerActivity.this);
        canDialog.showDialogForUpdateSetting("更新设置", "开启后将自动更新", str, "", "开启", "关闭", new CanDialog.OnCanBtnClickListener() {
            @Override
            public void onClickPositive() {
                PreferencesUtils.putBoolean(MyApp.mContext, "AUTO_UPDATE", true);
                mAutoUpdate = true;
                canDialog.dismiss();
            }

            @Override
            public void onClickNegative() {
                PreferencesUtils.putBoolean(MyApp.mContext, "AUTO_UPDATE", false);
                mAutoUpdate = false;
                canDialog.dismiss();
            }
        });
        canDialog.show();
    }

    protected void initData() {
        mPresenter.getSDInfo();
        mPresenter.getInstallPkgList(mAutoUpdate);
        mPresenter.setNum(0);
        mUpdateList = mPresenter.getList();
    }

    private void initView() {
        mTotalnum = (TextView) findViewById(R.id.tv_update_totalnum);
        mCurrentnum = (TextView) findViewById(R.id.tv_update_currentnum);
        mRoomSize = (TextView) findViewById(R.id.tv_update_roomsize);
        mDetectionButton = (Button) findViewById(R.id.bt_update_detection);
        mAutoButton = (Button) findViewById(R.id.bt_update_auto);
        mRecyclerView = (CanRecyclerView) findViewById(R.id.rv_update_recyclerview);
        mReminder = (TextView) findViewById(R.id.tv_update_reminder);
        mSizeProgressBar = (ProgressBar) findViewById(R.id.pb_update_progressbar);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setSelected(true);

    }

    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.createLoadingDialog(this, getString((R.string.update_search_updateinfo)));
            mLoadingDialog.show();
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void showNoData() {
        mReminder.setVisibility(View.VISIBLE);
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
    public void showSDProgressbar(int currentsize, int total, String sdinfo) {
        mSizeProgressBar.setMax(total);
        mSizeProgressBar.setProgress(currentsize);
        mRoomSize.setText(getString(R.string.update_sdavaliable_size) + sdinfo);
    }

    @Override
    public void refreshItem(int position) {

    }

    @Override
    public void refreshAll() {
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCurrentNum(int current, int total) {
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
    }

    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f);
                //mFocusScaleUtil.scaleToLarge(mFocusedListChild);
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

    @Override
    public void onPrepare(DownloadTask downloadTask) {
        String url = downloadTask.getUrl();
        /*for (int i = 0; i < mUpdateList.size(); i++) {
            String downloadUrl = mUpdateList.get(i).getDownloadUrl();
            if (downloadUrl.equals(url)) {
                View view = mRecyclerView.getChildAt(i);
                ProgressBar progressbar = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);

            }
        }*/
    }

    @Override
    public void onStart(DownloadTask downloadTask) {

    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        /*mHandler.removeMessages(1);
        Message message = mHandler.obtainMessage();
        message.what = 1;
        message.arg1 = (int) downloadTask.getPercent();
        mHandler.sendMessage(message);*/
    }

    @Override
    public void onPause(DownloadTask downloadTask) {

    }

    @Override
    public void onCancel(DownloadTask downloadTask) {

    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {

    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {

    }


    /*GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
    int firstItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
    if (i - firstItemPosition >= 0) {
        //得到要更新的item的view
        View view = mRecyclerView.getChildAt(i - firstItemPosition);
        if (null != mRecyclerView.getChildViewHolder(view)) {
            ProductsViewHolder viewHolder = (ProductsViewHolder) mRecyclerView.getChildViewHolder(view);

        }
    }*/
}
