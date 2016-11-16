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

import java.util.List;

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
    private AppListContract.View mView;
    private Handler mHandler;
    private Context mContext;
    private int mFromType;
    private String mTypeId;
    private String mTopicId;
    private List<Topic> topics;
    private List<AppInfo> AppInfos;
    private int mTotalLine;
    private int mCurrentLine;
    private int mMenuDataPosition;
    private int mPage;
    private int mTotalSize;

    public AppListPresenter(Context context, AppListContract.View view, int fromType, String typeId, String topicId) {
        mContext = context;
        mView = view;
        mFromType = fromType;
        // TODO: 2016/11/10 测试使用type  后期删除
        //mTypeId = typeId;
        mTypeId = "5";
        mTopicId = topicId;
        mView.setPresenter(this);
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
                        loadAppListData(topics.get(mMenuDataPosition).getId());
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
        mAppListInfoCall = HttpManager.getApiService().getAppinfos("", mTypeId, mPage, PAGE_SIZE);
        mAppListInfoCall.enqueue(new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                mPage++;
                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                mTotalSize = data.getTotal();
                String typeName = data.getTypeName();
                // TODO: 2016/11/11 先初始化 
                topics = data.getTopics();
                AppInfos = data.getData();
                // TODO: 2016/11/11  删除 
                //CollectionUtil.emptyIfNull()
                mMenuDataPosition = findMenuFocusPosition();//第一次加载完数据，找到需要获取焦点的位置
                for(int i = 0;i < 6;i++){
                    Topic topic = new Topic();
                    topic.setName("11111");
                    topics.add(topic);
                }
                mView.refreshTypeName(typeName);
                mView.refreshMenuList(topics, mMenuDataPosition);
                // TODO: 2016/11/11 重载
                mView.refreshAppList(AppInfos, REFRESH_APP);
                //计算总行数
                mTotalLine = calculateLineNumber(mTotalSize);
                refreshLineInformation();
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                mView.hideLoadingDialog();
                // TODO: 2016/11/11  具体
                mView.onLoadFail();
                Log.d(TAG, "onFailure:");
            }
        });


    }

    /**
     * menu 位置改变请求数据
     */
    @Override
    public void loadAppListData(String topicId) {
        mPage = 1;
        // TODO: 2016/11/11  名称
        AppInfos.clear();
        if (mAppListInfoCall != null) {
            mAppListInfoCall.cancel();
        }
        mAppListInfoCall = HttpManager.getApiService().getAppinfos(topicId, "5", mPage, PAGE_SIZE);
        mAppListInfoCall.enqueue(new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                mView.hideLoadingDialog();
                // TODO: 2016/11/11   
                //初始化分页信息
                mPage = 2;
                mCurrentLine = 1;

                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                AppInfos = data.getData();
                mTotalSize = data.getTotal();
                mView.hideLoadingDialog();
                mView.refreshAppList(AppInfos, REFRESH_APP);
                //计算总行数
                mTotalLine = calculateLineNumber(mTotalSize);
                refreshLineInformation();
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                mView.hideLoadingDialog();
                mView.onLoadFail();
                Log.d(TAG, "onFailure:");
            }
        });
    }

    @Override
    public void loadMoreData() {
        if (mAppListInfoCall != null) {
            mAppListInfoCall.cancel();
        }
        mAppListInfoCall = HttpManager.getApiService().getAppinfos(topics.get(mMenuDataPosition).getId(), "5", mPage,
                PAGE_SIZE);
        mAppListInfoCall.enqueue(new CanCallback<Result<AppInfoContainer>>() {
            @Override
            public void onResponse(CanCall<Result<AppInfoContainer>> call, Response<Result<AppInfoContainer>>
                    response) throws Exception {
                Result<AppInfoContainer> body = response.body();
                Log.d(TAG, "onResponse: " + body.toString());
                AppInfoContainer data = body.getData();
                List<AppInfo> eAppInfos = data.getData();
                AppInfos.addAll(eAppInfos);
                mView.refreshAppList(AppInfos, (mPage - 1) * PAGE_SIZE); // TODO: 2016/10/21  请求的数据为空数据不足  更新total
                refreshLineInformation();
                mPage++;//请求成功页数加1
            }

            @Override
            public void onFailure(CanCall<Result<AppInfoContainer>> call, CanErrorWrapper errorWrapper) {
                mView.hideLoadingDialog();
                mView.onLoadFail();
                Log.d(TAG, "onFailure:");
            }
        });
    }


    /**
     * 把所在位置或者App总数换算为所在行数或者总行数
     *
     * @param number
     * @return 行数`
     */
    private int calculateLineNumber(int number) {
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
        mCurrentLine = calculateLineNumber(position + 1);
        refreshLineInformation();
    }

    /**
     * 请求数据未成功的时候再次请求数据
     */
    public void loadAppListData() {
        loadAppListData(topics.get(mMenuDataPosition).getId());
    }

    /**
     * 获取菜单列表需要定位选中光标的位置
     *
     * @return position
     */
    private int findMenuFocusPosition() {
        //        if (mTopicId == null || "".equals(mTopicId)) {
        //            if (mFromType == AppListActivity.APPLICATION) {
        //                return 1;
        //            } else {
        //                return 0;
        //            }
        //        }
        //
        //        for (int i = 0; i < mMenuData.size(); i++) {
        //            if (mTopicId.equals(mMenuData.get(i).getId())) {
        //                return i;
        //            }
        //        }
        return 0;
    }

    @Override
    public void onMenuItemSelect(int position) {
        if (mHandler != null) {
            mHandler.removeMessages(REFRESH_APP_LIST);
            // TODO: 2016/11/11  合并
            Message message = Message.obtain();
            message.what = REFRESH_APP_LIST;
            message.arg1 = position;
            mHandler.sendMessageDelayed(message, REQUEST_DELAY);
        }
    }

    /**
     * 刷新右上角行数显示信息
     */
    private void refreshLineInformation() {
        // TODO: 2016/11/11  
        String lineText = mCurrentLine + " / " + mTotalLine + "行";
        SpannableStringBuilder spannable = new SpannableStringBuilder(lineText);
        int currentLineTextLength = String.valueOf(mCurrentLine).length();
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, currentLineTextLength, Spannable
                .SPAN_EXCLUSIVE_INCLUSIVE);
        mView.refreshLineText(spannable);
    }

    /**
     * 获取应用列表总数量
     */
    public int getAppListTotalSize() {
        return mTotalSize;
    }

    @Override
    public void release() {
        mView = null;
    }
}
