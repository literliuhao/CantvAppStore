package com.can.appstore.download;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.io.File;
import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.tvlib.utils.FileUtils;

/**
 * Created by laiforg on 2016/10/31.
 */

public class DownloadPresenterImpl implements DownloadContract.DownloadPresenter {

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
        DownloadManager downloadManager=DownloadManager.getInstance(mView.getContext().getApplicationContext());
        for (DownloadTask task: mTasks) {
            downloadManager.cancel(task);
        }
        new Thread(){
            @Override
            public void run() {
                for (DownloadTask task: mTasks) {
                    FileUtils.deleteFile(task.getSaveDirPath()+ File.separator+task.getFileName());
                }
            }
        }.start();
        mView.showNoDataView();
    }

    @Override
    public void pauseAllTasks() {
        DownloadManager downloadManager=DownloadManager.getInstance(mView.getContext().getApplicationContext());
        List<DownloadTask> tasks=downloadManager.loadAllTask();
        for (DownloadTask task: tasks) {
            if(DownloadStatus.DOWNLOAD_STATUS_COMPLETED==task.getDownloadStatus()){
                continue;
            }
            downloadManager.pause(task);
        }
    }

    @Override
    public void resumeAllTasks() {
        DownloadManager downloadManager=DownloadManager.getInstance(mView.getContext().getApplicationContext());
        List<DownloadTask> tasks=downloadManager.loadAllTask();
        for (int i=0;i<tasks.size();i++){
            if(DownloadStatus.DOWNLOAD_STATUS_COMPLETED==tasks.get(i).getDownloadStatus()){
               continue;
            }
            downloadManager.resume(tasks.get(i).getId());
        }
        mView.onDataLoaded(tasks);
    }

}
