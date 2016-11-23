package com.can.appstore.applist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.applist.adpter.*;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Topic;
import com.can.appstore.search.SearchActivity;
import com.can.appstore.upgrade.UpgradeService;

import java.util.HashMap;
import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.ToastUtils;

/**
 * Created by syl on 2016/10/18.
 * 应用列表页和排行榜列表页
 */
public class AppListActivity extends BaseActivity implements AppListContract.View, View.OnFocusChangeListener, View
        .OnClickListener, View.OnKeyListener {

    private static final String TAG = "AppListActivity";
    // 入口需要提供的参数
    public static final String ENTRY_KEY_SRC_TYPE = "srcType"; // 入口类型，即本页面展示数据类型
    public static final String ENTRY_KEY_TYPE_ID = "typeId"; // 一级分类id
    public static final String ENTRY_KEY_TOPIC_ID = "topicId"; // 默认获取焦点的二级分类id
    public static final String ENTRY_KEY_APP_ID = "appId"; //  应用详情id
    // handler msg.what
    private final int MSG_HIDE_MENU_TOP_SHADOW = 0x101;
    private final int MSG_HIDE_MENU_BOTTOM_SHADOW = 0x102;
    // 页面类型
    private static final int PAGE_TYPE_ILLEGAL = 0x100;//非法页面类型
    public static final int PAGE_TYPE_APP_LIST = 0x101;//页面类型 （应用列表）
    public static final int PAGE_TYPE_RANKING = 0x102;//页面类型  （排行榜）
    //显示隐藏的UI的类型
    public static final int SHOW_APP_LIST = 1;//显示隐藏的应用列表
    public static final int SHOW_FAIL_VIEW = 2;//显示隐藏的加载失败UI
    // menu相关
    public static final int MENU_DIVIDER_SIZE = 20; //menu列表行距
    public static float MENU_FOCUS_SCALE = 1.0f;  //菜单焦点放大倍数
    // 右侧应用列表相关
    public static final int APP_LIST_DIVIDER_SIZE = 32; //应用列表行距
    public static final int APP_LIST_SPAN_COUNT = 3;     //应用列表每行app个数
    public static float APP_LIST_FOCUS_SCALE = 1.0f;   //应用列表焦点放大倍数；
    public static int CHANGE_FOCUSED_VIEW_BG_DELAY = 150;//右侧应用列表焦点移动时，修改背景为透明延迟的时间
    // 全局配置参数
    public static final int MIN_LOADING_SHOW_TIME = 1000; //loading消失延迟时间
    // 限制焦点框移动范围
    public static int NO_LIMIT_REGION = 1; //限制焦点移动范围的类型   （不限制）

    private TextView mSearchBtn;
    private TextView mTileTv;
    private TextView mLineNumTv;
    // 左侧菜单
    private CanRecyclerView mMenu;
    private CanRecyclerViewAdapter mMenuAdapter;
    private LinearLayoutManager mMenuLM;
    private ImageView mMenuArrowUp;
    private ImageView mMenuArrowDown;
    private ImageView mMenuTopShadow;
    private ImageView mMenuBottomShadow;
    private boolean menuTopArrowShowing;
    private boolean menuBottomArrowShowing;
    private View mSelectedMenuChild;
    private Rect mMenuFocusRegion;
    private int mCurrMenuPaddingTop;
    private int mCurrMenuPaddingBottom;
    private int mMenuPaddingTopTmp;
    private int mMenuPaddingBottomTmp;
    // 右侧应用列表
    private CanRecyclerView mAppList;
    private CanRecyclerViewAdapter mAppListAdapter;
    private GridLayoutManager mAppListLM;
    private LinearLayout mLoadFailView;
    private TextView mRetryBtn;
    private View mSelectedAppListChild;
    private Rect listRect;
    // 全局参数
    private AppListContract.Presenter mPresenter;
    private FocusMoveUtil mFocusMoveUtil;
    private Handler mHandler;
    private Runnable mSelectRunable;
    private Runnable mChangeAppBgRunnable;
    private int mPageType;
    private String mTypeId;
    private String mTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (pageTypeIllegal()) {
            finish();
            Log.i(TAG, "onCreate: Illegal pageType !!!");
            return;
        }

        parseIntentData();
        setupContentView();
        initGlobalData();

        new AppListPresenter(this, mPageType, mTypeId, mTopicId).startLoadData();
    }

    private boolean pageTypeIllegal() {
        int pageType = getIntent().getIntExtra(ENTRY_KEY_SRC_TYPE, PAGE_TYPE_ILLEGAL);
        if (pageType == PAGE_TYPE_ILLEGAL) {
            return true;
        }
        mPageType = pageType;
        return false;
    }

    private void parseIntentData() {
        mTypeId = getIntent().getStringExtra(ENTRY_KEY_TYPE_ID);
        mTopicId = getIntent().getStringExtra(ENTRY_KEY_TOPIC_ID);
    }

    private void setupContentView() {
        setContentView(R.layout.activity_app_list);

        mTileTv = (TextView) findViewById(R.id.tv_type_name);
        mLineNumTv = (TextView) findViewById(R.id.tv_app_list_line);
        if (mPageType == PAGE_TYPE_APP_LIST) {
            // 应用列表页，显示搜索按钮
            mSearchBtn = (TextView) findViewById(R.id.tv_app_list_search);
            mSearchBtn.setVisibility(View.INVISIBLE);
            mSearchBtn.setOnClickListener(this);
            mSearchBtn.setOnFocusChangeListener(this);
            mSearchBtn.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != KeyEvent.ACTION_DOWN) {
                        return false;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        final View firstMenuChild = mMenuLM.getChildAt(0);
                        if (firstMenuChild != null) {
                            firstMenuChild.requestFocus();
                            refreshMenuPaddingWithFocusRegion();
                            mMenu.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    firstMenuChild.setBackgroundResource(android.R.color.transparent);
                                }
                            }, CHANGE_FOCUSED_VIEW_BG_DELAY);
                            return true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        menuFocusMoveToRight();
                        return true;
                    }
                    return false;
                }
            });
        }
        mLoadFailView = (LinearLayout) findViewById(R.id.ll_app_list_load_fail);
        mRetryBtn = (TextView) findViewById(R.id.tv_load_retry);
        mMenu = (CanRecyclerView) findViewById(R.id.rv_app_menu);
        mMenuArrowUp = (ImageView) findViewById(R.id.iv_arrow_up);
        mMenuArrowDown = (ImageView) findViewById(R.id.iv_arrow_down);
        mMenuTopShadow = (ImageView) findViewById(R.id.iv_menu_top_shadow);
        mMenuBottomShadow = (ImageView) findViewById(R.id.iv_menu_bottom_shadow);
        mAppList = (CanRecyclerView) findViewById(R.id.rv_app_list);

        configMenu();
        configAppList();

        mRetryBtn.setOnClickListener(this);
        mRetryBtn.setOnFocusChangeListener(this);
        mRetryBtn.setOnKeyListener(this);
    }

    private void configMenu() {
        mMenu.setFocusable(false);
        mMenu.addItemDecoration(new CanRecyclerViewDivider(MENU_DIVIDER_SIZE));
        mMenu.setItemAnimator(new DefaultItemAnimator());
        mMenuLM = new LinearLayoutManager(this);
        mMenu.setLayoutManager(mMenuLM);
        mMenu.setKeyCodeEffectInterval(CanRecyclerView.KEYCODE_EFFECT_INTERVAL_NORMAL);
        mMenu.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx == 0 && dy == 0) {
                    return;
                }

                if (mSelectedMenuChild != null) {
                    mFocusMoveUtil.startMoveFocus(mSelectedMenuChild, MENU_FOCUS_SCALE);
                }

                if (dy > 0) {
                    int firstVisibleChild = mMenuLM.findFirstCompletelyVisibleItemPosition();
                    if (firstVisibleChild != 0) {
                        showMenuTopArrow();
                        mCurrMenuPaddingTop = mMenuPaddingTopTmp;
                        refreshMenuPaddingWithFocusRegion();
                    }

                } else {
                    int l = mMenuLM.findLastCompletelyVisibleItemPosition();
                    int lastChildPosi = mMenuAdapter.getItemCount() - 1;
                    if (l != lastChildPosi && !menuBottomArrowShowing) {
                        showMenuBottomArrow();
                        mCurrMenuPaddingBottom = mMenuPaddingBottomTmp;
                        refreshMenuPaddingWithFocusRegion();
                    }
                }
            }
        });
    }

    @Override
    public void showSearchView() {
        mSearchBtn.setVisibility(View.VISIBLE);
    }

    private void showMenuTopArrow() {
        if (menuTopArrowShowing) {
            return;
        }
        menuTopArrowShowing = true;
        mMenuArrowUp.setVisibility(View.VISIBLE);
        mMenuTopShadow.setVisibility(View.VISIBLE);
    }

    private void showMenuBottomArrow() {
        if (menuBottomArrowShowing) {
            return;
        }
        menuBottomArrowShowing = true;
        mMenuArrowDown.setVisibility(View.VISIBLE);
        mMenuBottomShadow.setVisibility(View.VISIBLE);
    }

    private void configAppList() {
        mAppListLM = new GridLayoutManager(this, APP_LIST_SPAN_COUNT);
        mAppList.setLayoutManager(mAppListLM);
        mAppList.setKeyCodeEffectInterval(CanRecyclerView.KEYCODE_EFFECT_INTERVAL_NORMAL);
        mAppList.addItemDecoration(new CanRecyclerViewDivider(APP_LIST_DIVIDER_SIZE));
        mAppList.setItemAnimator(new DefaultItemAnimator());
        mAppList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 恢复加载图片
                    ImageLoader.getInstance().resumeAllTask(AppListActivity.this);
                    // 取消屏蔽menu焦点
                    mMenu.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                    // 分页加载,目前只有应用列表页面有分页加载
                    if (mPageType != PAGE_TYPE_APP_LIST) {
                        return;
                    }
                    int lastPosition = mAppListLM.findLastVisibleItemPosition();
                    int totalItemCount = mAppListLM.getItemCount();
                    if (lastPosition == totalItemCount - 1) {
                        mPresenter.loadMoreData();
                    }
                } else {
                    // 暂停加载图片
                    ImageLoader.getInstance().pauseAllTask(AppListActivity.this);
                    // 屏蔽menu焦点
                    mMenu.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx == 0 && dy == 0) {
                    return;
                }
                // 延迟更新当前获取焦点的view的背景
                mHandler.removeCallbacks(mChangeAppBgRunnable);
                mHandler.postDelayed(mChangeAppBgRunnable, CHANGE_FOCUSED_VIEW_BG_DELAY);
            }
        });
    }

    private void initGlobalData() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_HIDE_MENU_TOP_SHADOW:
                        if (mCurrMenuPaddingTop != 0) {
                            mCurrMenuPaddingTop = 0;
                            refreshMenuPaddingWithFocusRegion();
                            hideMenuTopArrow();
                        }
                        if (msg.obj != null && (boolean) msg.obj) {
                            mMenu.post(new Runnable() {
                                @Override
                                public void run() {
                                    mMenu.getChildAt(0).requestFocus();
                                }
                            });
                        }
                        break;

                    case MSG_HIDE_MENU_BOTTOM_SHADOW:
                        if (mCurrMenuPaddingBottom != 0) {
                            mCurrMenuPaddingBottom = 0;
                            refreshMenuPaddingWithFocusRegion();
                            hideMenuBottomArrow();
                        }
                        if (msg.obj != null && (boolean) msg.obj) {
                            mMenu.post(new Runnable() {
                                @Override
                                public void run() {
                                    View lastMenuChild = mMenu.getChildAt(mMenuAdapter.getItemCount() - mMenuLM
                                            .findFirstVisibleItemPosition());
                                    if (lastMenuChild != null) {
                                        lastMenuChild.requestFocus();
                                    }
                                }
                            });
                        }
                        break;
                }
            }
        };
        mFocusMoveUtil = new FocusMoveUtil(this, R.mipmap.btn_focus, getWindow().getDecorView(), true);
        mChangeAppBgRunnable = new Runnable() {
            @Override
            public void run() {
                if (mSelectedAppListChild != null && mSelectedAppListChild.isFocused()) {
                    mSelectedAppListChild.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        };
        measureMenuFocusActiveRegion();
        measureAppListFocusActiveRegion();
    }

    private void measureMenuFocusActiveRegion() {
        mMenuFocusRegion = new Rect();
        mMenu.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mMenu.getLocationInWindow(location);
                Log.d(TAG, "measureMenuFocusActiveRegion: " + location[0] + "--" + location[1] + "-----" + mMenu
                        .getPaddingTop
                                ());
                mMenuFocusRegion.set(location[0], location[1] + mMenu.getPaddingTop(), location[0] + mMenu
                        .getMeasuredWidth(), location[1] + mMenu.getMeasuredHeight() - mMenu.getPaddingBottom() -
                        MENU_DIVIDER_SIZE);
            }
        });
    }

    private void measureAppListFocusActiveRegion() {
        listRect = new Rect();
        mAppList.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mAppList.getLocationInWindow(location);
                listRect.set(location[0], location[1] + mAppList.getPaddingTop(), location[0] + mAppList
                        .getMeasuredWidth(), location[1] + mAppList.getMeasuredHeight() - mAppList.getPaddingBottom()
                        - APP_LIST_DIVIDER_SIZE);
                mAppList.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void setPresenter(AppListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshMenuList(final List<Topic> menuData, final int focusPosition) {
        mMenuAdapter = new AppListMenuAdapter(menuData);
        mMenu.setAdapter(mMenuAdapter);
        mMenuAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                Log.d(TAG, "onItemFocusChanged: " + position);
                if (hasFocus) {
                    if (position == 1) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_HIDE_MENU_TOP_SHADOW));
                    } else if (position == mMenuAdapter.getItemCount() - 2) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_HIDE_MENU_BOTTOM_SHADOW));
                    }

                    mFocusMoveUtil.startMoveFocus(view, MENU_FOCUS_SCALE);

                    //刚进入页面的时候，不重新加载数据，记录view
                    //焦点从搜索下来时，不重新加载数据，记录view
                    if (mSearchBtn == mSelectedMenuChild || mSelectedMenuChild == null) {
                        mSelectedMenuChild = view;
                        return;
                    }

                    //view没有改变的时候，不重新加载数据，不记录view,不隐藏加载失败UI
                    if (mSelectedMenuChild == view) {
                        return;
                    }

                    showAppInfoLoadingDialog();

                    //隐藏显示的应用列表范围的UI(应用列表，行数，加载失败UI)
                    if (mLoadFailView.getVisibility() == View.VISIBLE) {
                        mLoadFailView.setVisibility(View.INVISIBLE);
                    }
                    if (mAppList.getVisibility() == View.VISIBLE) {
                        mAppList.setVisibility(View.INVISIBLE);
                    }
                    if (mLineNumTv.getVisibility() == View.VISIBLE) {
                        mLineNumTv.setVisibility(View.INVISIBLE);
                    }

                    mPresenter.onMenuItemSelect(position);
                    mSelectedMenuChild = view;
                }
            }

            @Override
            public boolean onFocusMoveOutside(int currFocus, int direction) {
                if (direction == View.FOCUS_RIGHT) {
                    menuFocusMoveToRight();
                    if (mSelectedMenuChild != null) {
                        mSelectedMenuChild.setBackgroundResource(R.drawable.shap_app_list_menu);
                    }
                    Log.d(TAG, "onFocusMoveOutside: View.FOCUS_RIGHT");

                } else if (direction == View.FOCUS_UP && mPageType == PAGE_TYPE_APP_LIST) {
                    if (mSelectedMenuChild != null) {
                        mSelectedMenuChild.setBackgroundResource(R.drawable.shap_app_list_menu);
                    }
                    refreshFocusActiveRegion(NO_LIMIT_REGION);
                    mSearchBtn.setFocusable(true);
                    mSearchBtn.requestFocus();
                    Log.d(TAG, "onFocusMoveOutside: FOCUS_UP");
                }
                Log.d(TAG, "onFocusMoveOutside: ");
                return true;
            }
        });
        mMenuAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                if (position == 1 && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    mHandler.removeMessages(MSG_HIDE_MENU_TOP_SHADOW);
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_HIDE_MENU_TOP_SHADOW, true));
                    return true;

                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && position == mMenuAdapter.getItemCount() - 2) {
                    mHandler.removeMessages(MSG_HIDE_MENU_BOTTOM_SHADOW);
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_HIDE_MENU_BOTTOM_SHADOW, true));
                    return true;
                }
                return false;
            }
        });


        mSelectRunable = new Runnable() {
            @Override
            public void run() {
                // showFocusView
                if(isDestroyed()){
                    Log.d(TAG, "refreshAppList: isDestroyed1");
                    return;
                }
                View child = mMenu.getChildAt(focusPosition);
                mFocusMoveUtil.setFocusView(child);
                mFocusMoveUtil.showFocus();
                child.requestFocus();

                // measure menu child view height
                int menuItemHeight = child != null ? child.getMeasuredHeight() : 0;
                mMenuPaddingTopTmp = menuItemHeight + MENU_DIVIDER_SIZE;
                mMenuPaddingBottomTmp = mMenuPaddingTopTmp;

                // judge if show menu bottom shadow
                int lastVisibleMenuPos = mMenuLM.findLastCompletelyVisibleItemPosition();
                int childCount = mMenuAdapter.getItemCount();
                if (lastVisibleMenuPos != childCount - 1) {
                    showMenuBottomArrow();
                    mCurrMenuPaddingBottom = mMenuPaddingBottomTmp;
                    refreshMenuPaddingWithFocusRegion();
                }
            }
        };

        mHandler.postDelayed(mSelectRunable,100);
    }

    private void hideMenuTopArrow() {
        if (menuTopArrowShowing) {
            menuTopArrowShowing = false;
            mMenuArrowUp.setVisibility(View.INVISIBLE);
            mMenuTopShadow.setVisibility(View.INVISIBLE);
        }
    }

    private void hideMenuBottomArrow() {
        if (menuBottomArrowShowing) {
            menuBottomArrowShowing = false;
            mMenuArrowDown.setVisibility(View.INVISIBLE);
            mMenuBottomShadow.setVisibility(View.INVISIBLE);
        }
    }

    private boolean menuFocusMoveToRight() {
        if (mAppList.getVisibility() == View.VISIBLE && mAppListAdapter.getItemCount() > 0) {
            Log.d(TAG, "menuFocusMoveToRight: ");
            mFocusMoveUtil.setFocusActiveRegion(listRect.left, listRect.top, listRect.right, listRect.bottom);
            if (mSelectedAppListChild == null) {
                mAppList.getChildAt(0).requestFocus();
            } else {
                mSelectedAppListChild.requestFocus();
            }
            return true;

        } else if (mRetryBtn.getVisibility() == View.VISIBLE) {
            refreshFocusActiveRegion(NO_LIMIT_REGION);
            mRetryBtn.requestFocus();
            return true;
        }
        return false;
    }

    private void refreshMenuPaddingWithFocusRegion() {
        Log.d(TAG, "refreshMenuPaddingWithFocusRegion: " + mMenuFocusRegion.left + "--" + (mMenuFocusRegion.top +
                mCurrMenuPaddingTop) + "--" + mMenuFocusRegion.right + "---" + (mMenuFocusRegion.bottom -
                mCurrMenuPaddingBottom));
        mMenu.setPadding(0, mCurrMenuPaddingTop, 0, mCurrMenuPaddingBottom);
        mFocusMoveUtil.setFocusActiveRegion(mMenuFocusRegion.left, mMenuFocusRegion.top + mCurrMenuPaddingTop,
                mMenuFocusRegion.right, mMenuFocusRegion.bottom - mCurrMenuPaddingBottom);
    }

    @Override
    public void refreshAppList(List<AppInfo> appListData, int insertPosition, long delayTime) {
        mSelectedAppListChild = null;
        if (mAppListAdapter == null) {
            mAppListAdapter = new com.can.appstore.applist.adpter.AppListInfoAdapter(appListData);
            mAppList.setAdapter(mAppListAdapter);
            mAppListAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
                @Override
                public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                    final View selectView = view;
                    if (hasFocus) {
                        mSelectedAppListChild = view;
                        mPresenter.onAppListItemSelectChanged(position);
                        mFocusMoveUtil.startMoveFocus(mSelectedAppListChild, APP_LIST_FOCUS_SCALE);
                        mHandler.postDelayed(mChangeAppBgRunnable, CHANGE_FOCUSED_VIEW_BG_DELAY);
                    } else {
                        mHandler.removeCallbacks(mChangeAppBgRunnable);
                        selectView.setBackgroundResource(R.drawable.shap_app_list);
                    }
                }

                @Override
                public boolean onFocusMoveOutside(int currFocus, int direction) {
                    if (direction == View.FOCUS_LEFT) {
                        if (mSearchBtn == mSelectedMenuChild) {
                            refreshFocusActiveRegion(NO_LIMIT_REGION);
                            mSearchBtn.setFocusable(true);
                            mSearchBtn.requestFocus();
                            mSearchBtn.setBackgroundColor(Color.TRANSPARENT);
                            return true;
                        }
                        if (mSelectedMenuChild != null) {
                            refreshMenuPaddingWithFocusRegion();
                            mSelectedMenuChild.requestFocus();
                            mMenu.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mSelectedMenuChild.setBackgroundColor(Color.TRANSPARENT);
                                }
                            }, CHANGE_FOCUSED_VIEW_BG_DELAY);
                            return true;
                        }
                    }
                    if (mAppList.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                        return true;
                    }
                    return false;
                }
            });
            mAppListAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position, Object data) {
                    HashMap map = mPresenter.getIds(position);
                    AppDetailActivity.actionStart(AppListActivity.this,(String)map.get(ENTRY_KEY_APP_ID),(String)map.get(ENTRY_KEY_TOPIC_ID));
                }
            });

        } else if (insertPosition == AppListPresenter.REFRESH_APP) {
            //处理没有数据的情况
            if (appListData.size() == 0) {
                mAppList.setVisibility(View.INVISIBLE);
                mLineNumTv.setVisibility(View.INVISIBLE);
                return;
            }
            mAppListAdapter.notifyDataSetChanged();
            mMenu.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAppList.scrollToPosition(0);
                }
            }, 20);
        } else {
            mAppListAdapter.notifyItemInserted(insertPosition);
        }

        if (mLoadFailView.getVisibility() == View.VISIBLE && mSelectedMenuChild != null) {
            refreshMenuPaddingWithFocusRegion();
            mSelectedMenuChild.requestFocus();
            mSelectedMenuChild.setBackgroundColor(Color.TRANSPARENT);
        }

        if (mAppList.getVisibility() != View.VISIBLE) {
            mMenu.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAppList.setVisibility(View.VISIBLE);
                    mLineNumTv.setVisibility(View.VISIBLE);
                    mLoadFailView.setVisibility(View.GONE);
                }
            }, delayTime);
        }
    }

    /**
     * @param type 需要限制焦点框移动范围的类型
     */
    public void refreshFocusActiveRegion(int type) {
        if (type == NO_LIMIT_REGION) {
            mFocusMoveUtil.setFocusActiveRegion(0, 0, MyApp.Width, MyApp.Height);
        }
    }

    @Override
    public void refreshRowNumber(SpannableStringBuilder spannable) {
        mLineNumTv.setText(spannable);
    }

    @Override
    public void refreshTypeName(String typeName) {
        if (mTileTv.getVisibility() != View.VISIBLE) {
            mTileTv.setVisibility(View.VISIBLE);
        }
        mTileTv.setText(typeName);
    }

    /**
     * 显示应用列表
     */
    @Override
    public void showAppList(){
        if(mAppList.getVisibility() != View.VISIBLE){
            mAppList.setVisibility(View.VISIBLE);
        }
        if (mLineNumTv.getVisibility() != View.VISIBLE) {
            mLineNumTv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示加载失败的UI
     */
    @Override
    public void showFailUI(){
        if(mLoadFailView.getVisibility() != View.VISIBLE ){
            mLoadFailView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏应用列表
     */
    @Override
    public void hideAppList(){
        if(mAppList.getVisibility() == View.VISIBLE){
            mAppList.setVisibility(View.INVISIBLE);
        }
        if (mLineNumTv.getVisibility() == View.VISIBLE) {
            mLineNumTv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.tv_app_list_search://搜索按钮
                if (hasFocus) {
                    mSelectedMenuChild = mSearchBtn;
                    mFocusMoveUtil.startMoveFocus(v, MENU_FOCUS_SCALE);
                } else {
                    mSearchBtn.setFocusable(false);
                }
                break;

            case R.id.tv_load_retry://重试按钮
                if (hasFocus && mSelectedMenuChild != null) {
                    mSelectedMenuChild.setBackgroundResource(R.drawable.shap_app_list_menu);
                    mFocusMoveUtil.startMoveFocus(v, MENU_FOCUS_SCALE);
                } else if (mSelectedMenuChild != null) {
                    mSelectedMenuChild.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_app_list_search:
                SearchActivity.startAc(this);
                break;
            case R.id.tv_load_retry:
                showAppInfoLoadingDialog();
                mPresenter.loadAppListData();
                if (mSelectedMenuChild != null) {
                    mSelectedMenuChild.requestFocus();
                    mLoadFailView.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v.getId() == R.id.tv_load_retry) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT &&
                    mSelectedMenuChild != null) {
                mSelectedMenuChild.requestFocus();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && isAppInfoLoadingDialogShowing()) {
            //应用列表刷新的时候拦截向右移动操作
            return true;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && mAppList != null && mAppList.getVisibility() != View
                .VISIBLE && mLoadFailView.getVisibility() != View.VISIBLE) {
            //应用列表右侧无数据,并且不是网络错误的时候拦截向右移动操作
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
        }

        if(mPresenter != null){
            Log.d(TAG, "onDestroy: mPresenter.release()");
            mPresenter.release();
        }

        if(mMenu != null){
            mMenu.removeCallbacks(mSelectRunable);
            Log.d(TAG, "onDestroy:removeCallbacks ");
        }
        super.onDestroy();
    }

    public static void actionStart(Context context, int pageType, String typeId, String topicId) {
        Intent intent = new Intent(context, AppListActivity.class);
        intent.putExtra(AppListActivity.ENTRY_KEY_SRC_TYPE, pageType);
        intent.putExtra(AppListActivity.ENTRY_KEY_TYPE_ID, typeId);
        intent.putExtra(AppListActivity.ENTRY_KEY_TOPIC_ID, topicId);
        context.startActivity(intent);
    }
}
