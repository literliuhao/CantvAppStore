package com.can.appstore.applist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Topic;
import com.can.appstore.upgrade.UpgradeService;
import java.util.List;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.PromptUtils;
import cn.can.tvlib.utils.ToastUtils;

/**
 * Created by syl on 2016/10/18.
 * 应用列表页和排行榜列表页
 */

public class AppListActivity extends Activity implements AppListContract.View {
    private static final String TAG = "AppListActivity";
    public static final String PAGE_TYPE = "pageType";
    public static final String TYPE_ID = "typeId";
    public static final String TOPIC_ID = "topicId";
    public static final int APPLICATION = 0;
    public static final int RANKING = 1;
    public static final int MENU_RECYCLERVIEW_DIVIDER = 20; //menu列表行距
    public static final int APPLICATION_RECYCLERVIEW_DIVIDER = 32; //应用列表行距
    public static final int SPAN_COUNT = 3;     //应用列表每行app个数
    public static final int MIN_DOWN_INTERVAL = 150;//响应点击事件的最小间隔事件
    public static float MENU_FOCUS_SCALE = 1.0f;  //菜单焦点放大倍数
    public static float LIST_FOCUS_SCALE = 1.0f;   //应用列表焦点放大倍数；
    public static int ANIM_DURATION = 150;
    private CanRecyclerView mMenuRecyclerView;
    private CanRecyclerView mAppListRecyclerView;
    private LinearLayout mLoadFaliView;
    private TextView mLoadRetryView;
    private CanRecyclerViewAdapter menuAdapter;
    private CanRecyclerViewAdapter mAppListAdapter;
    private FocusMoveUtil mFocusMoveUtil;
    private AppListContract.Presenter mPresenter;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView mTvAppListLine;
    private TextView mTvSearch;
    private TextView mTvTypeName;
    private ImageView mIvArrowUp;
    private ImageView mIvArrowDown;
    private ImageView mIvMenuTopShadow;
    private ImageView mIvMenuBottomShadow;
    private GridLayoutManager mGridLayoutManager;
    private View mAppListSelectItem;
    private View mMenuSelectItem;
    private Runnable mMenuRunnable;
    private Runnable mAppRunnable;
    private Runnable mChangeAppBgRunnable;
    private Handler mHandler;
    private Dialog mLoadDialog;
    private boolean isComeFromSearch;
    private boolean isMenuCanScroll;
    private int mFromType;
    private long mTime;
    private int mMenuSize;
    private int mTopPadding;
    private int mBottomPadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        mFromType = getIntent().getIntExtra(PAGE_TYPE, APPLICATION);
        String typId = getIntent().getStringExtra(TYPE_ID);
        String topicId = getIntent().getStringExtra(TOPIC_ID);

