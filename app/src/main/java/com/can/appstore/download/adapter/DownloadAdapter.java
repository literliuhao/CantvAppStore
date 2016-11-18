package com.can.appstore.download.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.download.DownloadListener;
import com.can.appstore.download.DownloadPresenterImpl;
import com.can.appstore.download.widget.RotateView;
import com.can.appstore.eventbus.event.DownloadEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.ApkUtils;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PromptUtils;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.ToastUtils;

/**
 * Created by laiforg on 2016/10/31.
 */

public class DownloadAdapter extends CanRecyclerViewAdapter<DownloadTask> {

    private static final String TAG = "DownloadAdapter";
    private OnItemEventListener mOnItemEventListener;
    private List<DownloadTask> data;
    private ItemEventListener mHolderItemEventListener;
    private LayoutInflater mLayoutInflater;
    private RecyclerView mRecyclerView;

    public DownloadAdapter(List<DownloadTask> datas) {
        super(datas);
        data = datas;
        mHolderItemEventListener = new ItemEventListener();
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View itemView = mLayoutInflater.inflate(R.layout.layout_download_item, parent, false);
        DownloadViewHolder holder = new DownloadViewHolder(itemView);
        holder.eventListener = mHolderItemEventListener;
        holder.initItemEventListener();
        return holder;
    }

