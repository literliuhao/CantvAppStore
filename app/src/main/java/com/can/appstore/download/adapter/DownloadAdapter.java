package com.can.appstore.download.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.download.DownloadPresenterImpl;
import com.can.appstore.eventbus.event.DownloadEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.StringUtils;

/**
 * Created by laiforg on 2016/10/31.
 */

public class DownloadAdapter extends CanRecyclerViewAdapter<DownloadTask>{

    private static final String TAG = "DownloadAdapter";
    private OnItemEventListener mOnItemEventListener;
    private List<DownloadTask> data;
    public DownloadAdapter(List<DownloadTask> datas) {
        super(datas);
        data=datas;
    }
    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download_item,parent,false);
        DownloadViewHolder holder=new DownloadViewHolder(itemView);
        holder.eventListener=new ItemEventListener();
        return holder;
    }

    @Override
    protected void bindContentData(DownloadTask task, RecyclerView.ViewHolder holder, int position) {
        DownloadViewHolder viewHolder= (DownloadViewHolder) holder;
        viewHolder.appNameTv.setText(task.getFileName());
        viewHolder.position=position;
        viewHolder.downloadTask=task;
        viewHolder.appContentLayout.setOnFocusChangeListener(viewHolder.eventListener);
        viewHolder.appControlBtn.setOnFocusChangeListener(viewHolder.eventListener);
        viewHolder.appDeleteBtn.setOnFocusChangeListener(viewHolder.eventListener);
        viewHolder.appDeleteBtn.setOnClickListener(viewHolder.eventListener);
        viewHolder.appControlBtn.setOnClickListener(viewHolder.eventListener);
        viewHolder.appContentLayout.setOnClickListener(viewHolder.eventListener);
        viewHolder.appContentLayout.setTag(viewHolder);
        viewHolder.appControlBtn.setTag(viewHolder);
        viewHolder.appDeleteBtn.setTag(viewHolder);
        viewHolder.refreshStatus();
    }



    /**
     *  DownloadViewHolder create by laifrog
     */
    private static class DownloadViewHolder extends RecyclerView.ViewHolder{
        TextView appNameTv,appSizeTv,appDownloadStatusTv;
        ImageView appIconImgVi,appDownloadStatusImgVi;
        ProgressBar appDownloadProgressBar;
        RelativeLayout appControlLayout ,appContentLayout;
        TextView appDeleteBtn,appControlBtn;

        DownloadTask downloadTask;
        ItemEventListener eventListener;
        private DownloadListener downloadListener;
        private Runnable showControlViewRunnable,refreshStatusRunnable,selectedViewRunnable;


        int position=-1;

        private EventBus eventBus;

        public DownloadViewHolder(View itemView) {
            super(itemView);
            init();
        }
        private void init(){
            eventBus=EventBus.getDefault();
            downloadListener=new DownloadListener();
            initView();
            initRunnable();
        }



        private void initView() {
            appNameTv= (TextView) itemView.findViewById(R.id.download_item_title_tv);
            appSizeTv= (TextView) itemView.findViewById(R.id.download_item_size_tv);
            appIconImgVi= (ImageView) itemView.findViewById(R.id.download_item_appicon_imgvi);
            appDownloadProgressBar= (ProgressBar) itemView.findViewById(R.id.download_item_progress);
            appDownloadStatusImgVi= (ImageView) itemView.findViewById(R.id.download_item_status_imgvi);
            appDownloadStatusTv= (TextView) itemView.findViewById(R.id.download_item_status_tv);
            appControlLayout= (RelativeLayout) itemView.findViewById(R.id.download_item_control_rlayout);
            appContentLayout= (RelativeLayout) itemView.findViewById(R.id.download_item_content_rlayout);
            appDeleteBtn= (TextView) itemView.findViewById(R.id.download_item_delete_btn);
            appControlBtn= (TextView) itemView.findViewById(R.id.download_item_control_btn);
            itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    initDownloadListener();
                    eventBus.register(DownloadViewHolder.this);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    downloadTask.removeDownloadListener(downloadListener);
                    v.removeCallbacks(showControlViewRunnable);
                    v.removeCallbacks(selectedViewRunnable);
                    eventBus.unregister(DownloadViewHolder.this);

                }
            });
        }

        public void  initDownloadListener(){
            if(downloadTask!=null){
                downloadTask.removeDownloadListener(downloadListener);
                downloadTask.addDownloadListener(downloadListener);
            }
        }


        private void initRunnable(){

            showControlViewRunnable=new Runnable() {
                @Override
                public void run() {
                    appControlLayout.setVisibility(View.VISIBLE);
                }
            };

            refreshStatusRunnable=new Runnable() {
                @Override
                public void run() {
                    refreshControlButtonStatus();
                    refreshDownloadStatus();
                }
            };

            selectedViewRunnable=new Runnable() {
                @Override
                public void run() {
                    appContentLayout.setSelected(true);
                }
            };
        }

        /**
         * item显示右侧controlview
         */
        private void showControlViewDelay(){
            itemView.postDelayed(showControlViewRunnable,50);
        }

        /**
         * item隐藏右侧controlview
         */
        public void hidecotrolView(){
            itemView.removeCallbacks(showControlViewRunnable);
            appControlLayout.setVisibility(View.GONE);
        }

        /**
         * 设置item左侧selected选中状态
         */
        public void setContentLayoutSelected(){
            itemView.postDelayed(selectedViewRunnable,50);
        }

        /**
         * 设置item左侧Unselected选中状态
         */
        public void setContentLayoutUnselected(){
            itemView.removeCallbacks(selectedViewRunnable);
            appContentLayout.setSelected(false);
        }

        public void refreshStatus(){
            itemView.post(refreshStatusRunnable);
        }
        /**
         * 更新控制按钮状态
         */
        private void refreshControlButtonStatus(){
            if(downloadTask==null){
                return;
            }
            switch (downloadTask.getDownloadStatus()){
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
                    appControlBtn.setText("重试");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                    appControlBtn.setText("安装");
                    break;
            }
        }
        /**
         * 更新状态图标 进度 。。。。
         */
        private void refreshDownloadStatus(){
            if(downloadTask==null){
                return;
            }
            switch (downloadTask.getDownloadStatus()){
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
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_download_fail);
                    appDownloadStatusTv.setText("失败");
                    break;
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                    appDownloadStatusImgVi.setImageResource(R.mipmap.icon_download_finish);
                    appDownloadStatusTv.setText("下载成功");
                    break;
            }
            appDownloadProgressBar.setProgress((int)downloadTask.getPercent());
            appSizeTv.setText(StringUtils.formatFileSize(downloadTask.getCompletedSize(),false)+
                    "/"+StringUtils.formatFileSize(downloadTask.getTotalSize(),false));
        }

        private class DownloadListener implements DownloadTaskListener {
            @Override
            public void onPrepare(DownloadTask downloadTask) {
                Log.i(TAG, "onPrepare: id="+downloadTask.getId());
                refreshStatus();
            }

            @Override
            public void onStart(DownloadTask downloadTask) {
                Log.i(TAG, "onPrepare: compliteSize="+downloadTask.getCompletedSize());
                refreshStatus();
            }

            @Override
            public void onDownloading(DownloadTask downloadTask) {
                Log.i(TAG, "onDownloading: downloadTastSize= "+downloadTask.getCompletedSize());
                refreshStatus();
            }
            @Override
            public void onPause(DownloadTask downloadTask) {
                Log.i(TAG, "onPause: ");
                refreshStatus();
            }
            @Override
            public void onCancel(DownloadTask downloadTask) {
                refreshStatus();
                Log.i(TAG, "onCancel: ");
            }

            @Override
            public void onCompleted(DownloadTask downloadTask) {
                Log.i(TAG, "onCompleted: ");
                refreshStatus();
            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {
                Log.i(TAG, "onError: ");
                refreshStatus();
            }
        }
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onDownloadEvent(DownloadEvent event){
            Log.i(TAG, "onDownloadEvent: DownloadEvent="+event.toString());
            if(DownloadPresenterImpl.TAG_DOWNLOAD_UPDATA_STATUS.equals(event.getTag())
                    &&DownloadEvent.DOWNLOADEVENT_UPDATE_LIST_STATUS==event.getEventType()){
                refreshStatus();
            }
        }
    }

    public void setOnItemEventListener(OnItemEventListener listener){
        this.mOnItemEventListener=listener;
    }
    public interface OnItemEventListener{
        void onItemContentFocusChanged(View view,boolean hasFocus,int pos);
        void onItemControlButtonFocusChanged(View view,boolean hasFocus,int pos,DownloadTask downloadTask);
        void onItemDeleteButtonFocusChanged(View view,boolean hasFocus,int pos,DownloadTask downloadTask);
        void onItemContentClick(View view,int pos,DownloadTask downloadTask);
        void onControlButtonClick(View view,int pos,DownloadTask downloadTask);
        void onDeleteButtonClick(View view,int pos,DownloadTask downloadTask);
    }

    /**
     * @author laiforg
     * item  View.OnFocusChangeListener,View.OnClickListener的实现类
     */
    public class ItemEventListener implements View.OnFocusChangeListener,View.OnClickListener{

        @Override
        public void onClick(View v) {
            DownloadViewHolder holder= (DownloadViewHolder) v.getTag();
            if(holder==null){
                return;
            }
            if(v.getId()==holder.appContentLayout.getId()){
                if(mOnItemEventListener!=null){
                    mOnItemEventListener.onItemContentClick(v,holder.position,holder.downloadTask);
                }

            }else if(v.getId()==holder.appControlBtn.getId()){

                switch (holder.downloadTask.getDownloadStatus()){
                    case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                    case DownloadStatus.DOWNLOAD_STATUS_INIT:
                    case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
                    case DownloadStatus.DOWNLOAD_STATUS_START:
                        DownloadManager.getInstance(v.getContext().
                                getApplicationContext()).pause(holder.downloadTask);
                        break;
                    case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                        DownloadManager.getInstance(v.getContext().
                                getApplicationContext()).resume(holder.downloadTask.getId());
                        break;
                }
                //考虑到未执行的任务从init状态调pause状态时不会回掉，故此时手动刷新一次UI
                holder.refreshStatus();
                if(mOnItemEventListener!=null){
                    mOnItemEventListener.onControlButtonClick(v,holder.position,holder.downloadTask);
                }
            }else if(v.getId()==holder.appDeleteBtn.getId()){
                DownloadManager.getInstance(v.getContext().getApplicationContext()).cancel(holder.downloadTask);
                data.remove(holder.position);
                notifyItemRemoved(holder.position);
                if(mOnItemEventListener!=null){
                    mOnItemEventListener.onDeleteButtonClick(v,holder.position,holder.downloadTask);
                }
            }
        }
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            DownloadViewHolder holder= (DownloadViewHolder) v.getTag();
            if(holder==null){
                return;
            }
            if(!holder.appContentLayout.hasFocus()
                    &&!holder.appDeleteBtn.hasFocus()
                    &&!holder.appControlBtn.hasFocus()){
                holder.hidecotrolView();
            }
            if(v.getId()==holder.appContentLayout.getId()){
                if(hasFocus){
                    holder.showControlViewDelay();
                    holder.setContentLayoutSelected();
                }else{
                    holder.setContentLayoutUnselected();
                }
                if(mOnItemEventListener!=null){
                    mOnItemEventListener.onItemContentFocusChanged(v,hasFocus,holder.position);
                }
            }else if(v.getId()==holder.appControlBtn.getId()){
                if(hasFocus){
                    holder.appControlLayout.setVisibility(View.VISIBLE);
                }
                if(mOnItemEventListener!=null){
                    mOnItemEventListener.onItemControlButtonFocusChanged(v,hasFocus,holder.position,holder.downloadTask);
                }
            }else if(v.getId()==holder.appDeleteBtn.getId()){
                if(hasFocus){
                    holder.appControlLayout.setVisibility(View.VISIBLE);
                }
                if(mOnItemEventListener!=null){
                    mOnItemEventListener.onItemDeleteButtonFocusChanged(v,hasFocus,holder.position,holder.downloadTask);
                }
            }
        }
    }
}
