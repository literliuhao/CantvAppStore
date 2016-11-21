package com.can.appstore.applist;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.AppInfoContainer;
import com.can.appstore.entity.Result;
import com.can.appstore.entity.Topic;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.ToastUtils;
import retrofit2.Response;

/**
 * Created by syl on 2016/10/19.
 */

public class AppListPresenter implements AppListContract.Presenter {
    private static final String TAG = "AppListPresenter";
    public static final int REFRESH_APP_LIST = 1;
    public static final int PAGE_SIZE = 18;   //每次加载请求的总App数
    public static final int REFRESH_APP = 0;  //整个刷新adpter
    public static final int REQUEST_DELAY = 500;  //请求延迟时间
    private CanCall<Result<AppInfoContainer>> mAppListInfoCall;
    private CanCall<Result<AppInfoContainer>> mAppsRanking;
    private AppListContract.View mView;
    private Handler mHandler;
    private Context mContext;
    private int mPageType;
    private String mTypeId;
    private String mTopicId;
    private List<Topic> mTopics;
    private List<AppInfo> mAppInfos;
    private int mTotalLine;
    private int mCurrentLine;
    private int mMenuDataPosition;
    private int mPage;
    private int mTotalSize;

    public AppListPresenter(AppListContract.View view, int pageType, String typeId, String topicId) {
        mView = view;
        mContext = view.getContext();
        mPageType = pageType;
        // TODO: 2016/11/10 测试使用type  后期删除
        //mTypeId = typeId;
        mTypeId = "5";
        mTopicId = topicId;
        mView.setPresenter(this);
        mTopics = new ArrayList<>();
        mAppInfos = new ArrayList<>();
        initHandler();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == REFRESH_APP_LIST) {
                    if (mMenuDataPosition == msg.arg1) {
                        mView.hideLoadingDialog();
                    } else {
                        mMenuDataPosition = msg.arg1;
                        loadAppListData(mTopics.get(mMenuDataPosition).getId());
                    }
                }
            }
        };
    }

    /**
     * 第一次进入请求数据
     */
    @Override
    public void startLoadData() {
        mPage = 1;
        mCurrentLine = 1;

        //初始化请求数据回调
        CanCallback<Result<AppInfoContainer>> canCallback = new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                mPage++;
                mMenuDataPosition = 0;//第一次加载完数据，找到需要获取焦点的位置
                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                mTotalSize = data.getTotal();
                String typeName = data.getTypeName();
                List<Topic> topics = data.getTopics();
                List<AppInfo> appInfos = data.getData();
                mTopics.addAll(topics);
                mAppInfos.addAll(appInfos);
                for (int i = 0; i < 6; i++) {
                    Topic topic = new Topic();
                    topic.setName(i + "");
                    mTopics.add(topic);
                }
                mView.refreshTypeName(typeName);
                mView.refreshMenuList(mTopics, mMenuDataPosition);
                // TODO: 2016/11/11 重载
                mView.refreshAppList(mAppInfos, REFRESH_APP);
                //计算总行数
                mTotalLine = calculateRowNumber(mTotalSize);
                refreshLineInformation();
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                // TODO: 2016/11/11  具体
                //mView.changeAppInfoUiToFail();
                mView.finish();
                ToastUtils.showMessage(mContext, "网络连接错误，请检查网络");
                Log.d(TAG, "onFailure:" + errorWrapper.getReason() + "-----" + errorWrapper.getThrowable());
            }
        };


        //根据不同页面请求数据
        if (mPageType == AppListActivity.PAGE_TYPE_APP_LIST) {
            mAppListInfoCall = HttpManager.getApiService().getAppinfos("", mTypeId, mPage, PAGE_SIZE);
            mAppListInfoCall.enqueue(canCallback);
        } else {
            // TODO: 2016/11/17 测试 id  页面对接时修改
            mAppsRanking = HttpManager.getApiService().getAppsRanking("15");
            mAppsRanking.enqueue(canCallback);
        }


    }

    /**
     * menu 位置改变请求数据
     */
    @Override
    public void loadAppListData(String topicId) {
        mPage = 1;
        mAppInfos.clear();


        CanCallback<Result<AppInfoContainer>> canCallback = new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                // TODO: 2016/11/11
                //初始化分页信息
                mPage = 2;
                mCurrentLine = 1;

                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                List<AppInfo> appInfos = data.getData();
                mAppInfos.addAll(appInfos);
                mView.hideLoadingDialog();
                mView.refreshAppList(mAppInfos, REFRESH_APP);
                //计算总行数
                mTotalLine = calculateRowNumber(mTotalSize);
                refreshLineInformation();
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                mView.changeAppInfoUiToFail();
                Log.d(TAG, "onFailure:" + errorWrapper.getReason() + "-----" + errorWrapper.getThrowable());
            }
        };

        if (mPageType == AppListActivity.PAGE_TYPE_APP_LIST) {
            if (mAppListInfoCall != null) {
                mAppListInfoCall.cancel();
            }
            mAppListInfoCall = HttpManager.getApiService().getAppinfos(topicId, mTypeId, mPage, PAGE_SIZE);
            mAppListInfoCall.enqueue(canCallback);
        } else {
            if (mAppsRanking != null) {
                mAppsRanking.cancel();
            }
            mAppsRanking = HttpManager.getApiService().getAppsRanking(topicId);
            mAppsRanking.enqueue(canCallback);
        }

    }

    @Override
    public void loadMoreData() {
        int mCurrDataCount = mAppInfos.size();
        if (mCurrDataCount >= mTotalSize) {
            mView.showToast("没有更多了");
            return;
        }

        mView.showToast("加载更多！");
        if (mAppListInfoCall != null) {
            mAppListInfoCall.cancel();
        }
        mAppListInfoCall = HttpManager.getApiService().getAppinfos(mTopics.get(mMenuDataPosition).getId(), "5", mPage,
                PAGE_SIZE);
        mAppListInfoCall.enqueue(new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                List<AppInfo> eAppInfos = data.getData();
                mAppInfos.addAll(eAppInfos);
                mView.refreshAppList(mAppInfos, (mPage - 1) * PAGE_SIZE); // TODO: 2016/10/21  请求的数据为空数据不足  更新total
                refreshLineInformation();
                mPage++;//请求成功页数加1
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                Log.d(TAG, "loadMoreData,onFailure:");
            }
        });
    }


    /**
     * 把所在位置或者App总数换算为所在行数或者总行数
     *
     * @param number
     * @return 行数`
     */
    private int calculateRowNumber(int number) {
        int lines;
        if (number % 3 == 0) {
            lines = number / 3;
        } else {
            lines = number / 3 + 1;
        }
        return lines;
    }

    @Override
    public void onAppListItemSelectChanged(int position) {
        mCurrentLine = calculateRowNumber(position + 1);
        refreshLineInformation();
    }

    /**
     * 请求数据未成功的时候再次请求数据
     */
    public void loadAppListData() {
        loadAppListData(mTopics.get(mMenuDataPosition).getId());
    }

    @Override
    public void onMenuItemSelect(int position) {
        if (mHandler != null) {
            mHandler.removeMessages(REFRESH_APP_LIST);
            Message msg = Message.obtain();
            msg.what = REFRESH_APP_LIST;
            msg.arg1 = position;
            mHandler.sendMessageDelayed(msg, REQUEST_DELAY);
        }
    }

    /**
     * 刷新右上角行数显示信息
     */
    private void refreshLineInformation() {
        StringBuilder crowNumber = new StringBuilder();
        crowNumber.append(mCurrentLine);
        crowNumber.append(" / ");
        crowNumber.append(mTotalLine);
        crowNumber.append("行");
        SpannableStringBuilder spannable = new SpannableStringBuilder(crowNumber);
        int currentLineTextLength = String.valueOf(mCurrentLine).length();
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, currentLineTextLength, Spannable
                .SPAN_EXCLUSIVE_INCLUSIVE);
        mView.refreshRowNumber(spannable);
    }

    /**
     * 释放view引用，关闭没有完成的网络请求
     */
    @Override
    public void release() {
        mView = null;
        if (mAppListInfoCall != null) {
            mAppListInfoCall.cancel();
        }
        if (mAppsRanking != null) {
            mAppsRanking.cancel();
        }
    }
}
