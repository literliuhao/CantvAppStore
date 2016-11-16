package com.can.appstore.update;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.installpkg.InstallManagerAdapter;
import com.can.appstore.update.model.AppInfoBean;

import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by shenpx on 2016/10/12 0012.
 */

public class UpdateManagerAdapter extends CanRecyclerViewAdapter<AppInfoBean> {

    private List<AppInfoBean> mDatas;
    private DownloadManager mDownloadManager;
    private static final String TAG = "updateManagerAdapter";


    public UpdateManagerAdapter(List<AppInfoBean> datas) {
        super(datas);
        mDatas = datas;
        mDownloadManager = DownloadManager.getInstance(MyApp.mContext);
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_updatemanager_item, parent, false);
        UpdateViewHolder holder = new UpdateViewHolder(view);
        return holder;
    }

    @Override
    protected void bindContentData(AppInfoBean date, RecyclerView.ViewHolder holder, final int position) {
        final UpdateViewHolder updateHolder = (UpdateViewHolder) holder;
        String downloadUrl = mDatas.get(position).getDownloadUrl();
        DownloadTask curDownloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
        updateHolder.appName.setText(date.getAppName());
        updateHolder.appSize.setText(date.getAppSize());
        updateHolder.appVersioncode.setText(mDatas.get(position).getVersionName());
        updateHolder.appIcon.setImageDrawable(mDatas.get(position).getIcon());
//        ImageLoader.getInstance().load(MyApp.mContext,updateHolder.appIcon,mDatas.get(position).getIconUrl(),0,0,null,null);
        updateHolder.updatedIcon.setVisibility(mDatas.get(position).getInstall() ? View.VISIBLE : View.INVISIBLE);
        //updateHolder.downloading.setVisibility(mDatas.get(position).getIsInstalling() ? (mDatas.get(position).getInstall() ? View.INVISIBLE : View.VISIBLE) : View.INVISIBLE);
        /**
         * 初始化更新状态
         */
        //mDownloadManager = DownloadManager.getInstance(MyApp.mContext);
        if (curDownloadTask != null) {
            int downloadStatus = curDownloadTask.getDownloadStatus();
            //获取更新状态
            if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText("等待中");
                updateHolder.progressbar.setVisibility(View.GONE);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText("等待中");
                updateHolder.progressbar.setVisibility(View.VISIBLE);
                updateHolder.progressbar.setProgress((int) curDownloadTask.getPercent());
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText("下载中");
                updateHolder.progressbar.setVisibility(View.VISIBLE);
                updateHolder.progressbar.setProgress((int) curDownloadTask.getPercent());
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText("安装中");
                updateHolder.progressbar.setVisibility(View.GONE);
                updateHolder.progressbar.setProgress((int) curDownloadTask.getPercent());
            }
            mDownloadManager.addDownloadListener(curDownloadTask, new DownloadTaskListener() {
                /**
                 * 更新进度监听
                 *
                 * @param downloadTask
                 */
                @Override
                public void onPrepare(DownloadTask downloadTask) {
                    Log.i(TAG, "onPrepare");
                    if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                        refreshStatus(downloadTask,updateHolder.downloading,updateHolder.progressbar);
                    }
                }

                @Override
                public void onStart(DownloadTask downloadTask) {
                    Log.i(TAG, "onStart");
                    if (downloadTask.getDownloadStatus() == 0) {
                        refreshStatus(downloadTask,updateHolder.downloading,updateHolder.progressbar);
                    }
                }

                @Override
                public void onDownloading(DownloadTask downloadTask) {
                    Log.i(TAG, "onDownloading");
                    refreshStatus(downloadTask,updateHolder.downloading,updateHolder.progressbar);
                }

                @Override
                public void onPause(DownloadTask downloadTask) {
                    Log.i(TAG, "onPause");
                    refreshStatus(downloadTask,updateHolder.downloading,updateHolder.progressbar);
                }

                @Override
                public void onCancel(DownloadTask downloadTask) {
                    Log.i(TAG, "onCancel");
//        mUpdateView.refreshUpdateButton("等待中", true);
//        mUpdateView.refreshUpdateProgress((int) downloadTask.getPercent(), false);
                }

                @Override
                public void onCompleted(DownloadTask downloadTask) {
                    Log.i(TAG, "onCompleted");
                    if (downloadTask != null) {
                        refreshStatus(downloadTask,updateHolder.downloading,updateHolder.progressbar);
                    }
                }

                @Override
                public void onError(DownloadTask downloadTask, int errorCode) {
                    Log.i(TAG, "onError");
                    switch (errorCode) {
                        case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                            //mUpdateView.showCompleted("未找到下载文件");
                            break;
                        case DOWNLOAD_ERROR_IO_ERROR:
                            //mUpdateView.showCompleted("IO异常");
                            break;
                        case DOWNLOAD_ERROR_NETWORK_ERROR:
                            //mUpdateView.showCompleted("网络异常，请重试！");
                            break;
            /*case DOWNLOAD_ERROR_UNKONW_ERROR:
                mUpdateView.showCompleted("未知错误");
                break;*/
                        default:
                            break;
                    }
                }
            });
        } else {
            updateHolder.downloading.setVisibility(View.GONE);
            updateHolder.progressbar.setVisibility(View.GONE);
        }
    }

    private void refreshStatus(final DownloadTask downloadTask, TextView status, final ProgressBar progress) {
        refreshDownliadStatus(downloadTask.getDownloadStatus(), status);
        //if (downloadTask.getCompletedSize() > 0 && downloadTask.getCompletedSize() < downloadTask.getTotalSize()) {
        progress.post(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.VISIBLE);
                progress.setProgress((int) downloadTask.getPercent());
            }
        });

    }


    public void refreshDownliadStatus(int downloadStatus, TextView status) {
        switch (downloadStatus) {
            case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                status.setVisibility(View.VISIBLE);
                status.setText("下载中");
                break;
            case DownloadStatus.DOWNLOAD_STATUS_INIT:
            case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
            case DownloadStatus.DOWNLOAD_STATUS_START:
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                status.setVisibility(View.VISIBLE);
                status.setText("等待中");
                break;
            case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                status.setVisibility(View.VISIBLE);
                status.setText("安装中");
                break;
            default:
                status.setVisibility(View.INVISIBLE);
                break;
                    /*case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                        controlButton.setText("重试");
                        break;*/
        }
    }

    class UpdateViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appVersioncode;
        TextView appSize;
        ProgressBar progressbar;
        ImageView updatedIcon;
        TextView downloading;

        public UpdateViewHolder(View view) {
            super(view);
            appName = (TextView) view.findViewById(R.id.tv_updateapp_name);
            appSize = (TextView) view.findViewById(R.id.tv_updateapp_size);
            appVersioncode = (TextView) view.findViewById(R.id.tv_updateapp_versioncode);
            appIcon = (ImageView) view.findViewById(R.id.iv_updateapp_icon);
            progressbar = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);
            updatedIcon = (ImageView) view.findViewById(R.id.iv_updateapp_updatedicon);
            downloading = (TextView) view.findViewById(R.id.tv_updateapp_downloading);
        }

        /**
         * 添加更新任务
         */
        public void addUpdateTask(int position) {
            String downloadUrl = mDatas.get(position).getDownloadUrl();
            //mDownloadManager = DownloadManager.getInstance(MyApp.mContext);
            DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
            if (downloadTask != null) {
                int status = downloadTask.getDownloadStatus();
                if (status == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING
                        || status == DownloadStatus.DOWNLOAD_STATUS_PREPARE
                        || status == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                    //mDownloadManager.addDownloadListener(downloadTask, UpdatePresenter.this);
                    return;
                } else if (status == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                    mDownloadManager.resume(downloadTask.getId());
                }
            } else {
                downloadTask = new DownloadTask();
                String md5 = MD5.MD5(downloadUrl);
                downloadTask.setFileName(md5);
                downloadTask.setId(md5);
                downloadTask.setSaveDirPath(MyApp.mContext.getExternalCacheDir().getPath() + "/");
                downloadTask.setUrl(downloadUrl);
                //mDownloadManager.addDownloadTask(downloadTask, mDownloadListener);
            }
        }
    }
}

