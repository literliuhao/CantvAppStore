package com.can.appstore.applist;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.can.appstore.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by syl on 2016/10/19.
 */

public class AppListPresenter implements AppListContract.Presenter {
    public static final int REFRESH_APP_LIST = 1;
    public static final int PAGE_SIZE = 100;   //每次加载请求的总App数
    public static final int REFRESH_APP = 0;  //整个刷新adpter
    public static final String DEFAULT_TOPIC = "-1";  //默认不请求数据，显示搜索图标的topic标识
    public static final int REQUEST_DELAY = 500;  //请求延迟时间
    private AppListContract.View mView;
    private Handler mHandler;
    private Context mContext;
    private int mFromType;
    private String mTypeId;
    private String mTopicId;
    private List<AppListMenuInfo> mMenuData;
    private List<AppListInfo> mAppListData;
    private int mTotalLine;
    private int mCurrentLine;
    private int mMenuDataPosition;
    private int mPage;
    private int mTotalSize;

    // TODO: 2016/10/21   界面切换失败，原来数据要不要清空
    public AppListPresenter(Context context, AppListContract.View view, int fromType, String typeId, String topicId) {
        mContext = context;
        mView = view;
        mFromType = fromType;
        mTypeId = typeId;
        mTopicId = topicId;
        mView.setPresenter(this);
        initHandler();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == REFRESH_APP_LIST && mMenuDataPosition != msg.arg1) {
                   if(msg.arg1 == 3){
                        mMenuDataPosition = msg.arg1;
                        mView.onLoadFail();
                        return;
                    }
                    mMenuDataPosition = msg.arg1;
                    mTopicId = mMenuData.get(msg.arg1).getId();
                    loadAppListData();
                }
            }
        };
    }

    /**
     * 第一次进入请求数据
     */
    @Override
    public void startLoadData() {
        //初始化分页信息
        mPage = 1;
        mCurrentLine = 1;

        if (mMenuData == null) {
            mMenuData = new ArrayList();
            mAppListData = new ArrayList<>();
        }else{
            mAppListData.clear();
        }
//        if (mFromType == AppListActivity.APPLICATION) {
//            AppListMenuInfo info = new AppListMenuInfo();
//            info.setId(DEFAULT_TOPIC);
//            info.setName(mContext.getResources().getString(R.string.serach));
//            mMenuData.add(info);
//        }

        //** 假数据  start**
        mView.showLoadingDialog();
        //左侧数据
        String[] str = {"全部","休闲益智","休闲益智","休闲益智","休闲益智","休闲益智","休闲益智","休闲益智"};
        for (int i = 0; i < 7; i++) {
            AppListMenuInfo info = new AppListMenuInfo();
            info.setId(String.valueOf(i));
            info.setName(str[i]);
            mMenuData.add(info);
        }
        //右侧数据
        mAppListData.clear();
        for (int i = 0; i < PAGE_SIZE; i++) {
            AppListInfo info = new AppListInfo();
            if (i == 2) {
                info.setId(i);
                info.setAppName("蓝牙共享");
                info.setIcon("http://cdn.can.cibntv.net/02/mam/Public/Picture/moviepost/16-11/01/01/fuji.jpg");
                info.setSize("15Mb");
                info.setDownloadVolume("1000+");
                info.setPackageName("com.android.bluetooth");
                info.setVersionCode(19);
                if (i % 5 == 0) {
                    info.setNew(true);
                } else {
                    info.setNew(false);
                }
            }else {
                info.setId(i);
                info.setAppName(mMenuDataPosition + "三国杀传奇" + i);
                info.setIcon("http://cdn.can.cibntv.net/02/mam/Public/Picture/moviepost/16-11/01/01/fuji.jpg");
                info.setSize("15Mb");
                info.setDownloadVolume("1000+");
                info.setPackageName("");
                if (i % 2 == 0) {
                    info.setRecommend("参与游戏活动，赢好礼");
                } else {
                    info.setRecommend("");
                }
                info.setVersionCode(i);
                if (i % 5 == 0) {
                    info.setNew(true);
                } else {
                    info.setNew(false);
                }
            }
            mAppListData.add(info);
        }
        //** 假数据  end **
        mView.hideLoadingDialog();
        mTotalSize = 120;
        mMenuDataPosition = findMenuFocusPosition();//第一次加载完数据，找到需要获取焦点的位置
        mView.refreshMenuList(mMenuData, mMenuDataPosition);
        mView.refreshAppList(mAppListData, REFRESH_APP);
        //计算总行数
        mTotalLine = calculateLineNumbe(mTotalSize);
        refreshLineInformation();
    }
    /**
     * menu 位置改变请求数据
     */
    @Override
    public void loadAppListData() {
        //初始化分页信息
        mPage = 1;
        mCurrentLine = 1;

        //** 假数据  start**
        mView.showLoadingDialog();
        if (mMenuData == null) {
            mMenuData = new ArrayList();
            mAppListData = new ArrayList<>();
        }
        //右侧数据
        mAppListData.clear();
        for (int i = 0; i < PAGE_SIZE; i++) {
            AppListInfo info = new AppListInfo();
            if (i == 2) {
                info.setId(i);
                info.setAppName("蓝牙共享");
                info.setIcon("http://cdn.can.cibntv.net/02/mam/Public/Picture/moviepost/16-11/01/01/fuji.jpg");
                info.setSize("15Mb");
                info.setDownloadVolume("1000+");
                info.setPackageName("com.android.bluetooth");
                if (i % 2 == 0) {
                    info.setRecommend("参与游戏活动，赢好礼");
                } else {
                    info.setRecommend("");
                }
                info.setVersionCode(19);
                if (i % 5 == 0) {
                    info.setNew(true);
                } else {
                    info.setNew(false);
                }
            } else {
                info.setId(i);
                info.setAppName(mMenuDataPosition + "三国杀传奇" + i);
                info.setIcon("http://cdn.can.cibntv.net/02/mam/Public/Picture/moviepost/16-11/01/01/fuji.jpg");
                info.setSize("15Mb");
                info.setDownloadVolume("1000+");
                info.setPackageName("");
                if (i % 2 == 0) {
                    info.setRecommend("参与游戏活动，赢好礼");
                } else {
                    info.setRecommend("");
                }
                info.setVersionCode(i);
                if (i % 5 == 0) {
                    info.setNew(true);
                } else {
                    info.setNew(false);
                }
            }
            mAppListData.add(info);
        }
        //** 假数据  end **
        mTotalSize = 120;
        mView.hideLoadingDialog();
        mView.refreshAppList(mAppListData, REFRESH_APP);
        //计算总行数
        mTotalLine = calculateLineNumbe(mTotalSize);
        refreshLineInformation();
    }

    @Override
    public void loadMoreData() {
        mView.showLoadingDialog();
        //**假数据 start **
        for (int i = 0; i < PAGE_SIZE; i++) {
            AppListInfo info = new AppListInfo();
            if (i == 2) {
                info.setId(i);
                info.setAppName("蓝牙共享");
                info.setIcon("http://cdn.can.cibntv.net/02/mam/Public/Picture/moviepost/16-11/01/01/fuji.jpg");
                info.setSize("15Mb");
                info.setDownloadVolume("1000+");
                info.setPackageName("com.android.bluetooth");
                if (i % 2 == 0) {
                    info.setRecommend("参与游戏活动，赢好礼");
                } else {
                    info.setRecommend("");
                }
                info.setVersionCode(19);
                if (i % 5 == 0) {
                    info.setNew(true);
                } else {
                    info.setNew(false);
                }
            } else {
                info.setId(i);
                info.setAppName(mMenuDataPosition + "三国杀传奇" + i);
                info.setIcon("http://cdn.can.cibntv.net/02/mam/Public/Picture/moviepost/16-11/01/01/fuji.jpg");
                info.setSize("15Mb");
                info.setDownloadVolume("1000+");
                info.setPackageName("");
                if (i % 2 == 0) {
                    info.setRecommend("参与游戏活动，赢好礼");
                } else {
                    info.setRecommend("");
                }
                info.setVersionCode(i);
                if (i % 5 == 0) {
                    info.setNew(true);
                } else {
                    info.setNew(false);
                }
            }
            mAppListData.add(info);
        }
        //**假数据  end **
        mView.hideLoadingDialog();
        mView.refreshAppList(mAppListData, mPage * PAGE_SIZE); // TODO: 2016/10/21  请求的数据为空数据不足  更新total
        refreshLineInformation();
        mPage++;//请求成功页数加1
    }

    /**
     * 把所在位置或者App总数换算为所在行数或者总行数
     * @param number
     * @return 行数`
     */
    private int calculateLineNumbe(int number) {
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
        mCurrentLine = calculateLineNumbe(position+1);
        refreshLineInformation();
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
        if (position != mMenuDataPosition && mHandler != null) {
            mHandler.removeMessages(REFRESH_APP_LIST);
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
        String lineText = mCurrentLine + " / " + mTotalLine + "行";
        SpannableStringBuilder spannable = new SpannableStringBuilder(lineText);
        int currentLineTextLength = String.valueOf(mCurrentLine).length();
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE),0,currentLineTextLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mView.refreshLineText(spannable);
    }

    /**
     * 获取应用列表总数量
     */
    public int getAppListTotalSize() {
        return mTotalSize;
    }

    @Override
    public void relese() {
        mView = null;
    }
}
