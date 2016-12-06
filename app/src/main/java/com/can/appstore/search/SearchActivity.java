package com.can.appstore.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.search.adapter.HotRecommendAdapter;
import com.can.appstore.search.adapter.KeyboardAdapter;
import com.can.appstore.search.adapter.SearchAppListAdapter;
import com.dataeye.sdk.api.app.DCEvent;
import com.dataeye.sdk.api.app.channel.DCPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.utils.PromptUtils;

public class SearchActivity extends BaseActivity implements SearchContract.View, View.OnClickListener {

    private TextView mSearch_con_view;
    private RecyclerView mKeyboard_recy;
    private TextView mContent_cl_view;
    private TextView mContent_del_view;
    private KeyboardAdapter mKeyboardAdapter;
    private SearchPresenter mSearchPresenter;
    private static final int START_SEARCH = 1;

    private final int TAG_SHOW_TOP_BOTTOM = 0;   //默认展示
    private final int TAG_S_NULLAPP_G_TOP_APPLIST = 1;    //没有搜到内容
    private final int TAG_S_TOP_APPLIST_G_BOTTOM = 2;    //搜到内容
    private int mCurrPageIndex = 1; //搜索到的第几页的数据,默认第一页
    public String mSearchKeyStr;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_SEARCH:
                    mCurrPageIndex = 1;
                    mSearchKeyStr = mSearch_con_view.getText().toString().trim();
                    mSearchPresenter.getSearchList(mSearchKeyStr, mCurrPageIndex);
                    break;
            }
        }
    };
    private TextView mleft_top;
    private TextView mright_top;
    private RecyclerView mSearAppList_recycle;
    private LinearLayout mBottom_re_ll;
    private RecyclerView mBottom_re_recycle;
    private static final int KEYBOARD_SPANCOUNT = 6;
    public final int SEARCH_APP_SPANCOUNT = 4;
    private SearchAppListAdapter mAppListAdapter;
    private View mSearch_null;
    private HotRecommendAdapter mHotRecommendAdapter;
    private FocusMoveUtil mFocusMoveUtil;
    //    private FocusScaleUtil mFocusScaleUtil;
    private MyFocusRunnable myFocusRunnable;
    private View mFocusedListChild;
    private ScaleFocusChangeListener mScaleFocusChangeListener;
    private RelativeLayout mTopView;
    private GridLayoutManager mGridLayoutManager;
    private View mLeftView;
    private int mWinH;
    private int mWinW;
    private boolean setRightNextFocus;  //设置右侧热词和热门推荐之间焦点跳转
    private View mRLNoNetworkView;
    private List mHotRomList = new ArrayList();
    private List mSearchList = new ArrayList();
    private View mSerch_icon;
    private int mCurrLineNumber;
    private int mTotalLineCount;
    private int mSearchTotal;

    public static void startAc(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //获取到屏幕的宽高
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWinH = outMetrics.heightPixels;
        mWinW = outMetrics.widthPixels;

        initView();
        initData();
    }

    private void initView() {
        mSearchPresenter = new SearchPresenter(this);
        //用于缩放操作
        mScaleFocusChangeListener = new ScaleFocusChangeListener();
        List<String> mKeyList = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"
                , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
        //左侧布局
        mLeftView = findViewById(R.id.search_left_vew);
        mSerch_icon = findViewById(R.id.search_icon);
        mSearch_con_view = (TextView) findViewById(R.id.show_se_con_view);
        mKeyboard_recy = (RecyclerView) findViewById(R.id.keyboard_recycleview);
        mContent_cl_view = (TextView) findViewById(R.id.con_clear_view);
        mContent_del_view = (TextView) findViewById(R.id.con_del_view);

        //无网络展示的布局
        mRLNoNetworkView = findViewById(R.id.rl_no_network);

        //右侧布局
        mTopView = (RelativeLayout) findViewById(R.id.top_view);
        mleft_top = (TextView) findViewById(R.id.left_top_view);
        mright_top = (TextView) findViewById(R.id.right_top_view);
        mSearAppList_recycle = (RecyclerView) findViewById(R.id.applist_recycle);
        //设置焦点框可以显示的范围
        mSearAppList_recycle.post(new Runnable() {
            @Override
            public void run() {
                int[] posi = new int[2];
                mSearAppList_recycle.getLocationInWindow(posi);
                mFocusMoveUtil.setFocusActiveRegion(0, 0, mWinW, mWinH);
            }
        });
        mBottom_re_ll = (LinearLayout) findViewById(R.id.ll_recommed);
        mBottom_re_ll.setVisibility(View.VISIBLE);
        mBottom_re_recycle = (RecyclerView) findViewById(R.id.recommend_app_recycle);
        //没有搜索到数据时显示
        mSearch_null = findViewById(R.id.search_null);

        mKeyboard_recy.setLayoutManager(new GridLayoutManager(SearchActivity.this, KEYBOARD_SPANCOUNT, LinearLayoutManager.VERTICAL, false));
        mKeyboardAdapter = new KeyboardAdapter(mKeyList);
        mKeyboard_recy.setAdapter(mKeyboardAdapter);

        mKeyboardAdapter.setOnItemClickListener(new KeyboardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, String content) {
                mSearch_con_view.setText(mSearch_con_view.getText() + content);
            }
        });

        //发搜索Handler
        final SearchRunner searchRunner = new SearchRunner();
        mSearch_con_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeMessages(START_SEARCH);
                mHandler.removeCallbacks(searchRunner);
                if (s.length() > 0) {
                    if (s.length() > 8) {
                        mSearch_con_view.setText(s.toString().substring(0, 8));
                    } else {
                        mSerch_icon.setVisibility(View.GONE);
                        mHandler.postDelayed(searchRunner, 2000);
                    }
                } else {
                    mSerch_icon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //搜索内容
        mAppListAdapter = new SearchAppListAdapter(mSearchList, this);
        mSearAppList_recycle.setAdapter(mAppListAdapter);
        //"热门推荐"数据
        mHotRecommendAdapter = new HotRecommendAdapter(mHotRomList, this);
        mBottom_re_recycle.setAdapter(mHotRecommendAdapter);

        mContent_del_view.setOnClickListener(this);
        mContent_cl_view.setOnClickListener(this);

        //焦点缩放相关
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.search_btn_focus1);
//        mFocusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();
        mContent_cl_view.setOnFocusChangeListener(mScaleFocusChangeListener);
        mContent_del_view.setOnFocusChangeListener(mScaleFocusChangeListener);
        mKeyboardAdapter.setMyOnFocusChangeListener(mScaleFocusChangeListener);

    }


    /**
     * 得到"大家都在搜"的数据
     */
    private void initData() {
        showLoadingDialog();

        mGridLayoutManager = new GridLayoutManager(this, SEARCH_APP_SPANCOUNT, LinearLayoutManager.VERTICAL, false);
        mSearAppList_recycle.setLayoutManager(mGridLayoutManager);

        mSearAppList_recycle.addOnScrollListener(getOnBottomListener());

        mBottom_re_recycle.setLayoutManager(new GridLayoutManager(this, SEARCH_APP_SPANCOUNT, LinearLayoutManager.VERTICAL, false));
        mSearchPresenter.getDefaultList();  //获取"大家都在搜"的数据
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.con_clear_view:
                clearContent();
                break;
            case R.id.con_del_view:
                delContent();
                break;
        }
    }

    public class SearchRunner implements Runnable {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(START_SEARCH);
        }
    }

    @Override
    public void startSearch() {
        PromptUtils.toast(this.getApplicationContext(), "开始搜索...");
    }

    /**
     * 进行删除首字母
     */
    @Override
    public void delContent() {
        CharSequence con = mSearch_con_view.getText();
        if (con.length() > 0) {
            mSearch_con_view.setText(con.toString().substring(0, con.length() - 1));
        }
        if (!(mSearch_con_view.getText().length() > 0)) {
            resetDefaultList();
        }
    }

    /**
     * 清空首字母
     */
    @Override
    public void clearContent() {
        mSearch_con_view.setText("");
        resetDefaultList();
    }


    /**
     * 获取到搜索
     *
     * @param list
     */
    @Override
    public void getAppList(List list, int total, boolean... isFirstSearch) {
        mSearchTotal = total;
        mleft_top.setText(R.string.search_left_top_prompt2);
        if (null != list && list.size() > 0) {
            showGoneView(TAG_S_TOP_APPLIST_G_BOTTOM);
            mAppListAdapter.setDataList(list, isFirstSearch[0]);
        } else {
            showGoneView(TAG_S_NULLAPP_G_TOP_APPLIST);
        }
    }

    /**
     * 设置首字母
     *
     * @param con
     */
    @Override
    public void getInitials(String con) {
        mSearch_con_view.setText(con.toUpperCase());
    }

    /**
     * 热门推荐
     *
     * @param list
     */
    @Override
    public void getHotRecomAppList(List list) {
        mHotRecommendAdapter.setDataList(list);
        mHotRecommendAdapter.setMyOnFocusChangeListener(mScaleFocusChangeListener);
        showGoneView(TAG_SHOW_TOP_BOTTOM);
        if (!setRightNextFocus) {
            setRNextFocus();
        }
        mSearchPresenter.resourcesPositionExposure();
    }

    /**
     * 大家都在搜
     *
     * @param list
     */
    @Override
    public void getHotKeyList(List list) {
        //"大家都在搜"数据
        mAppListAdapter.setDefaultApplist(list);
        mAppListAdapter.setOnInitialsListener(new SearchAppListAdapter.OnInitialsListener() {
            @Override
            public void onInitials(String con) {
                getInitials(con);
            }
        });

        showGoneView(TAG_SHOW_TOP_BOTTOM);

        //对显示行号的处理
        mAppListAdapter.setOnFocusChangeListener(new SearchAppListAdapter.YOnFocusChangeListener() {

            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    if (mAppListAdapter.SEARCH_APPLIST_TYPE == mAppListAdapter.getItemViewType(position)) {
                        //显示出行数View
                        mright_top.setVisibility(View.VISIBLE);
                        //行数
                        mCurrLineNumber = position / SEARCH_APP_SPANCOUNT + 1;
//                        int totalItemCount = mSearAppList_recycle.getLayoutManager().getItemCount();
                        //计算总行数
                        mTotalLineCount = mSearchTotal / SEARCH_APP_SPANCOUNT + (mSearchTotal % SEARCH_APP_SPANCOUNT > 0 ? 1 : 0);
                        //列数
//                        int colNumber = (position + 1) % SEARCH_APP_SPANCOUNT == 0 ? SEARCH_APP_SPANCOUNT : (position + 1) % SEARCH_APP_SPANCOUNT;
//                        mright_top.setText(colNumber + "/" + lineNumber + "行");
                        mright_top.setText(mCurrLineNumber + "/" + mSearchTotal + "行");
                    }
                    mFocusedListChild = view;
                    view.postDelayed(myFocusRunnable, 50);
                } else {
                    mright_top.setVisibility(View.GONE);
//                    mFocusScaleUtil.scaleToNormal();
                }
                view.setSelected(hasFocus);
            }
        });
        if (!setRightNextFocus) {
            setRNextFocus();
        }
    }

    //显示无网络
    @Override
    public void noNetWork() {
        mRLNoNetworkView.setVisibility(View.VISIBLE);
    }

    //隐藏loading
    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    /**
     * 在清空搜索时,右侧重置为默认数据
     */
    public void resetDefaultList() {
        mleft_top.setText(getResources().getText(R.string.search_left_top_prompt1));
        //热词/热门推荐都有数据时不再重新请求,否则要重新请求数据
        if (mAppListAdapter.mDefaultList.size() > 0 && mHotRecommendAdapter.mDataList.size() > 0) {
            mAppListAdapter.setDefaultApplist();
            showGoneView(TAG_SHOW_TOP_BOTTOM);
        } else {
            mSearchPresenter.getDefaultList();
        }
        mSearchPresenter.resourcesPositionExposure();
    }

    /**
     * 执行焦点变化后的具体操作
     */
    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f);
                //1f,1f不缩放
