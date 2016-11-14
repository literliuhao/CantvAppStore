package com.can.appstore.specialtopic;

import com.can.appstore.R;
import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.SpecialTopic;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by laiforg on 2016/10/25.
 */

public class SpecialPresenterImpl implements SpecialContract.SpecialPresenter{


    private SpecialContract.SubjectView mView;
    private final int pageSize=16;
    private int pageNumber=1;
    private int totalSize;

    private boolean isFocusedLastRow=false;
    private boolean hasMoreData=true;

    private List<SpecialTopic> mSpecialTopics;
    private CanCall<ListResult<SpecialTopic>> mSpecialCall;
    private static final String TAG = "SpecialPresenterImpl";
    public SpecialPresenterImpl(SpecialContract.SubjectView view){
        mView=view;
        mView.setPresenter(this);
    }
    @Override
    public void startLoad() {
        mView.hideRetryView();
        mView.showLoadingDialog();
        mSpecialTopics=new ArrayList<>();
        if(mSpecialCall==null){
            mSpecialCall = HttpManager.getApiService().getSpecialTopics(pageNumber,pageSize);
        }else{
            mSpecialCall=mSpecialCall.clone();
        }
        mSpecialCall.enqueue(new CanCallback<ListResult<SpecialTopic>>() {
            @Override
            public void onResponse(CanCall<ListResult<SpecialTopic>> call, Response<ListResult<SpecialTopic>> response) throws Exception {
                mView.hideLoadingDialog();
                ListResult<SpecialTopic> specialTopics = response.body();
                if(specialTopics==null){
                    //TODO 请求失败处理
                    mView.showRetryView();
                    return;
                }
                if(specialTopics.getStatus()!=0||specialTopics.getData()==null||specialTopics.getData().size()==0){
                    //TODO 没有数据处理
                    mView.showNoDataView();
                    return ;
                }
                totalSize=specialTopics.getTotal();
                mSpecialTopics.addAll(specialTopics.getData());
                mView.refreshData(mSpecialTopics);
                pageNumber++;
            }
            @Override
            public void onFailure(CanCall<ListResult<SpecialTopic>> call, CanErrorWrapper errorWrapper) {
                mView.hideLoadingDialog();
                mView.showRetryView();
            }
        });
    }
    @Override
    public void onItemFocused(int position) {
        int rowNum=position/SpecialActivity.COLUMN_COUNT+1;
        int total=totalSize%SpecialActivity.COLUMN_COUNT>0?totalSize/SpecialActivity.COLUMN_COUNT+1:totalSize/SpecialActivity.COLUMN_COUNT;
        String rowFmt=String.format("%d/%d行",rowNum,total);
        mView.refreshRowNum(rowFmt);
        isFocusedLastRow(position);
    }

    private void isFocusedLastRow(int position){
        boolean condition=(position<=totalSize-1)&&(position>totalSize-1-SpecialActivity.COLUMN_COUNT);
        isFocusedLastRow=condition?true:false;
    }

    @Override
    public void release() {
        if(mSpecialCall!=null){
            mSpecialCall.cancel();
        }
        mView=null;
    }

    @Override
    public void remindNoData(){
        if(isFocusedLastRow&&!hasMoreData){
            mView.showToast("没有更多数据！亲！");
        }
    }

    @Override
    public void loadMore(final int lastVisiablePos) {
        if(lastVisiablePos<mSpecialTopics.size()-1){
            return;
        }
        hasMoreData=true;
        if(lastVisiablePos<totalSize-1){
            mView.showLoadingDialog();
            CanCall<ListResult<SpecialTopic>> specialTopicsCall = HttpManager.getApiService().getSpecialTopics(pageNumber, pageSize);
            specialTopicsCall.enqueue(new CanCallback<ListResult<SpecialTopic>>() {
                @Override
                public void onResponse(CanCall<ListResult<SpecialTopic>> call, Response<ListResult<SpecialTopic>> response) throws Exception {
                    mView.hideLoadingDialog();
                    ListResult<SpecialTopic> specialTopics = response.body();
                    if(specialTopics==null){
                        //TODO 请求失败处理 toast
                        mView.showToast(R.string.load_data_faild);
                        return;
                    }
                    if(specialTopics.getStatus()!=0||specialTopics.getData()==null||specialTopics.getData().size()==0){
                        //TODO 没有数据处理,toast
                        mView.showToast(R.string.load_data_faild);
                        return ;
                    }
                    mSpecialTopics.addAll(specialTopics.getData());
                    mView.onLoadMore(lastVisiablePos+1,mSpecialTopics.size()-1);
                    pageNumber++;
                }
                @Override
                public void onFailure(CanCall<ListResult<SpecialTopic>> call, CanErrorWrapper errorWrapper) {
                    mView.showToast(R.string.load_data_faild);
                }
            });
        }else{
            hasMoreData=false;
        }
    }
}
