package com.can.appstore.download;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.can.appstore.R;
import com.can.appstore.eventbus.dispatcher.DownloadDispatcher;

import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;

/**
 * Created by laiforg on 2016/10/31.
 */

public class DownloadPresenterImpl implements DownloadContract.DownloadPresenter {


    public static final String TAG_DOWNLOAD_UPDATA_STATUS="download_update_status";

    private DownloadContract.DownloadView mView;

    private DownloadManager mDownLoadManager;

    private List<DownloadTask> mTasks;
    
    private static final String TAG = "DownloadPresenterImpl";

    public DownloadPresenterImpl(DownloadContract.DownloadView view){
        mView=view;
        mDownLoadManager=DownloadManager.getInstance(mView.getContext());
        mView.setPresenter(this);
    }
    @Override
    public void loadData() {
        mTasks= mDownLoadManager.loadAllTask();
        if(mTasks!=null&&mTasks.size()>0){
            mView.hideNoDataView();
            mView.onDataLoaded(mTasks);
        }else{
            mView.showNoDataView();
        }
    }
    @Override
    public void release() {
        mView=null;
        mDownLoadManager.release();
    }

    @Override
    public void onItemFocused(int focusPos) {
        String rowFmt=String.format("%d/%d行",focusPos+1,mTasks.size());
        int pos= rowFmt.indexOf("/");
        SpannableString ss=new SpannableString(rowFmt);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#EAEAEA")),0,pos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.refreshRowNumber(rowFmt);
    }

    @Override
    public void deleteAllTasks() {
        if(mTasks==null||mTasks.size()==0){
           mView.showToast(R.string.download_no_task);
        }
        DownloadManager downloadManager=DownloadManager.getInstance(mView.getContext().getApplicationContext());
        for (DownloadTask task: mTasks) {
            downloadManager.cancel(task);
        }
        mTasks.clear();
        mView.showNoDataView();
    }

    @Override
    public boolean pauseAllTasks() {
        if(mTasks==null||mTasks.size()==0){
            mView.showToast(R.string.download_no_task);
            return false;
        }
        DownloadManager downloadManager=DownloadManager.getInstance(mView.getContext().getApplicationContext());
        int pauseSize=0;
        for (DownloadTask task: mTasks) {
            if(DownloadStatus.DOWNLOAD_STATUS_COMPLETED==task.getDownloadStatus()){
                continue;
            }
            pauseSize++;
            downloadManager.pause(task);
        }

        if(pauseSize>0){
            DownloadDispatcher.getInstance().postUpdateStatusEvent(TAG,TAG_DOWNLOAD_UPDATA_STATUS);
            return true;
        }
        return false;
    }

    @Override
    public boolean resumeAllTasks() {
        if(mTasks==null||mTasks.size()==0){
            mView.showToast(R.string.download_no_task);
            return false;
        }
        int pauseSize=0;
        DownloadManager downloadManager=DownloadManager.getInstance(mView.getContext().getApplicationContext());
        for (int i=0;i<mTasks.size();i++){
            if(DownloadStatus.DOWNLOAD_STATUS_COMPLETED==mTasks.get(i).getDownloadStatus()){
               continue;
            }
            downloadManager.resume(mTasks.get(i).getId());
            pauseSize++;
        }
        if(pauseSize>0){
            DownloadDispatcher.getInstance().postUpdateStatusEvent(TAG,TAG_DOWNLOAD_UPDATA_STATUS);
            return true;
        }
        return false;
    }

}