//                mFocusScaleUtil.scaleToLarge(mFocusedListChild, 1f, 1f);
            }
        }
    }

    /**
     * 焦点变化监听
     */
    private class ScaleFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                mFocusedListChild = view;
                view.postDelayed(myFocusRunnable, 50);
            } else {
//                mFocusScaleUtil.scaleToNormal();
            }
            view.setSelected(hasFocus);
        }

    }

    /**
     * 统一控制布局的显示/隐藏
     *
     * @param tag
     */
    private void showGoneView(int tag) {
        //默认状态
        if (tag == TAG_SHOW_TOP_BOTTOM) {
            mTopView.setVisibility(View.VISIBLE); //"大家都在搜"
            mSearAppList_recycle.setVisibility(View.VISIBLE);   //搜索结果对应的布局
            mBottom_re_ll.setVisibility(View.VISIBLE);  //热门推荐
            mSearch_null.setVisibility(View.GONE);  //没有到结果对应的布局
            mright_top.setVisibility(View.GONE);
            mRLNoNetworkView.setVisibility(View.GONE);
        }

        //没有搜到内容
        if (tag == TAG_S_NULLAPP_G_TOP_APPLIST) {
            mTopView.setVisibility(View.GONE); //"大家都在搜"
            mSearAppList_recycle.setVisibility(View.GONE);   //搜索结果对应的布局
            mSearch_null.setVisibility(View.VISIBLE);
            mBottom_re_ll.setVisibility(View.VISIBLE);  //热门推荐
            mright_top.setVisibility(View.GONE);
            mRLNoNetworkView.setVisibility(View.GONE);
        }

        //搜到内容
        if (tag == TAG_S_TOP_APPLIST_G_BOTTOM) {
            mTopView.setVisibility(View.VISIBLE); //"大家都在搜"
            mSearAppList_recycle.setVisibility(View.VISIBLE);   //搜索结果对应的布局
            mSearch_null.setVisibility(View.GONE);
            mBottom_re_ll.setVisibility(View.GONE);  //热门推荐
            mRLNoNetworkView.setVisibility(View.GONE);
        }

    }

    /**
     * 滑动到底部的监听
     *
     * @return
     */
    private RecyclerView.OnScrollListener getOnBottomListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                myFocusRunnable.run();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.w("onScrollStateChanged", newState + "");
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    //滑动过程中修改焦点可移动范围
                    mSearAppList_recycle.post(new Runnable() {
                        @Override
                        public void run() {
                            int[] posi = new int[2];
                            mSearAppList_recycle.getLocationInWindow(posi);
                            mFocusMoveUtil.setFocusActiveRegion(posi[0], posi[1] + mSearAppList_recycle.getPaddingTop(),
                                    posi[0] + mSearAppList_recycle.getWidth(),
                                    posi[1] + mSearAppList_recycle.getHeight() - mSearAppList_recycle.getPaddingBottom());
                        }
                    });
                    //将左边布局取消焦点
                    setLeftFocus(false);

                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mSearAppList_recycle.post(new Runnable() {
                        @Override
                        public void run() {
                            mFocusMoveUtil.setFocusActiveRegion(0, 0, mWinW, mWinH);
                            setLeftFocus(true);
                        }
                    });


                    Log.w("SCROLL_STATE_IDLE", "测试");
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        //分页加载数据
                        int lastItem = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                        int totalItemCount = layoutManager.getItemCount();

                        Log.w("lastItem", lastItem + "");
                        Log.w("totalItemCount", totalItemCount + "");
                        if ((lastItem >= totalItemCount - 1 - SEARCH_APP_SPANCOUNT)
                                && mCurrLineNumber == mTotalLineCount) {
//                            ToastUtil.toastShort("正在加载更多数据...");
                            mCurrPageIndex++;
                            mSearchPresenter.getSearchList(mSearch_con_view.getText().toString(), mCurrPageIndex);
                        }

                    }

                }
            }
        };

    }

    long time;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            //控制按键响应的速度
            case KeyEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - time > 200) {
                    time = System.currentTimeMillis();
                } else {
                    return true;
                }
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 设置搜索页左侧焦点的问题,一般在右侧数据滚动的过程中左侧会失去焦点,防止焦点乱跑
     *
     * @param isFocus
     */
    private void setLeftFocus(boolean isFocus) {
        mContent_del_view.setFocusable(isFocus);
        mContent_cl_view.setFocusable(isFocus);
        mKeyboardAdapter.setKeyLoseFocus(!isFocus);
    }

    /**
     * 设置右侧大家都在搜,热门推荐的上下焦点
     */
    private void setDefaultNextFocus() {
        Log.w("setDefaultNextFocus", "");
        if (setRightNextFocus) {
            return;
        }
        if (null != mAppListAdapter && null != mHotRecommendAdapter) {
            //热词(大家都在搜)
            List<View> hotKeyViewList = mAppListAdapter.mHotKeyViewList;
            //热门推荐
            List<View> hotRecomViewList = mHotRecommendAdapter.mViewList;

            int hotKeySize = hotKeyViewList.size() % SEARCH_APP_SPANCOUNT;
            if (hotKeyViewList.size() > 0 && hotKeySize == 0) {
                hotKeySize = SEARCH_APP_SPANCOUNT;
            }

            int hotRecomSize = hotRecomViewList.size() > SEARCH_APP_SPANCOUNT ? SEARCH_APP_SPANCOUNT : hotRecomViewList.size();
            if (hotKeySize == 0 || hotRecomSize == 0) {
                return;
            }
            setRightNextFocus = true;
            if (hotKeySize > hotRecomSize) {
                for (int i = 0; i < hotKeySize; i++) {
                    if (i < hotRecomSize) {
                        hotKeyViewList.get(hotKeyViewList.size() - hotKeySize + i).setNextFocusDownId(hotRecomViewList.get(i).getId());
                        hotRecomViewList.get(i).setNextFocusUpId(hotKeyViewList.get(hotKeyViewList.size() - hotKeySize + i).getId());
                    } else {
                        hotKeyViewList.get(hotKeyViewList.size() - hotKeySize + i).setNextFocusDownId(hotRecomViewList.get(hotRecomViewList.size() - 1).getId());
                    }
                }
            } else {
                for (int i = 0; i < hotRecomSize; i++) {
                    if (i < hotKeySize) {
                        hotKeyViewList.get(hotKeyViewList.size() - hotKeySize + i).setNextFocusDownId(hotRecomViewList.get(i).getId());
                        hotRecomViewList.get(i).setNextFocusUpId(hotKeyViewList.get(hotKeyViewList.size() - hotKeySize + i).getId());
                    } else {
                        hotRecomViewList.get(i).setNextFocusUpId(hotKeyViewList.get(hotKeyViewList.size() - 1).getId());
                    }
                }
            }
            Log.w("设置焦点完成", "");
        }
    }

    private void setRNextFocus() {
        mSearAppList_recycle.postDelayed(new Runnable() {
            @Override
            public void run() {
                setDefaultNextFocus();
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DCPage.onEntry(AppConstants.RESEARCH_PAGE);
        DCEvent.onEvent(AppConstants.RESEARCH_PAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DCPage.onExit(AppConstants.RESEARCH_PAGE);
        DCEvent.onEventDuration(AppConstants.RESEARCH_PAGE, mDuration);
    }

    @Override
    protected void onHomeKeyDown() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mFocusMoveUtil.release();
    }
}
