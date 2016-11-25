package com.can.appstore.update;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.installpkg.utils.InstallPkgUtils;
import com.can.appstore.update.model.AppInfoBean;

import java.util.List;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.ui.view.RoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.ToastUtils;


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
        updateHolder.updatedIcon.setVisibility(mDatas.get(position).getUpdated() ? View.VISIBLE : View.INVISIBLE);
        updateHolder.downloading.setVisibility(View.INVISIBLE);
        /**
         * 初始化更新状态
         */
        if (curDownloadTask != null) {
            int downloadStatus = curDownloadTask.getDownloadStatus();
            //获取更新状态
            if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.mContext.getResources().getString(R.string.update_download_waitting));
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.mContext.getResources().getString(R.string.update_download_waitting));
                updateHolder.progressbar.setVisibility(View.VISIBLE);
                updateHolder.progressbar.setProgress((int) curDownloadTask.getPercent());
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.mContext.getResources().getString(R.string.update_downloading));
                updateHolder.progressbar.setVisibility(View.VISIBLE);
                updateHolder.progressbar.setProgress((int) curDownloadTask.getPercent());
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED || downloadStatus == AppInstallListener.APP_INSTALLING) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.mContext.getResources().getString(R.string.update_download_installing));
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else if (downloadStatus == AppInstallListener.APP_INSTALL_FAIL) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.mContext.getResources().getString(R.string.update_download_installfalse));
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else if (downloadStatus == AppInstallListener.APP_INSTALL_SUCESS) {
                updateHolder.downloading.setVisibility(View.INVISIBLE);
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.mContext.getResources().getString(R.string.update_download_false));
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else {
                updateHolder.downloading.setVisibility(View.INVISIBLE);
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
                updateHolder.updatedIcon.setVisibility(View.INVISIBLE);
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
                    refreshStatus(downloadTask, updateHolder.downloading, updateHolder.progressbar, updateHolder.updatedIcon);
                }

                @Override
                public void onStart(DownloadTask downloadTask) {
                    Log.i(TAG, "onStart");
                    refreshStatus(downloadTask, updateHolder.downloading, updateHolder.progressbar, updateHolder.updatedIcon);
                }

                @Override
                public void onDownloading(DownloadTask downloadTask) {
                    Log.i(TAG, "onDownloading");
                    refreshStatus(downloadTask, updateHolder.downloading, updateHolder.progressbar, updateHolder.updatedIcon);
                }

                @Override
                public void onPause(DownloadTask downloadTask) {
                    Log.i(TAG, "onPause");
                    refreshStatus(downloadTask, updateHolder.downloading, updateHolder.progressbar, updateHolder.updatedIcon);
                }

                @Override
                public void onCancel(DownloadTask downloadTask) {
                    Log.i(TAG, "onCancel");
                }

                @Override
                public void onCompleted(final DownloadTask downloadTask) {
                    Log.i(TAG, "onCompleted");
                    refreshStatus(downloadTask, updateHolder.downloading, updateHolder.progressbar, updateHolder.updatedIcon);
                    updateHolder.updatedIcon.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getInstallStatus(downloadTask, updateHolder);
                        }
                    }, 1000);
                }

                @Override
                public void onError(DownloadTask downloadTask, int errorCode) {
                    Log.i(TAG, "onError");
                    refreshStatus(downloadTask, updateHolder.downloading, updateHolder.progressbar, updateHolder.updatedIcon);
                    switch (errorCode) {
                        case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                            ToastUtils.showMessage(MyApp.mContext, "未找到下载文件");
                            Log.i(TAG, "未找到下载文件: ");
                            break;
                        case DOWNLOAD_ERROR_IO_ERROR:
                            ToastUtils.showMessage(MyApp.mContext, "IO异常");
                            Log.i(TAG, "IO异常: ");
                            break;
                        case DOWNLOAD_ERROR_NETWORK_ERROR:
                            ToastUtils.showMessage(MyApp.mContext, "网络异常，请重试！");
                            Log.i(TAG, "网络异常，请重试！");
                            break;
                        case DOWNLOAD_ERROR_UNKONW_ERROR:
                            ToastUtils.showMessage(MyApp.mContext, "未知错误");
                            Log.i(TAG, "未知错误: ");
                            break;
                        default:
                            break;
                    }
                    mDownloadManager.cancel(downloadTask);
                }
            });
        } else {
            updateHolder.downloading.setVisibility(View.INVISIBLE);
            updateHolder.progressbar.setVisibility(View.INVISIBLE);
            updateHolder.updatedIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void getInstallStatus(DownloadTask downloadTask, UpdateViewHolder updateHolder) {
        int result = InstallPkgUtils.installApp(downloadTask.getSaveDirPath());
        if (result == 0) {
            updateHolder.downloading.setVisibility(View.INVISIBLE);
            //updateHolder.downloading.setText("安装成功");
            updateHolder.updatedIcon.setVisibility(View.VISIBLE);
            Log.i(TAG, "run: " + "安装成功");
        } else {
            updateHolder.downloading.setVisibility(View.VISIBLE);
            updateHolder.downloading.setText(MyApp.mContext.getResources().getString(R.string.update_download_installfalse));
            updateHolder.updatedIcon.setVisibility(View.INVISIBLE);
            Log.i(TAG, "run: " + "安装失败");
        }
    }

    private void refreshStatus(final DownloadTask downloadTask, final TextView status, final ProgressBar progress, ImageView updateicon) {
        progress.post(new Runnable() {
            @Override
            public void run() {
                refreshDownliadStatus(downloadTask.getDownloadStatus(), status);
                if (downloadTask.getCompletedSize() > 0 && downloadTask.getCompletedSize() < downloadTask.getTotalSize() && downloadTask.getDownloadStatus() != DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                    progress.setVisibility(View.VISIBLE);
                } else {
                    progress.setVisibility(View.INVISIBLE);
                }
                progress.setProgress((int) downloadTask.getPercent());
            }
        });

    }


    public void refreshDownliadStatus(int downloadStatus, TextView status) {
        switch (downloadStatus) {
            case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.mContext.getResources().getString(R.string.update_downloading));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_INIT:
            case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
            case DownloadStatus.DOWNLOAD_STATUS_START:
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.mContext.getResources().getString(R.string.update_download_waitting));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
            case AppInstallListener.APP_INSTALLING:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.mContext.getResources().getString(R.string.update_download_installing));
                break;
            case AppInstallListener.APP_INSTALL_FAIL:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.mContext.getResources().getString(R.string.update_download_installfalse));
                break;
            case AppInstallListener.APP_INSTALL_SUCESS:
                status.setVisibility(View.INVISIBLE);
                break;
            case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.mContext.getResources().getString(R.string.update_download_false));
                break;
            default:
                status.setVisibility(View.INVISIBLE);
                break;
        }
    }

    class UpdateViewHolder extends RecyclerView.ViewHolder {
        RoundCornerImageView appIcon;
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
            appIcon = (RoundCornerImageView) view.findViewById(R.id.iv_updateapp_icon);
            progressbar = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);
            updatedIcon = (ImageView) view.findViewById(R.id.iv_updateapp_updatedicon);
            downloading = (TextView) view.findViewById(R.id.tv_updateapp_downloading);
        }
    }
}