    @Override
    protected void bindContentData(DownloadTask task, RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "bindContentData: task=" + task.toString());
        DownloadViewHolder viewHolder = (DownloadViewHolder) holder;
        viewHolder.appNameTv.setText(task.getFileName());
        viewHolder.position = position;
        viewHolder.downloadTask = task;
        viewHolder.appContentLayout.setTag(viewHolder);
        viewHolder.appControlBtn.setTag(viewHolder);
        viewHolder.appDeleteBtn.setTag(viewHolder);
        viewHolder.refreshStatus();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }


    /**
     * DownloadViewHolder create by laifrog
     */
    public static class DownloadViewHolder extends RecyclerView.ViewHolder {

        public static final int DELAY_MILLIS = 50;

        TextView appNameTv, appSizeTv, appDownloadStatusTv;
        ImageView appIconImgVi;
        RotateView appDownloadStatusImgVi;
        ProgressBar appDownloadProgressBar;
        RelativeLayout appControlLayout, appContentLayout;
        TextView appDeleteBtn, appControlBtn;

        DownloadTask downloadTask;
        ItemEventListener eventListener;
        private DownloadListener downloadListener;
        private Runnable showControlViewRunnable, refreshStatusRunnable, selectedViewRunnable;


        int position = -1;

        private EventBus eventBus;

        public DownloadViewHolder(View itemView) {
            super(itemView);
            init();
        }

        private void init() {
            eventBus = EventBus.getDefault();
            initListener();
            initView();
            initRunnable();
            initItemAttachStateListener();
        }

        /**
         * 初始化downloadListener appintallslistener
         */
        private void initListener() {
            downloadListener = new DownloadListener() {
                @Override
                public void onError(DownloadTask downloadTask, int errorCode) {
                    postRefreshStatus();
                }

                @Override
                public void onDownloadStatusUpdate(DownloadTask downloadTask) {
                    postRefreshStatus();
                }
            };
        }

        private void initView() {
            appNameTv = (TextView) itemView.findViewById(R.id.download_item_title_tv);
            appSizeTv = (TextView) itemView.findViewById(R.id.download_item_size_tv);
            appIconImgVi = (ImageView) itemView.findViewById(R.id.download_item_appicon_imgvi);
            appDownloadProgressBar = (ProgressBar) itemView.findViewById(R.id.download_item_progress);
            appDownloadStatusImgVi = (RotateView) itemView.findViewById(R.id.download_item_status_imgvi);
            appDownloadStatusTv = (TextView) itemView.findViewById(R.id.download_item_status_tv);
            appControlLayout = (RelativeLayout) itemView.findViewById(R.id.download_item_control_rlayout);
            appContentLayout = (RelativeLayout) itemView.findViewById(R.id.download_item_content_rlayout);
            appDeleteBtn = (TextView) itemView.findViewById(R.id.download_item_delete_btn);
            appControlBtn = (TextView) itemView.findViewById(R.id.download_item_control_btn);
        }

        private void initItemEventListener() {
            appContentLayout.setOnFocusChangeListener(eventListener);
            appControlBtn.setOnFocusChangeListener(eventListener);
            appDeleteBtn.setOnFocusChangeListener(eventListener);
            appDeleteBtn.setOnClickListener(eventListener);
            appControlBtn.setOnClickListener(eventListener);
            appContentLayout.setOnClickListener(eventListener);
            appContentLayout.setOnKeyListener(eventListener);
        }

        private void initItemAttachStateListener() {

            itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    setDownloadListener();
                    eventBus.register(DownloadViewHolder.this);
                    appDownloadStatusImgVi.startRotate();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    removeDownloadListener();
                    v.removeCallbacks(showControlViewRunnable);
                    v.removeCallbacks(selectedViewRunnable);
                    eventBus.unregister(DownloadViewHolder.this);
                    appDownloadStatusImgVi.stopRotate();
                }
            });
        }

        private void initRunnable() {

            showControlViewRunnable = new Runnable() {
                @Override
                public void run() {
                    appControlLayout.setVisibility(View.VISIBLE);
                }
            };

            refreshStatusRunnable = new Runnable() {
                @Override
                public void run() {
                    refreshStatus();
                }
            };

            selectedViewRunnable = new Runnable() {
                @Override
                public void run() {
                    appContentLayout.setSelected(true);
                }
            };
        }

        private void setDownloadListener() {
            if (downloadTask != null) {
                downloadTask.addDownloadListener(downloadListener);
            }
        }

        private void removeDownloadListener() {
            if (downloadTask != null) {
                downloadTask.removeDownloadListener(downloadListener);
            }
        }

        /**
         * item显示右侧controlview
         */
        private void showControlViewDelay() {
            itemView.postDelayed(showControlViewRunnable, DELAY_MILLIS);
        }

        /**
         * item隐藏右侧controlview
         */
        public void hidecontrolView() {
            itemView.removeCallbacks(showControlViewRunnable);
            appControlLayout.setVisibility(View.INVISIBLE);
        }

        /**
         * 设置item左侧selected选中状态
         */
        public void setContentLayoutSelected() {
            itemView.postDelayed(selectedViewRunnable, DELAY_MILLIS);
        }

        /**
         * 设置item左侧Unselected选中状态
         */
        public void setContentLayoutUnselected() {
            itemView.removeCallbacks(selectedViewRunnable);
            appContentLayout.setSelected(false);
        }

        public void refreshStatus() {
            refreshControlButtonStatus();
            refreshDownloadStatus();
        }

        public void postRefreshStatus() {
            itemView.post(refreshStatusRunnable);
        }

        /**
         * 更新控制按钮状态
         */
        private void refreshControlButtonStatus() {
            if (downloadTask == null) {
                return;
            }
            switch (downloadTask.getDownloadStatus()) {
                case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                case DownloadStatus.DOWNLOAD_STATUS_INIT:
                case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
                case DownloadStatus.DOWNLOAD_STATUS_START:
                    appControlBtn.setText("暂停");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    appControlBtn.setText("继续");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                case DownloadStatus.SPACE_NOT_ENOUGH:
                    appControlBtn.setText("重试");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                    appControlBtn.setText("安装");
                    break;
                case AppInstallListener.APP_INSTALLING:
                    appControlBtn.setText("安装中");
                    break;
                case AppInstallListener.APP_INSTALL_FAIL:
                    appControlBtn.setText("重新安装");
                    break;
                case AppInstallListener.APP_INSTALL_SUCESS:
                    appControlBtn.setText("打开");
                    break;
            }
        }

        /**
         * 更新状态图标 进度 。。。。
         */
        private void refreshDownloadStatus() {
            if (downloadTask == null) {
                return;
            }
            switch (downloadTask.getDownloadStatus()) {
                case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_downloading);
                    appDownloadStatusTv.setText("下载中");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_download_pause);
                    appDownloadStatusTv.setText("已暂停");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_INIT:
                case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
                case DownloadStatus.DOWNLOAD_STATUS_START:
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_download_wait);
                    appDownloadStatusTv.setText("等待中");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                case DownloadStatus.SPACE_NOT_ENOUGH:
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_download_fail);
                    appDownloadStatusTv.setText("失败");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_download_finish);
                    appDownloadStatusTv.setText("下载成功");
                    break;
                case AppInstallListener.APP_INSTALLING:
                    appDownloadStatusImgVi.setImageResource(R.drawable.rotate_drawable);
                    appDownloadStatusTv.setText("安装中");
                    break;
                case AppInstallListener.APP_INSTALL_FAIL:
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_download_fail);
                    appDownloadStatusTv.setText("安装失败");
                    break;
                case AppInstallListener.APP_INSTALL_SUCESS:
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_download_finish);
                    appDownloadStatusTv.setText("安装成功");
                    break;
            }
            appDownloadStatusImgVi.updateStatus(downloadTask.getDownloadStatus());
            appDownloadProgressBar.setProgress((int) downloadTask.getPercent());
            appSizeTv.setText(StringUtils.formatFileSize(downloadTask.getCompletedSize(), false) +
                    "/" + StringUtils.formatFileSize(downloadTask.getTotalSize(), false));
        }


        /**
         * download页面，全部暂停按钮监听，用于刷新当前屏幕的item状态
         *
         * @param event
         */
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onDownloadEvent(DownloadEvent event) {
            if (DownloadPresenterImpl.TAG_DOWNLOAD_UPDATA__STATUS.equals(event.getTag())) {
                switch (event.getEventType()) {
                    case DownloadEvent.DOWNLOADEVENT_UPDATE_DOWNLOAD_STATUS:
                        refreshStatus();
                        break;
                    case DownloadEvent.DOWNLOADEVENT_UPDATE_INSTALL_STATUS:
                        if (event.getDownloadTaskId().equals(downloadTask.getId())) {
                            refreshStatus();
                        }
                        break;
                }
            }
        }
    }

    public void setOnItemEventListener(OnItemEventListener listener) {
        this.mOnItemEventListener = listener;
    }

    /**
     * 外部回调接口
     */
    public interface OnItemEventListener {
        void onItemContentFocusChanged(View view, boolean hasFocus, int pos);

        void onItemControlButtonFocusChanged(View view, boolean hasFocus, int pos, DownloadTask downloadTask);

        void onItemDeleteButtonFocusChanged(View view, boolean hasFocus, int pos, DownloadTask downloadTask);

        void onItemContentClick(View view, int pos, DownloadTask downloadTask);

        void onControlButtonClick(View view, int pos, DownloadTask downloadTask);

        void onDeleteButtonClick(View view, int pos, DownloadTask downloadTask);

        boolean onItemContentKeyListener(View view, int keyCode, KeyEvent event, int pos, DownloadTask downloadTask);
    }

    /**
     * @author laiforg
     *         item  View.OnFocusChangeListener,View.OnClickListener的实现类，view.OnKeyListener
     */
    public class ItemEventListener implements View.OnFocusChangeListener, View.OnClickListener, View.OnKeyListener {

        @Override
        public void onClick(View v) {
            DownloadViewHolder holder = (DownloadViewHolder) v.getTag();
            if (holder == null) {
                return;
            }
            if (v.getId() == holder.appContentLayout.getId()) {
                if (mOnItemEventListener != null) {
                    mOnItemEventListener.onItemContentClick(v, holder.position, holder.downloadTask);
                }
            } else if (v.getId() == holder.appControlBtn.getId()) {

                switch (holder.downloadTask.getDownloadStatus()) {
                    case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                    case DownloadStatus.DOWNLOAD_STATUS_INIT:
                    case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
                    case DownloadStatus.DOWNLOAD_STATUS_START:
                        DownloadManager.getInstance(v.getContext().
                                getApplicationContext()).pause(holder.downloadTask);
                        break;
                    case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                        DownloadManager.getInstance(v.getContext().
                                getApplicationContext()).resume(holder.downloadTask.getId());
                        break;
                    case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                        holder.downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
                        DownloadManager.getInstance(v.getContext().getApplicationContext()).
                                addDownloadTask(holder.downloadTask, holder.downloadListener);
                        break;
                    case DownloadStatus.SPACE_NOT_ENOUGH:
                        DownloadManager.getInstance(v.getContext().
                                getApplicationContext()).resume(holder.downloadTask.getId());
                        break;
                    case AppInstallListener.APP_INSTALLING:
                        //正在安装
                        ToastUtils.showMessage(v.getContext(), v.getContext().getString(R.string.download_installing));
                        break;
                    case AppInstallListener.APP_INSTALL_SUCESS:
                        //TODO
                        String pacageName = ApkUtils.getPkgNameFromApkFile(v.getContext().getApplicationContext(), holder.downloadTask.getFilePath());
                        boolean isAvailable = ApkUtils.isAvailable(v.getContext().getApplicationContext(), pacageName);
                        if (isAvailable) {
                            PackageUtil.openApp(v.getContext().getApplicationContext(), pacageName);
                        } else {
                            PromptUtils.toastShort(v.getContext(), v.getContext().getString(R.string.download_open_app_error));
                        }
                        break;
                    case AppInstallListener.APP_INSTALL_FAIL:
                        holder.downloadTask.setDownloadStatus(AppInstallListener.APP_INSTALLING);

                        break;
                }
                //考虑到未执行的任务从init状态调pause状态时不会回掉，故此时手动刷新一次UI
                holder.refreshStatus();
                if (mOnItemEventListener != null) {
                    mOnItemEventListener.onControlButtonClick(v, holder.position, holder.downloadTask);
                }
            } else if (v.getId() == holder.appDeleteBtn.getId()) {
                if (AppInstallListener.APP_INSTALLING == holder.downloadTask.getDownloadStatus()) {
                    //安装中 安装成功 删除按钮不可点击
                    ToastUtils.showMessage(v.getContext(), v.getContext().getString(R.string.download_installing));
                    return;
                }
                DownloadManager.getInstance(v.getContext().getApplicationContext()).cancel(holder.downloadTask);
                if (mOnItemEventListener != null) {
                    mOnItemEventListener.onDeleteButtonClick(v, holder.position, holder.downloadTask);
                }
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            DownloadViewHolder holder = (DownloadViewHolder) v.getTag();
            if (holder == null) {
                return;
            }
            if (!holder.appContentLayout.hasFocus()
                    && !holder.appDeleteBtn.hasFocus()
                    && !holder.appControlBtn.hasFocus()) {
                holder.hidecontrolView();
            }
            if (v.getId() == holder.appContentLayout.getId()) {
                if (hasFocus) {
                    holder.showControlViewDelay();
                    holder.setContentLayoutSelected();
                } else {
                    holder.setContentLayoutUnselected();
                }
                if (mOnItemEventListener != null) {
                    mOnItemEventListener.onItemContentFocusChanged(v, hasFocus, holder.position);
                }
            } else if (v.getId() == holder.appControlBtn.getId()) {
                if (hasFocus) {
                    holder.appControlLayout.setVisibility(View.VISIBLE);
                }
                if (mOnItemEventListener != null) {
                    mOnItemEventListener.onItemControlButtonFocusChanged(v, hasFocus, holder.position, holder.downloadTask);
                }
            } else if (v.getId() == holder.appDeleteBtn.getId()) {
                if (hasFocus) {
                    holder.appControlLayout.setVisibility(View.VISIBLE);
                }
                if (mOnItemEventListener != null) {
                    mOnItemEventListener.onItemDeleteButtonFocusChanged(v, hasFocus, holder.position, holder.downloadTask);
                }
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            DownloadViewHolder holder = (DownloadViewHolder) v.getTag();
            if (holder == null) {
                return false;
            }
            if (v.getId() == holder.appContentLayout.getId()) {
                if (holder.position == 0 && KeyEvent.KEYCODE_DPAD_UP == keyCode) {
                    return true;
                }
                if (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
                    holder.appControlBtn.requestFocus();
                    return true;
                }
                if (mOnItemEventListener != null) {
                    return mOnItemEventListener.onItemContentKeyListener(v, keyCode, event, holder.position, holder.downloadTask);
                }
            }
            return false;
        }
    }
}
