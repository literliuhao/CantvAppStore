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
import com.can.appstore.update.model.UpdateApkModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.ui.view.GlideRoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.PromptUtils;


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
        mDownloadManager = DownloadManager.getInstance(MyApp.getContext());
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
        updateHolder.appSize.setText(date.getAppSizeStr());
        updateHolder.appVersioncode.setText(mDatas.get(position).getVersionName());
        updateHolder.appIcon.load(mDatas.get(position).getIconUrl());
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
                updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_waitting));
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_waitting));
                updateHolder.progressbar.setVisibility(View.VISIBLE);
                updateHolder.progressbar.setProgress((int) curDownloadTask.getPercent());
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_downloading));
                updateHolder.progressbar.setVisibility(View.VISIBLE);
                updateHolder.progressbar.setProgress((int) curDownloadTask.getPercent());
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED || downloadStatus == AppInstallListener.APP_INSTALLING) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_installing));
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else if (downloadStatus == AppInstallListener.APP_INSTALL_FAIL) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_installfalse));
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else if (downloadStatus == AppInstallListener.APP_INSTALL_SUCESS) {
                updateHolder.downloading.setVisibility(View.INVISIBLE);
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_ERROR) {
                updateHolder.downloading.setVisibility(View.VISIBLE);
                updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_false));
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
                }

                @Override
                public void onError(DownloadTask downloadTask, int errorCode) {
                    Log.i(TAG, "onError");
                    updateHolder.downloading.post(new Runnable() {
                        @Override
                        public void run() {
                            updateHolder.downloading.setVisibility(View.VISIBLE);
                            updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_false));
                            updateHolder.progressbar.setVisibility(View.INVISIBLE);
                        }
                    });
                    switch (errorCode) {
                        case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                            PromptUtils.toast(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.downlaod_error));
                            break;
                        case DOWNLOAD_ERROR_IO_ERROR:
                            PromptUtils.toast(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.downlaod_error));
                            break;
                        case DOWNLOAD_ERROR_NETWORK_ERROR:
                            PromptUtils.toast(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.network_connection_error));
                            break;
                        case DOWNLOAD_ERROR_UNKONW_ERROR:
                            PromptUtils.toast(MyApp.getContext(), MyApp.getContext().getResources().getString(R.string.unkonw_error));
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
                    final int itemPosition = getItemPosition(downloadTask.getUrl());
                    updateHolder.downloading.post(new Runnable() {
                        @Override
                        public void run() {
                            if (position == itemPosition) {
                                updateHolder.downloading.setVisibility(View.VISIBLE);
                                updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_installing));
                                updateHolder.progressbar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }

                @Override
                public void onInstallSucess(String id) {
                    final int itemPosition = getItemPosition(id);
                    getUpdateApkNum(0);
                    updateHolder.downloading.post(new Runnable() {
                        @Override
                        public void run() {
                            if (position == itemPosition) {
                                updateHolder.downloading.setVisibility(View.INVISIBLE);
                                updateHolder.updatedIcon.setVisibility(View.VISIBLE);
                                Log.i(TAG, "run: " + "安装成功");
                            }
                        }
                    });
                }

                @Override
                public void onInstallFail(String id) {
                    final int itemPosition = getItemPosition(id);
                    updateHolder.downloading.post(new Runnable() {
                        @Override
                        public void run() {
                            if (position == itemPosition) {
                                updateHolder.downloading.setVisibility(View.VISIBLE);
                                updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_installfalse));
                                updateHolder.updatedIcon.setVisibility(View.INVISIBLE);
                                Log.i(TAG, "run: " + "安装失败");

                            }
                        }
                    });
                }
                    @Override
                    public void onUninstallSucess (String id){

                    }

                    @Override
                    public void onUninstallFail (String id){

                    }
                }

                );
            }else{
                updateHolder.downloading.setVisibility(View.INVISIBLE);
                updateHolder.progressbar.setVisibility(View.INVISIBLE);
                updateHolder.updatedIcon.setVisibility(View.INVISIBLE);
            }
        }

    private void getInstallStatus(DownloadTask downloadTask, UpdateViewHolder updateHolder) {
        int result = InstallPkgUtils.installApp(downloadTask.getSaveDirPath(),downloadTask.getTotalSize());
        if (result == 0) {
            updateHolder.downloading.setVisibility(View.INVISIBLE);
            //updateHolder.downloading.setText("安装成功");
            updateHolder.updatedIcon.setVisibility(View.VISIBLE);
            Log.i(TAG, "run: " + "安装成功");
        } else {
            updateHolder.downloading.setVisibility(View.VISIBLE);
            updateHolder.downloading.setText(MyApp.getContext().getResources().getString(R.string.update_download_installfalse));
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
                status.setText(MyApp.getContext().getResources().getString(R.string.update_downloading));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_INIT:
            case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
            case DownloadStatus.DOWNLOAD_STATUS_START:
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.getContext().getResources().getString(R.string.update_download_waitting));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
            case AppInstallListener.APP_INSTALLING:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.getContext().getResources().getString(R.string.update_download_installing));
                break;
            case AppInstallListener.APP_INSTALL_FAIL:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.getContext().getResources().getString(R.string.update_download_installfalse));
                break;
            case AppInstallListener.APP_INSTALL_SUCESS:
                status.setVisibility(View.INVISIBLE);
                break;
            case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                status.setVisibility(View.VISIBLE);
                status.setText(MyApp.getContext().getResources().getString(R.string.update_download_false));
                break;
            default:
                status.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 获取指定item位置
     *
     * @param url
     * @return
     */
    public int getItemPosition(String url) {

        for (int i = 0; i < mDatas.size(); i++) {
            String downloadUrl = mDatas.get(i).getDownloadUrl();
            if (downloadUrl.equals(url)) {
                return i;
            } else if (MD5.MD5(downloadUrl).equals(url)) {
                return i;
            }
        }

        return 0;

    }

    /**
     * 获取可更新app数量
     *
     * @return
     */
    public void getUpdateApkNum(int position) {
        try {
            AutoUpdate.getInstance().mUpdateApkNumDatas.remove(0);
            //发送数量
            EventBus.getDefault().post(new UpdateApkModel(AutoUpdate.getInstance().mUpdateApkNumDatas.size()));
            Log.i(TAG, "getUpdateApkNum: " + AutoUpdate.getInstance().mUpdateApkNumDatas.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getUpdateApkNum: " + AutoUpdate.getInstance().mUpdateApkNumDatas.size());
    }

    class UpdateViewHolder extends RecyclerView.ViewHolder {
        GlideRoundCornerImageView appIcon;
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
            appIcon = (GlideRoundCornerImageView) view.findViewById(R.id.iv_updateapp_icon);
            progressbar = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);
            updatedIcon = (ImageView) view.findViewById(R.id.iv_updateapp_updatedicon);
            downloading = (TextView) view.findViewById(R.id.tv_updateapp_downloading);
        }
    }
}

