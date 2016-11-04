package com.can.appstore.subject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.can.appstore.subject.model.SubjectInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laiforg on 2016/10/25.
 */

public class SubjectPresenterImpl implements SubjectContract.SubjectPresenter{


    private SubjectContract.SubjectView mView;
    private int pageSize=40;
    private int totalSize=200;
    private List<SubjectInfo> infos;
    private boolean hasMoreData=true;
    private boolean isFocusedLastRow=false;

    private Handler mHandler;

    private static final int LOAD_SUCCESS_CALLBACK=0x1;
    private static final int LOAD_Failed_CALLBACK=0x2;
    private static final int LOAD_MORE_SUCCESS_CALLBACK=0x3;

    public SubjectPresenterImpl(SubjectContract.SubjectView view){
        mView=view;
        mView.setPresenter(this);
        initHandler();
    }

    public void initHandler(){
        mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case LOAD_SUCCESS_CALLBACK:
                        infos.addAll((List<SubjectInfo>)msg.obj);
                        mView.hideLoadingDialog();
                        mView.refreshData(infos);
                        break;
                    case LOAD_MORE_SUCCESS_CALLBACK:
                        infos.addAll((List<SubjectInfo>)msg.obj);
                        mView.hideLoadingDialog();
                        mView.onLoadMore(msg.arg1,infos.size()-1);
                        break;
                }
            }
        };
    }

    @Override
    public void startLoad() {
        mView.showLoadingDialog();
        infos=new ArrayList<>();
        loadData();
    }
    @Override
    public void loadMore(int lastVisiablePos) {
        if(lastVisiablePos<infos.size()-1){
            return;
        }
        hasMoreData=true;
        if(lastVisiablePos<totalSize-1){
            mView.showLoadingDialog();
            startLoadMore(lastVisiablePos);
        }else{
            hasMoreData=false;
        }
    }

    @Override
    public void onItemFocused(int position) {
        int rowNum=position/SubjectActivity.COLUMN_COUNT+1;
        String rowFmt=String.format("%d/%d行",rowNum,totalSize/SubjectActivity.COLUMN_COUNT);
        mView.refreshRowNum(rowFmt);
        isFocusedLastRow(position);
    }

    private void isFocusedLastRow(int position){
        boolean condition=(position<=totalSize-1)&&(position>totalSize-1-SubjectActivity.COLUMN_COUNT);
        isFocusedLastRow=condition?true:false;
    }

    @Override
    public void release() {
        mHandler.removeCallbacksAndMessages(null);
        mView=null;
    }

    @Override
    public void remindNoData(){
        if(isFocusedLastRow&&!hasMoreData){
            mView.showToast("没有更多数据！亲！");
        }
    }

    private  void startLoadMore(final int lastVisiablePos){
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<SubjectInfo> infos=new ArrayList<>();
                for (int i=lastVisiablePos+1;i<lastVisiablePos+1+pageSize&&i<totalSize;i++){
                    SubjectInfo info=new SubjectInfo();
                    info.setTitle("教育精选"+i);
                    info.setIcon("http://img05.tooopen.com/images/20150202/sy_80219211654.jpg");
                    infos.add(info);
                }
                Message msg=Message.obtain();
                msg.what=LOAD_MORE_SUCCESS_CALLBACK;
                msg.obj=infos;
                msg.arg1=lastVisiablePos+1;
                mHandler.sendMessage(msg);
            }
        }.start();
    }


    private void loadData(){
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<SubjectInfo>  infos=new ArrayList<SubjectInfo>();
                for (int i=0;i<pageSize;i++){
                    SubjectInfo info=new SubjectInfo();
                    info.setTitle("教育精选"+i);
                    info.setIcon("http://img05.tooopen.com/images/20150202/sy_80219211654.jpg");
                    infos.add(info);
                }
                Message msg=Message.obtain();
                msg.obj=infos;
                msg.what=LOAD_SUCCESS_CALLBACK;
                mHandler.sendMessage(msg);
            }
        }.start();
    }
}