        initView();
        initRecyclerView();
        initFocusView();
        initHandlerRunnable();
        initListener();
        AppListPresenter appListPresenter = new AppListPresenter(this, this, mFromType, typId, topicId);
        appListPresenter.startLoadData();

    }

    private void initView() {

        mTvTypeName = (TextView) findViewById(R.id.tv_type_name);
        mMenuRecyclerView = (CanRecyclerView) findViewById(R.id.rv_app_menu);
        mAppListRecyclerView = (CanRecyclerView) findViewById(R.id.rv_app_list);
        mTvAppListLine = (TextView) findViewById(R.id.tv_app_list_line);
        mTvSearch = (TextView) findViewById(R.id.tv_app_list_search);

        mLoadFaliView = (LinearLayout) findViewById(R.id.ll_app_list_load_fail);
        mLoadRetryView = (TextView) findViewById(R.id.tv_load_retry);

        mIvArrowUp = (ImageView) findViewById(R.id.iv_arrow_up);
        mIvArrowDown = (ImageView) findViewById(R.id.iv_arrow_down);
        mIvMenuTopShadow = (ImageView) findViewById(R.id.iv_menu_top_shadow);
        mIvMenuBottomShadow = (ImageView) findViewById(R.id.iv_menu_bottom_shadow);

        /**
         * 应用列表页，显示搜索，默认显示
         * 排行榜列表页，隐藏搜索
         */
        if (mFromType == RANKING) {
            mTvSearch.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView() {
        //左侧recyclerView
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMenuRecyclerView.setLayoutManager(mLinearLayoutManager);
        // 设置item分
        mMenuRecyclerView.addItemDecoration(new CanRecyclerViewDivider(MENU_RECYCLERVIEW_DIVIDER));
        // 设置item动画
        mMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //右侧recyclerView
        mGridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        mAppListRecyclerView.setLayoutManager(mGridLayoutManager);
        // 设置item分
        mAppListRecyclerView.addItemDecoration(new CanRecyclerViewDivider(APPLICATION_RECYCLERVIEW_DIVIDER));
        // 设置item动画
        mAppListRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initHandlerRunnable() {
        mMenuRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMenuSelectItem != null) {
                    mFocusMoveUtil.startMoveFocus(mMenuSelectItem, MENU_FOCUS_SCALE);
                }
            }
        };
        mAppRunnable = new Runnable() {
            @Override
            public void run() {
                if (mAppListSelectItem != null) {
                    mFocusMoveUtil.startMoveFocus(mAppListSelectItem, LIST_FOCUS_SCALE);
                }
            }
        };
        mChangeAppBgRunnable = new Runnable() {
            @Override
            public void run() {
                if (mAppListSelectItem != null) {
                    mAppListSelectItem.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        };
        mHandler = new Handler();
    }

    private void initFocusView() {
        mMenuRecyclerView.setFocusable(false);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView().findViewById(android.R.id.content), R
                .mipmap.btn_focus);
        mFocusMoveUtil.hideFocus();
    }

    /**
     * 初始化部分监听，adapter相关监听在adapter初始化后添加
     */
    private void initListener() {

        mMenuRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(isMenuCanScroll){
                    int l =  mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int f =  mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int fv = mLinearLayoutManager.findFirstVisibleItemPosition();
                    int lv = mLinearLayoutManager.findLastVisibleItemPosition();
                    Log.d(TAG, "onScrolled: "+f+"---"+l+"-----"+fv +"-----"+lv+"-----"+mMenuSize);
                    if(fv > 1){
                        mIvArrowUp.setVisibility(View.VISIBLE);
                        mIvMenuTopShadow.setVisibility(View.VISIBLE);
                    }else{
                        mIvArrowUp.setVisibility(View.GONE);
                        mIvMenuTopShadow.setVisibility(View.GONE);
                    }

                    if(lv < mMenuSize-1){
                        mIvArrowDown.setVisibility(View.VISIBLE);
                        mIvMenuBottomShadow.setVisibility(View.VISIBLE);
                    }else{
                        mIvArrowDown.setVisibility(View.GONE);
                        mIvMenuBottomShadow.setVisibility(View.GONE);
                    }

                    int t;
                    int b;

                    if(f > 1){
                        t = 100;
                    }else{
                        t = 0;
                    }

                    if(l < mMenuSize -1){
                        b = 100;
                    }else{
                        b = 0;
                    }
                    if(mTopPadding != t || mBottomPadding != b){
                        mTopPadding = t;
                        mBottomPadding = b;
                        mMenuRecyclerView.setPadding(0, t, 0, b);
                    }
                }

                if (dx == 0 && dy == 0) {
                    return;
                }
                if (isComeFromSearch) {
                    return;
                }
                mHandler.removeCallbacks(mMenuRunnable);
                mHandler.postDelayed(mMenuRunnable, 30);
            }
        });

        mAppListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ImageLoader.getInstance().resumeAllTask(AppListActivity.this);//恢复所有加载
                    mMenuRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                    //获取最后一个显示的item和item总数
                    int lastPosition = mGridLayoutManager.findLastVisibleItemPosition();
                    int totalItemCount = mGridLayoutManager.getItemCount();
                    //根据最后一个显示的item判断是否已经滑动到底部
                    if (lastPosition == totalItemCount - 1) {
                        if (lastPosition < (mPresenter.getAppListTotalSize() - 1)) {
                            ToastUtils.showMessage(AppListActivity.this, "加载更多！");
                            mPresenter.loadMoreData();
                        } else {
                            ToastUtils.showMessage(AppListActivity.this, "没有更多了");
                        }
                    }
                } else {
                    ImageLoader.getInstance().pauseAllTask(AppListActivity.this);//暂停所有加载
                    mMenuRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx == 0 && dy == 0) {
                    return;
                }
                mHandler.removeCallbacks(mAppRunnable);
                mHandler.postDelayed(mAppRunnable, 30);
                mHandler.removeCallbacks(mChangeAppBgRunnable);
                mHandler.postDelayed(mChangeAppBgRunnable, ANIM_DURATION);
            }
        });

        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/11/8  跳转到搜索页面
                ToastUtils.showMessage(AppListActivity.this, "跳转搜索页面！");
            }
        });

        mTvSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(view, MENU_FOCUS_SCALE);
                    isComeFromSearch = true;
                } else {
                    mTvSearch.setFocusable(false);
                }
            }
        });

        mLoadRetryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                mPresenter.loadAppListData();
            }
        });
        mLoadRetryView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mMenuSelectItem != null) {
                    mMenuSelectItem.setBackgroundResource(R.drawable.shap_app_list_menu);
                } else if (mMenuSelectItem != null) {
                    mMenuSelectItem.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        mLoadRetryView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT &&
                        mMenuSelectItem != null) {
                    mMenuSelectItem.requestFocus();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void setPresenter(AppListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshMenuList(final List<Topic> menuData, final int focusPosition) {
        menuAdapter = new AppListMenuAdapter(this, menuData);
        mMenuRecyclerView.setAdapter(menuAdapter);
        menuAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    if (isComeFromSearch) {
                        isComeFromSearch = false;
                        if (mMenuSelectItem != null) {
                            mMenuSelectItem.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    if (mMenuSelectItem != view) {
                        showLoadingDialog();
                        mAppListRecyclerView.setVisibility(View.INVISIBLE);
                        mLoadFaliView.setVisibility(View.GONE);
                    }
                    mPresenter.onMenuItemSelect(position);
                    mMenuSelectItem = view;
                    mHandler.removeCallbacks(mMenuRunnable);
                    mHandler.postDelayed(mMenuRunnable, 30);
                }
            }

            @Override
            public boolean onFocusMoveOutside(int currFocus, int direction) {
                if (direction == View.FOCUS_RIGHT) {
                    if (mAppListRecyclerView.getVisibility() == View.VISIBLE) {
                        if (mAppListSelectItem == null) {
                            mAppListRecyclerView.getChildAt(0).requestFocus();
                        } else {
                            mAppListSelectItem.requestFocus();
                        }
                    } else {
                        mLoadRetryView.requestFocus();
                        mFocusMoveUtil.startMoveFocus(mLoadRetryView, 1.0f);
                    }
                    //}else if(direction == View.FOCUS_UP){
                } else if (direction == 33) {
                    mHandler.removeCallbacks(mMenuRunnable);
                    mTvSearch.setFocusable(true);
                    mTvSearch.requestFocus();
                }

                if (mMenuSelectItem != null && (direction == View.FOCUS_RIGHT || direction == 33)) {
                    mMenuSelectItem.setBackgroundResource(R.drawable.shap_app_list_menu);
                }

                return true;
            }
        });
        mMenuRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFocusMoveUtil.setFocusView(mMenuRecyclerView.getChildAt(focusPosition));
                mFocusMoveUtil.showFocus();
                mMenuRecyclerView.getChildAt(focusPosition).requestFocus();
            }
        }, 100);

        mMenuRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int s =  mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                mMenuSize = menuData.size()-1;
                if(s < menuData.size()-1){
                    mIvArrowDown.setVisibility(View.VISIBLE);
                    mIvMenuBottomShadow.setVisibility(View.VISIBLE);
                    isMenuCanScroll = true;
                    mMenuRecyclerView.setPadding(0, 0, 0, 100);
                }
            }
        }, 500);
    }

    @Override
    public void refreshAppList(List<AppInfo> appListData, int insertPosition) {
        hideLoadingDialog();
        if (mAppListAdapter == null) {
            mAppListAdapter = new AppListInfoAdapter(this, appListData);
            mAppListRecyclerView.setAdapter(mAppListAdapter);
            mAppListAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
                @Override
                public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                    final View selectView = view;
                    if (hasFocus) {
                        mAppListSelectItem = view;
                        mPresenter.onAppListItemSelectChanged(position);
                        mHandler.removeCallbacks(mAppRunnable);
                        mHandler.postDelayed(mAppRunnable, 30);
                        mHandler.postDelayed(mChangeAppBgRunnable,ANIM_DURATION);
//                        mAppListRecyclerView.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                selectView.setBackgroundColor(Color.TRANSPARENT);
//                            }
//                        },ANIM_DURATION);
                    }else{
                        mHandler.removeCallbacks(mChangeAppBgRunnable);
                        selectView.setBackgroundResource(R.drawable.shap_app_list);
                    }
                }

                @Override
                public boolean onFocusMoveOutside(int currFocus, int direction) {
                    if (direction == View.FOCUS_LEFT) {
                        if (isComeFromSearch) {
                            mTvSearch.setFocusable(true);
                            mTvSearch.requestFocus();
                            return true;
                        }
                        mMenuSelectItem.requestFocus();
                        mMenuSelectItem.setBackgroundColor(Color.TRANSPARENT);
                        return true;
                    }
                    if (mAppListRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                        return true;
                    }
                    return false;
                }
            });
            mAppListAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position, Object data) {
                    Intent intent = new Intent(AppListActivity.this, UpgradeService.class);
                    AppListActivity.this.startService(intent);
                }
            });
        } else if (insertPosition == AppListPresenter.REFRESH_APP) {
            //处理没有数据的情况
            if (appListData.size() == 1 && appListData.get(0).getId() == null) {
                mAppListRecyclerView.setVisibility(View.GONE);
                mTvAppListLine.setVisibility(View.GONE);
                return;
            } else {
                mAppListRecyclerView.setVisibility(View.VISIBLE);
                mTvAppListLine.setVisibility(View.VISIBLE);
            }

            mAppListSelectItem = null;
            // TODO: 2016/11/14 删除
            mAppListAdapter.setDatas(appListData);
            mAppListAdapter.notifyDataSetChanged();
            mMenuRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAppListRecyclerView.scrollToPosition(0);
                }
            }, 20);
        } else {
            mAppListAdapter.setDatas(appListData);
            mAppListAdapter.notifyItemInserted(insertPosition);
        }

        if (mLoadFaliView.getVisibility() == View.VISIBLE && mMenuSelectItem != null) {
            mMenuSelectItem.requestFocus();
            mMenuSelectItem.setBackgroundColor(Color.TRANSPARENT);
        }

        if (mAppListRecyclerView.getVisibility() != View.VISIBLE) {
            mMenuRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAppListRecyclerView.setVisibility(View.VISIBLE);
                    mTvAppListLine.setVisibility(View.VISIBLE);
                    mLoadFaliView.setVisibility(View.GONE);
                }
            },1000);
        }

    }

    @Override
    public void refreshLineText(SpannableStringBuilder spannable) {
        mTvAppListLine.setText(spannable);
    }

    @Override
    public void refreshTypeName(String typeName) {
        mTvTypeName.setText(typeName);
    }

    @Override
    public void onLoadFail() {
        hideLoadingDialog();
        mAppListRecyclerView.setVisibility(View.INVISIBLE);
        mLoadFaliView.setVisibility(View.VISIBLE);
        mTvAppListLine.setVisibility(View.GONE);

    }

    @Override
    public void showLoadingDialog() {
        if (mLoadDialog == null) {
            mLoadDialog = PromptUtils.showLoadingDialog(this);
        }
        mLoadDialog.show();
    }

    @Override
    public void hideLoadingDialog() {
        if (mLoadDialog != null && mLoadDialog.isShowing()) {
            mLoadDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadDialog != null) {
            mLoadDialog.dismiss();
        }
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
        }
    }

    /**
     * 1 处理滑动过快问题
     * 2 应用列表刷新的时候拦截向右移动操作
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && mLoadDialog != null && mLoadDialog.isShowing()) {
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            long time = System.currentTimeMillis();
            if (mTime == 0) {
                mTime = System.currentTimeMillis();
                return super.dispatchKeyEvent(event);
            } else if (time - mTime < MIN_DOWN_INTERVAL) {
                Log.d(TAG, "dispatchKeyEvent: " + System.currentTimeMillis());
                return true;
            } else {
                mTime = System.currentTimeMillis();
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
