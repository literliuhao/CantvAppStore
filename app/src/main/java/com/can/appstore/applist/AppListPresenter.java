package com.can.appstore.applist;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.AppInfoContainer;
import com.can.appstore.entity.Result;
import com.can.appstore.entity.Topic;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.ToastUtils;
import retrofit2.Response;

/**
 * Created by syl on 2016/10/19.
 */

public class AppListPresenter implements AppListContract.Presenter {
    private static final String TAG = "AppListPresenter";
    //常量
    public static final int PAGE_SIZE = 18;   //每次加载请求的总App数
    public static final int REFRESH_APP = 0;  //整个刷新adpter
    public static final int REQUEST_DELAY = 1000;  //请求延迟时间
    //handler msg.what
    public static final int REFRESH_APP_LIST = 1;
    public static final int HIDE_LOADING = 2;
    //联网请求相关
    private CanCall<Result<AppInfoContainer>> mAppListInfoCall;
    private CanCall<Result<AppInfoContainer>> mAppsRanking;
    //数据
    private int mPageType;//当前页类型
    private String mTypeId;//页面类型id
    private String mTopicId;//左侧menu列表id
    private List<Topic> mTopics;//左侧列表数据
    private List<AppInfo> mAppInfos;//右侧列表数据
    private int mTotalLine;//当前应用列表总行数
    private int mCurrentLine;//当前焦点所在应用列表行数
    private int mMenuDataPosition;//左侧menu请求数据的位置
    private int mPage;//应用列表分页请求当前请求的的页数
    private int mTotalSize;//应用列表item总个数
    private long mRecentLoadingTime;//最近一次loading框显示的时间
    //标识
    private boolean isFirstFocus = true;//应用列表是否是第一次得到焦点
    //其他
    private AppListContract.View mView;
    private Handler mHandler;
    private Context mContext;
    private Runnable mLoadFailRunable;

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

                switch (msg.what) {
                    case REFRESH_APP_LIST:
                        if (mMenuDataPosition == msg.arg1) {
                            mView.hideLoadingDialog();
                            //显示隐藏的UI
                            noLoadShowHideUI();
                        } else {
                            //消除正在延时中还没有执行的runable
                            mHandler.removeCallbacks(mLoadFailRunable);

                            mMenuDataPosition = msg.arg1;
                            loadAppListData(mTopics.get(mMenuDataPosition).getId());
                        }
                        break;
                    case HIDE_LOADING:
                        mView.hideOffsetLoadingDialog();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 第一次进入请求数据
     */
    @Override
    public void startLoadData() {

        if (!NetworkUtils.isNetworkConnected(mContext)) {
            ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.no_network));
            mView.finish();
            return;
        }

        mPage = 1;
        mView.showLoadingDialog();
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

