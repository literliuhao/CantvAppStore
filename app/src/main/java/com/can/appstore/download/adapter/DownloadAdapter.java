package com.can.appstore.download.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;

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
        Log.i(TAG, "generateViewHolder: ");
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download_item,parent,false);
        DownloadViewHolder holder=new DownloadViewHolder(itemView);
        holder.eventListener=new ItemEventListener();
        return holder;
    }

    @Override
    protected void bindContentData(DownloadTask task, RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "bindContentData: ");
        DownloadViewHolder viewHolder= (DownloadViewHolder) holder;
        viewHolder.appNameTv.setText(task.getFileName());
        viewHolder.appSizeTv.setText(StringUtils.formatFileSize(task.getCompletedSize(),false)+"/"+StringUtils.formatFileSize(task.getTotalSize(),false));
        viewHolder.appDownloadProgressBar.setProgress((int)task.getPercent());
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
        refreshDownloadStatus(task.getDownloadStatus(),viewHolder.appDownloadStatusImgVi,viewHolder.appDownloadStatusTv);
        refreshControlStatus(task.getDownloadStatus(),viewHolder.appControlBtn);
    }
    private static void refreshControlStatus(int downloadStatus,Button controlButton){
        switch (downloadStatus){
            case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
            case DownloadStatus.DOWNLOAD_STATUS_INIT:
            case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
            case DownloadStatus.DOWNLOAD_STATUS_START:
                controlButton.setText("暂停");
                break;
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                controlButton.setText("继续");
                break;
            case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                controlButton.setText("重试");
                break;
        }
    }

    private static void refreshDownloadStatus(int downloadStatus, ImageView statusImgvi, TextView statusText){
        switch (downloadStatus){
            case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                statusImgvi.setImageResource(R.mipmap.icon_downloading);
                statusText.setText("下载中");
                break;
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                statusImgvi.setImageResource(R.mipmap.icon_download_pause);
                statusText.setText("已暂停");
                break;
            case DownloadStatus.DOWNLOAD_STATUS_INIT:
            case DownloadStatus.DOWNLOAD_STATUS_PREPARE:
            case DownloadStatus.DOWNLOAD_STATUS_START:
                statusImgvi.setImageResource(R.mipmap.icon_download_wait);
                statusText.setText("等待中");
                break;
            case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                statusImgvi.setImageResource(R.mipmap.icon_download_fail);
                statusText.setText("失败");
                break;
            case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                statusImgvi.setImageResource(R.mipmap.icon_download_finish);
                statusText.setText("下载成功");
                break;
        }
    }


    /**
     *  DownloadViewHolder create by laifrog
     */
    private static class DownloadViewHolder extends RecyclerView.ViewHolder{
        TextView appNameTv,appSizeTv,appDownloadStatusTv;
        ImageView appIconImgVi,appDownloadStatusImgVi;
        ProgressBar appDownloadProgressBar;
        RelativeLayout appControlLayout ,appContentLayout;
        Button appDeleteBtn,appControlBtn;

        DownloadTask downloadTask;
        ItemEventListener eventListener;
        int position=-1;
        private DownloadListener downloadListener;

        public DownloadViewHolder(View itemView) {
            super(itemView);
            initView();
            downloadListener=new DownloadListener();
            Log.i(TAG, "DownloadViewHolder: structor");
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
            appDeleteBtn= (Button) itemView.findViewById(R.id.download_item_delete_btn);
            appControlBtn= (Button) itemView.findViewById(R.id.download_item_control_btn);
            itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    Log.i(TAG, "onViewAttachedToWindow: ");
                    initDownloadListener();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    Log.i(TAG, "onViewDetachedFromWindow: ");
                    downloadTask.removeDownloadListener(downloadListener);
                }
            });
        }

        public void  initDownloadListener(){
            if(downloadTask!=null){
                downloadTask.removeDownloadListener(downloadListener);
                downloadTask.addDownloadListener(downloadListener);
            }
        }

        private class DownloadListener implements DownloadTaskListener{

            @Override
            public void onPrepare(DownloadTask downloadTask) {
                Log.i(TAG, "onPrepare: id="+downloadTask.getId());
                refreshStatus(downloadTask);

            }

            @Override
            public void onStart(DownloadTask downloadTask) {
                Log.i(TAG, "onPrepare: compliteSize="+downloadTask.getCompletedSize());
                refreshStatus(downloadTask);
            }

            @Override
            public void onDownloading(DownloadTask downloadTask) {
                Log.i(TAG, "onDownloading: downloadTastSize= "+downloadTask.getCompletedSize());
                refreshStatus(downloadTask);
            }
            @Override
            public void onPause(DownloadTask downloadTask) {
                Log.i(TAG, "onPause: ");
                refreshStatus(downloadTask);
            }
            @Override
            public void onCancel(DownloadTask downloadTask) {
                Log.i(TAG, "onCancel: ");
            }

            @Override
            public void onCompleted(DownloadTask downloadTask) {
                Log.i(TAG, "onCompleted: ");
                refreshStatus(downloadTask);
            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {
                Log.i(TAG, "onError: ");
                refreshStatus(downloadTask);
            }

            private void refreshStatus(final DownloadTask task){
                itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshDownloadStatus(task.getDownloadStatus(),appDownloadStatusImgVi,appDownloadStatusTv);
                        appDownloadProgressBar.setProgress((int)downloadTask.getPercent());
                        appSizeTv.setText(StringUtils.formatFileSize(downloadTask.getCompletedSize(),false)+
                                "/"+StringUtils.formatFileSize(downloadTask.getTotalSize(),false));
                        refreshControlStatus(task.getDownloadStatus(),appControlBtn);
                    }
                });
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
//                        holder.downloadTask= DownloadManager.getInstance(v.getContext().
//                                getApplicationContext()).resume(holder.downloadTask.getId());
//                        holder.initDownloadListener();

//                        //因为resume时，如果是从数据库读取的话，返回重新创建的对象
//                        try {
//                            for (int i=0;i<data.size();i++){
//                                if(holder.downloadTask.getId()==data.get(i).getId()){
//                                    data.add(i,holder.downloadTask);
//                                    break;
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        break;
                }
                //考虑到未执行的任务从init状态调pause状态时不会回掉，故此时手动刷新一次UI
                refreshControlStatus(holder.downloadTask.getDownloadStatus(),holder.appControlBtn);
                refreshDownloadStatus(holder.downloadTask.getDownloadStatus(),holder.appDownloadStatusImgVi,holder.appDownloadStatusTv);
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
                holder.appControlLayout.setVisibility(View.GONE);
            }
            if(v.getId()==holder.appContentLayout.getId()){
                if(hasFocus){
                    holder.appControlLayout.setVisibility(View.VISIBLE);
                    holder.appContentLayout.setSelected(true);
                }else{
                    holder.appContentLayout.setSelected(false);
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
