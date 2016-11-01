package com.can.appstore.subject;

import com.can.appstore.subject.model.SubjectInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laiforg on 2016/10/25.
 */

public class SubjectPresenterImpl implements SubjectContract.SubjectPresenter{


    private SubjectContract.SubjectView mView;
    private int pageSize=200;
    private int totalSize=200;
    private List<SubjectInfo> infos;

    public SubjectPresenterImpl(SubjectContract.SubjectView view){
        mView=view;
        mView.setPresenter(this);
    }

    @Override
    public void startLoad() {
        mView.showLoadingDialog();
        infos=new ArrayList<>();
        for (int i=0;i<pageSize;i++){
            SubjectInfo info=new SubjectInfo();
            info.setTitle("教育精选"+i);
            info.setIcon("http://img05.tooopen.com/images/20150202/sy_80219211654.jpg");
            infos.add(info);
        }
        mView.hideLoadingDialog();
        mView.refreshData(infos);
    }

    @Override
    public void loadMore(int lastVisiablePos) {

        if(lastVisiablePos*pageSize<=100||lastVisiablePos*pageSize<=(totalSize%pageSize!=0?(totalSize/pageSize+1):totalSize/pageSize)){
            List<SubjectInfo> data=new ArrayList<>();
            for (int i=pageSize*(lastVisiablePos-1);i<lastVisiablePos*pageSize;i++){
                SubjectInfo info=new SubjectInfo();
                info.setTitle("教育精选"+i);
                info.setIcon("http://img05.tooopen.com/images/20150202/sy_80219211654.jpg");
                data.add(info);
            }
        }else{
            mView.showToast("已经没有数据了！亲！");
        }
    }

    @Override
    public void onItemFocused(int position) {
        int rowNum=position/SubjectActivity.COLUME_COUNT+1;
        String rowFmt=String.format("%d/%d行",rowNum,100/4);
        mView.refreshRowNum(rowFmt);
        if(position+SubjectActivity.COLUME_COUNT>=totalSize){

        }
    }

    @Override
    public void release() {
        mView=null;
    }
}
