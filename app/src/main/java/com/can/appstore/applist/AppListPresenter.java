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
import retrofit2.Response;

/**
 * Created by syl on 2016/10/19.
 */

public class AppListPresenter implements AppListContract.Presenter {
    private static final String TAG = "AppListPresenter";
    //常量
    public static final int PAGE_SIZE = 18;   //每次加载请求的总App数
    public static final int REQUEST_DELAY = 500;  //请求延迟时间
    public static final int HIDE_LOADING_DELAY = 200;//加载更多失败延时消失loading
    //handler msg.what  request
    public static final int REQUEST_DATA = 1;
    public static final int REFRESH_APP_LIST = 2;
    public static final int REFRESH_TYPE_NAME = 3;
    public static final int HIDE_LOADING = 4;
    public static final int SHOW_APP_LIST_UI = 5;
    public static final int SHOW_LOAD_FAIL_UI = 6;
    //联网请求相关
    private CanCall<Result<AppInfoContainer>> mAppListInfoCall;
    private CanCall<Result<AppInfoContainer>> mAppsRanking;
    //页面数据
    private int mPageType;//当前页类型
    private String mTypeId;//页面类型id
    private long mRecentLoadingTime;//最近一次loading框显示的时间
    private int mLoadOffset;
    //menu数据
    private String mTopicId;//左侧menu列表id
    private int mMenuDataPosition;//左侧menu请求数据的位置
    private List<Topic> mTopics;//左侧列表数据
    //应用列表数据
    private int mPage;//应用列表分页请求当前请求的的页数
    private int mTotalSize;//应用列表item总个数
    private int mCurrentLine;//当前焦点所在应用列表行数
    private int mTotalLine;//当前应用列表总行数
    private List<AppInfo> mAppInfos;//右侧列表数据
    //标识
    private boolean isLoadFail;
    //全局参数
    private AppListContract.View mView;
    private Handler mHandler;
    private Context mContext;


    public AppListPresenter(AppListContract.View view, int pageType, String typeId, String topicId) {
        mView = view;
        mContext = view.getContext();
        mPageType = pageType;
        mTypeId = typeId;
        mTopicId = topicId;
        mView.setPresenter(this);
        mTopics = new ArrayList<>();
        mAppInfos = new ArrayList<>();
        mLoadOffset = mContext.getResources().getDimensionPixelSize(R.dimen.px132);
        initHandler();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case REQUEST_DATA:
                        if (mMenuDataPosition == msg.arg1) {
                            mView.hideLoadingDialog();
                            //显示隐藏的UI
                            noLoadShowHideUI();
                        } else {
                            mMenuDataPosition = msg.arg1;
                            loadAppListData(mTopics.get(mMenuDataPosition).getId());
                        }
                        break;
                    case REFRESH_APP_LIST:
                        mView.refreshAppList(mAppInfos);
                        break;
                    case HIDE_LOADING:
                        mView.hideLoadingDialog();
                        break;
                    case SHOW_APP_LIST_UI:
                        refreshLineInformation();
                        mView.showAppList();
                        mView.hideFailUI();
                        break;
                    case REFRESH_TYPE_NAME:
                        mView.refreshTypeName((String) msg.obj);
                        break;
                    case SHOW_LOAD_FAIL_UI:
                        mView.hideAppList();
                        mView.showFailUI();
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
            mView.showToast(mContext.getResources().getString(R.string.no_network));
            mView.finish();
            return;
        }

        mPage = 1;
        mCurrentLine = 1;
        mView.showLoadingDialog();
        //初始化请求数据回调
        CanCallback<Result<AppInfoContainer>> canCallback = new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                mPage++;
                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                mTotalSize = data.getTotal();
                String typeName = data.getTypeName();
                mTypeId = data.getTypeId();
                List<Topic> topics = data.getTopics();
                List<AppInfo> appInfos = data.getData();
                mTopics.addAll(topics);
                mMenuDataPosition = findMenuFocusPosition();//第一次加载完数据，找到需要获取焦点的位置
                mAppInfos.addAll(appInfos);
                if (mPageType == AppListActivity.PAGE_TYPE_APP_LIST) {
                    mView.showSearchView();
                }
                mView.refreshTypeName(typeName);
                mView.refreshMenuList(mTopics, mMenuDataPosition);

                //应用列表数据为空的时候不显示列表和行数
                if (mAppInfos.size() == 0) {
                    mView.hideLoadingDialog();
                    return;
                }
                mView.refreshAppList(mAppInfos);
                //计算总行数
                mTotalLine = calculateRowNumber(mTotalSize);
                refreshLineInformation();

