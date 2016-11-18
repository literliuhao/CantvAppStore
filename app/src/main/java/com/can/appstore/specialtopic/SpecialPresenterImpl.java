package com.can.appstore.specialtopic;

import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

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

public class SpecialPresenterImpl implements SpecialContract.SpecialPresenter {

    private static final String TAG = "SpecialPresenterImpl";
    public static final int DELAY_MILLIS = 1000;

    private final int pageSize = 16;
    private int pageNumber = 1;
    private int totalSize;

    private boolean isFocusedLastRow = false;

    private List<SpecialTopic> mSpecialTopics;
    private CanCall<ListResult<SpecialTopic>> mSpecialCall;

    private SpecialContract.SubjectView mView;
    private Handler mHandler;

    public SpecialPresenterImpl(SpecialContract.SubjectView view) {
        mView = view;
        mView.setPresenter(this);
        mHandler = new Handler();
    }

    @Override
    public void startLoad() {
        mView.hideRetryView();
        mView.showLoadingDialog();
        mSpecialTopics = new ArrayList<>();
        if (mSpecialCall == null) {
            mSpecialCall = HttpManager.getApiService().getSpecialTopics(pageNumber, pageSize);
        } else {
            mSpecialCall = mSpecialCall.clone();
        }
        mSpecialCall.enqueue(new CanCallback<ListResult<SpecialTopic>>() {
            @Override
            public void onResponse(CanCall<ListResult<SpecialTopic>> call, final Response<ListResult<SpecialTopic>> response) throws Exception {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mView.hideLoadingDialog();
                        ListResult<SpecialTopic> specialTopics = response.body();
                        if (specialTopics == null) {
                            mView.showRetryView();
                            return;
                        }
                        if (specialTopics.getData() == null || specialTopics.getData().size() == 0) {
                            mView.showNoDataView();
                            return;
                        }
                        totalSize = specialTopics.getTotal();
                        mSpecialTopics.addAll(specialTopics.getData());
                        mView.refreshData(mSpecialTopics);
                        pageNumber++;
                    }
                }, DELAY_MILLIS);
            }

            @Override
            public void onFailure(CanCall<ListResult<SpecialTopic>> call, CanErrorWrapper errorWrapper) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mView.hideLoadingDialog();
                        mView.showRetryView();
                    }
                }, DELAY_MILLIS);
            }
        });
    }

    @Override
    public void onItemFocused(int position) {
        int rowNum = position / SpecialActivity.COLUMN_COUNT + 1;
        int total = totalSize % SpecialActivity.COLUMN_COUNT > 0 ? totalSize / SpecialActivity.COLUMN_COUNT + 1 : totalSize / SpecialActivity.COLUMN_COUNT;
        String rowFmt = String.format("%d/%d行", rowNum, total);
        int pos = rowFmt.indexOf("/");
        SpannableString spanString = new SpannableString(rowFmt);
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#EAEAEA")), 0, pos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mView.refreshRowNum(spanString);
        isFocusedLastRow(position);
    }

    private void isFocusedLastRow(int position) {
        boolean condition = (position <= totalSize - 1) && (position > totalSize - 1 - SpecialActivity.COLUMN_COUNT);
        isFocusedLastRow = condition ? true : false;
    }

    @Override
    public void release() {
        if (mSpecialCall != null) {
            mSpecialCall.cancel();
        }
        mView = null;
    }

    @Override
    public void remindNoData() {
        if (isFocusedLastRow && !hasMoreData()) {
            mView.showToast(R.string.no_more_data);
        }
    }

    public boolean hasMoreData() {
        return mSpecialTopics.size() >= totalSize ? false : true;
    }

    @Override
    public void loadMore(final int lastVisiablePos) {
        //此处是滑到已经加载的数据的最后一行是检测是否需要加载更多
        if (lastVisiablePos < mSpecialTopics.size() - 1) {
            return;
        }
        if (lastVisiablePos < totalSize - 1) {
            mView.showLoadingDialog();
            CanCall<ListResult<SpecialTopic>> specialTopicsCall = HttpManager.getApiService().getSpecialTopics(pageNumber, pageSize);
            specialTopicsCall.enqueue(new CanCallback<ListResult<SpecialTopic>>() {
                @Override
                public void onResponse(CanCall<ListResult<SpecialTopic>> call, final Response<ListResult<SpecialTopic>> response) throws Exception {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mView.hideLoadingDialog();
                            ListResult<SpecialTopic> specialTopics = response.body();
                            if (specialTopics == null) {
                                mView.showToast(R.string.load_data_faild);
                                return;
                            }
                            if (specialTopics.getStatus() != 0 || specialTopics.getData() == null || specialTopics.getData().size() == 0) {
                                mView.showToast(R.string.load_data_faild);
                                return;
                            }
                            mSpecialTopics.addAll(specialTopics.getData());
                            mView.onLoadMore(lastVisiablePos + 1, mSpecialTopics.size() - 1);
                            pageNumber++;
                        }
                    }, DELAY_MILLIS);
                }

                @Override
                public void onFailure(CanCall<ListResult<SpecialTopic>> call, CanErrorWrapper errorWrapper) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mView.hideLoadingDialog();
                            mView.showToast(R.string.load_data_faild);
                        }
                    }, DELAY_MILLIS);
                }
            });
        }
    }
}