                //处理页面显示
                mView.hideLoadingDialog();
                if (mPageType == AppListActivity.PAGE_TYPE_APP_LIST) {
                    mView.showSearchView();
                }
                mView.refreshTypeName(typeName);
                mView.refreshMenuList(mTopics, mMenuDataPosition);
                // TODO: 2016/11/11 重载
                mView.refreshAppList(mAppInfos, REFRESH_APP, 0);
                //计算总行数
                mTotalLine = calculateRowNumber(mTotalSize);
                refreshLineInformation();
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                // TODO: 2016/11/11  具体
                if (call.isCanceled()) {
                    return;
                }
                mView.hideLoadingDialog();
                ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.load_data_faild));
                mView.finish();
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
        mView.refreshAppList(mAppInfos, REFRESH_APP, -1);
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.no_network));
            sendFailLoadRunable(calculateDelayTime());
            return;
        }

        CanCallback<Result<AppInfoContainer>> canCallback = new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                // TODO: 2016/11/11
                //初始化分页信息
                mPage = 2;
                mCurrentLine = 0;
                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                List<AppInfo> appInfos = data.getData();
                mAppInfos.addAll(appInfos);
                //计算总行数
                mTotalLine = calculateRowNumber(mTotalSize);
                //计算延迟时间
                long delayTime = calculateDelayTime();
                mHandler.sendEmptyMessageDelayed(HIDE_LOADING, delayTime);
                mView.refreshAppList(mAppInfos, REFRESH_APP, delayTime);
                refreshLineInformation();
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                long delayTime = calculateDelayTime();
                // TODO: 2016/11/22 合并代码后删除
                if (call.isCanceled()) {
                    return;
                }
                mHandler.sendEmptyMessageDelayed(HIDE_LOADING, delayTime);
                sendFailLoadRunable(delayTime);
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
            mView.showToast(mContext.getResources().getString(R.string.no_more_content));
            return;
        }
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            ToastUtils.showMessage(mContext, mContext.getResources().getString(R.string.no_network));
            return;
        }
        mView.showToast(mContext.getResources().getString(R.string.load_more_content));
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
                List<AppInfo> appInfos = data.getData();
                mAppInfos.addAll(appInfos);
                mView.refreshAppList(mAppInfos, (mPage - 1) * PAGE_SIZE, 0); // TODO: 2016/10/21  请求的数据为空数据不足  更新total
                refreshLineInformation();
                mPage++;//请求成功页数加1
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                Log.d(TAG, "onFailure:loadMoreData");
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
        Log.d(TAG, "refreshLineInformation: onAppListItemSelectChanged");
        //applist第一次获得焦点不处理
        if (isFirstFocus) {
            isFirstFocus = false;
            return;
        }

        if (position == AppListActivity.FOCUS_MOVE_OUTSIDE_APP_LIST) {
            mCurrentLine = 0;
        } else {
            mCurrentLine = calculateRowNumber(position + 1);
        }
        refreshLineInformation();
    }

    /**
     * 请求数据未成功的时候再次请求数据
     */
    public void loadAppListData() {
        mRecentLoadingTime = System.currentTimeMillis();
        loadAppListData(mTopics.get(mMenuDataPosition).getId());
    }

    @Override
    public void onMenuItemSelect(int position) {
        mHandler.removeMessages(HIDE_LOADING);
        mRecentLoadingTime = System.currentTimeMillis();
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
        Log.d(TAG, "refreshLineInformation: " + mCurrentLine + ",---" + mTotalLine);
        StringBuilder crowNumber = new StringBuilder();
        crowNumber.append(mCurrentLine);
        crowNumber.append(mContext.getResources().getString(R.string.backslashes));
        crowNumber.append(mTotalLine);
        crowNumber.append(mContext.getResources().getString(R.string.line));
        SpannableStringBuilder spannable = new SpannableStringBuilder(crowNumber);
        int currentLineTextLength = String.valueOf(mCurrentLine).length();
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, currentLineTextLength, Spannable
                .SPAN_EXCLUSIVE_INCLUSIVE);
        mView.refreshRowNumber(spannable);
    }

    /**
     * 延时显现加载错误UI
     *
     * @param delayTime 延迟时间
     */
    private void sendFailLoadRunable(long delayTime) {
        if (mLoadFailRunable == null) {
            mLoadFailRunable = new Runnable() {
                @Override
                public void run() {
                    mView.hideAppList();
                    mView.showFailUI();
                }
            };
        }
        mHandler.removeCallbacks(mLoadFailRunable);
        mHandler.postDelayed(mLoadFailRunable, delayTime);
    }

    /**
     * 计算loading隐藏所需要延迟的时间
     *
     * @return 延迟时间
     */
    private long calculateDelayTime() {
        long time = System.currentTimeMillis() - mRecentLoadingTime;
        long delayTime;
        if (time < AppListActivity.MIN_LOADING_SHOW_TIME && AppListActivity.MIN_LOADING_SHOW_TIME - time >
                AppListActivity.MIN_APPLIST_REFRES_TIME) {
            delayTime = AppListActivity.MIN_LOADING_SHOW_TIME - time;
        } else {
            delayTime = AppListActivity.MIN_APPLIST_REFRES_TIME;
        }
        Log.d(TAG, "calculateDelayTime: " + delayTime);
        return delayTime;
    }

    /**
     * 获取当前的TopicId AppId
     *
     * @return topicId 和 appId的数组
     */
    public HashMap getIds(int position) {
        HashMap map = new HashMap();
        map.put(AppListActivity.ENTRY_KEY_TOPIC_ID, mTopics.get(mMenuDataPosition).getId());
        map.put(AppListActivity.ENTRY_KEY_APP_ID, mAppInfos.get(position).getId());
        return map;
    }

    /**
     * 上下移动menu，位置没有改变的时候，不加载数据，显示隐藏的UI
     */
    public void noLoadShowHideUI() {
        if (mAppInfos.size() == 0) {
            mView.showFailUI();
        } else {
            mView.showAppList();
        }
    }

    /**
     * 释放view引用，关闭没有完成的网络请求
     */
    @Override
    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mView = null;
        if (mAppListInfoCall != null) {
            mAppListInfoCall.cancel();
        }
        if (mAppsRanking != null) {
            mAppsRanking.cancel();
        }
    }
}