                //处理页面显示
                mView.hideLoadingDialog();
                mView.showAppList();
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                if (call.isCanceled()) {
                    return;
                }
                mView.hideLoadingDialog();
                mView.showToast(mContext.getResources().getString(R.string.load_data_faild));
                mView.finish();
                Log.d(TAG, "onFailure:" + errorWrapper.getReason() + "-----" + errorWrapper.getThrowable());
            }
        };

        //根据不同页面请求数据
        if (mPageType == AppListActivity.PAGE_TYPE_APP_LIST) {
            mAppListInfoCall = HttpManager.getApiService().getAppinfos(mTopicId, mTypeId, mPage, PAGE_SIZE);
            mAppListInfoCall.enqueue(canCallback);
        } else {
            mAppsRanking = HttpManager.getApiService().getAppsRanking(mTopicId);
            mAppsRanking.enqueue(canCallback);
        }


    }

    /**
     * menu 位置改变请求数据
     */
    public void loadAppListData(final String topicId) {
        mPage = 1;
        mCurrentLine = 1;
        mAppInfos.clear();
        mView.refreshAppList(mAppInfos);
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            mView.showToast(mContext.getResources().getString(R.string.no_network));
            isLoadFail = true;
            long delayTime = calculateDelayTime();
            mHandler.sendEmptyMessageDelayed(HIDE_LOADING, delayTime);
            mHandler.sendEmptyMessageDelayed(SHOW_LOAD_FAIL_UI, delayTime);
            return;
        }

        CanCallback<Result<AppInfoContainer>> canCallback = new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                isLoadFail = false;
                //初始化分页信息
                mPage = 2;
                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                mTotalSize = data.getTotal();
                List<AppInfo> appInfos = data.getData();
                mAppInfos.addAll(appInfos);

                //数据为空的情况下不刷新列表,隐藏loading框
                if (mAppInfos.size() == 0) {
                    mView.hideLoadingDialog();
                    return;
                }
                //计算总行数
                mTotalLine = calculateRowNumber(mTotalSize);
                //计算延迟时间
                long delayTime = calculateDelayTime();
                //刷新ui
                mHandler.sendEmptyMessage(REFRESH_APP_LIST);
                mHandler.sendEmptyMessageDelayed(HIDE_LOADING, delayTime);
                mHandler.sendEmptyMessageDelayed(SHOW_APP_LIST_UI, delayTime);
                mHandler.sendMessageDelayed(Message.obtain(mHandler, REFRESH_TYPE_NAME, data.getTypeName()),
                        delayTime);
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                long delayTime = calculateDelayTime();
                isLoadFail = true;
                mHandler.sendEmptyMessageDelayed(HIDE_LOADING, delayTime);
                mHandler.sendEmptyMessageDelayed(SHOW_LOAD_FAIL_UI, delayTime);
                if (mPageType == AppListActivity.PAGE_TYPE_RANKING) {
                    mHandler.sendMessageDelayed(Message.obtain(mHandler, REFRESH_TYPE_NAME, mTopics.get
                                    (mMenuDataPosition).getName() + mContext.getResources().getString(R.string
                                    .ranking)),
                            delayTime);
                }
                Log.d(TAG, "onFailure:" + errorWrapper.getReason() + "-----" + errorWrapper.getThrowable());
            }
        };

        cancelCall();
        if (mPageType == AppListActivity.PAGE_TYPE_APP_LIST) {
            mAppListInfoCall = HttpManager.getApiService().getAppinfos(topicId, mTypeId, mPage, PAGE_SIZE);
            mAppListInfoCall.enqueue(canCallback);
        } else {
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
            mView.showToast(mContext.getResources().getString(R.string.no_network));
            return;
        }
        mView.showToast(mContext.getResources().getString(R.string.load_more_content));
        cancelCall();
        mAppListInfoCall = HttpManager.getApiService().getAppinfos(mTopics.get(mMenuDataPosition).getId(), mTypeId,
                mPage, PAGE_SIZE);
        mAppListInfoCall.enqueue(new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                List<AppInfo> appInfos = data.getData();

                //数据总数错误，重新计算
                if (appInfos.size() == 0) {
                    mTotalSize = mAppInfos.size();
                    mTotalLine = calculateRowNumber(mTotalSize);
                    refreshLineInformation();
                    return;
                }

                mAppInfos.addAll(appInfos);
                mView.refreshAppList(mAppInfos, (mPage - 1) * PAGE_SIZE);
                refreshLineInformation();
                mPage++;//请求成功页数加1
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                mHandler.sendEmptyMessageDelayed(HIDE_LOADING, HIDE_LOADING_DELAY);
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

    /**
     * 根据topic找到焦点需要定位的位置
     *
     * @return 焦点位置
     */
    private int findMenuFocusPosition() {
        for (int i = 0; i < mTopics.size(); i++) {
            if (mTopicId.equals(mTopics.get(i).getId())) {
                return i;
            }
        }
        return 0;
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
        mRecentLoadingTime = System.currentTimeMillis();
        loadAppListData(mTopics.get(mMenuDataPosition).getId());
    }

    @Override
    public void onMenuItemSelect(int position) {
        //移除网络请求和消息
        cancelCall();
        mHandler.removeMessages(REFRESH_APP_LIST);
        mHandler.removeMessages(REFRESH_TYPE_NAME);
        mHandler.removeMessages(HIDE_LOADING);
        mHandler.removeMessages(SHOW_APP_LIST_UI);
        mHandler.removeMessages(SHOW_LOAD_FAIL_UI);
        mRecentLoadingTime = System.currentTimeMillis();
        //发送延时请求数据消息
        if (mHandler != null) {
            mHandler.removeMessages(REQUEST_DATA);
            Message msg = Message.obtain();
            msg.what = REQUEST_DATA;
            msg.arg1 = position;
            mHandler.sendMessageDelayed(msg, REQUEST_DELAY);
        }
    }

    /**
     * 取消网络请求
     */
    private void cancelCall() {
        if (mPageType == AppListActivity.PAGE_TYPE_APP_LIST) {
            if (mAppListInfoCall != null) {
                mAppListInfoCall.cancel();
            }
        } else {
            if (mAppsRanking != null) {
                mAppsRanking.cancel();
            }
        }
    }

    /**
     * 刷新右上角行数显示信息
     */
    private void refreshLineInformation() {
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
        return delayTime;
    }

    /**
     * 获取当前的TopicId AppId
     *
     * @return topicId 和 appId 数组
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
        if (isLoadFail) {
            mHandler.sendEmptyMessage(SHOW_LOAD_FAIL_UI);
        } else if (mAppInfos.size() != 0) {
            mHandler.sendEmptyMessage(SHOW_APP_LIST_UI);
        }
        mHandler.sendEmptyMessage(HIDE_LOADING);
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
