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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.util.LogTime;
import com.can.appstore.R;
import com.can.appstore.upgrade.UpgradeService;

import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
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
    public static final int APPLICATION = 0;
    public static final int RANKING = 1;
    public static final int MENU_RECYCLERVIEW_DIVIDER = 20; //列表行距
    public static final int APPLICATION_RECYCLERVIEW_DIVIDER = 32; //列表行距
    public static final int SPAN_COUNT = 3;     //应用列表每行app个数
    public static int REFRESH_APP_DELAY_TIME = 1000;   //刷新应用列表信息延迟时间
    public static float MENU_FOCUS_SCALE = 1.0f;  //菜单焦点放大倍数
    public static float LIST_FOCUS_SCALE = 1.0f;   //应用列表焦点放大倍数；
    public static final String FROM_TYPE = "fromType";
    public static final String TYPE_ID = "typeId";
    public static final String TOPIC_ID = "topicId";
    private int mFromType;
    private CanRecyclerView mMenuRecyclerView;
    private CanRecyclerView mAppListRecyclerView;
    private LinearLayout mLoadFaliView;
    private TextView mLoadRetryView;
    private CanRecyclerViewAdapter menuAdapter;
    private CanRecyclerViewAdapter mAppListAdapter;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mFocusScaleUtil;
    private AppListContract.Presenter mPresenter;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView mTvAppListLine;
    private TextView mTvSearch;
    private GridLayoutManager mGridLayoutManager;
    private View mAppListSelectItem;
    private View mMenuSelectItem;
    private Runnable mRunnable;
    private Handler mHandler;
    private Dialog mLoadDialog;
    private boolean isComeFromSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        mFromType = getIntent().getIntExtra(FROM_TYPE, APPLICATION);
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
        mMenuRecyclerView = (CanRecyclerView) findViewById(R.id.rv_app_menu);
        mAppListRecyclerView = (CanRecyclerView) findViewById(R.id.rv_app_list);
        mTvAppListLine = (TextView) findViewById(R.id.tv_app_list_line);
        mTvSearch = (TextView) findViewById(R.id.tv_app_list_search);

        mLoadFaliView = (LinearLayout) findViewById(R.id.ll_app_list_load_fail);
        mLoadRetryView = (TextView) findViewById(R.id.tv_load_retry);
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
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mAppListSelectItem != null) {
                    mFocusMoveUtil.startMoveFocus(mAppListSelectItem, LIST_FOCUS_SCALE);
                    // mFocusScaleUtil.scaleToLarge(mAppListSelectItem, LIST_FOCUS_SCALE, LIST_FOCUS_SCALE);
                }
            }
        };
        mHandler = new Handler();
    }

    private void initFocusView() {
        mMenuRecyclerView.setFocusable(false);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView().findViewById(android.R.id.content), R
                .mipmap.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
    }

    /**
     * 初始化部分监听，adapter相关监听在adapter初始化后添加
     */
    private void initListener() {
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
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 30);
            }
        });

        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/11/8  跳转到搜索页面
                ToastUtils.showMessage(AppListActivity.this,"跳转搜索页面！");
            }
        });

        mTvSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    mFocusMoveUtil.startMoveFocus(view, MENU_FOCUS_SCALE);
                    isComeFromSearch = true;
                }
            }
        });

        mLoadRetryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public void refreshMenuList(List<AppListMenuInfo> menuData, final int focusPosition) {
        menuAdapter = new AppListMenuAdapter(this, menuData);
        mMenuRecyclerView.setAdapter(menuAdapter);
        menuAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    if(isComeFromSearch){
                        isComeFromSearch = false;
                        if(mMenuSelectItem != null){
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
                    mFocusMoveUtil.startMoveFocus(view, MENU_FOCUS_SCALE);
                    mFocusScaleUtil.scaleToLarge(view, MENU_FOCUS_SCALE, MENU_FOCUS_SCALE);
                } else {
                    mFocusScaleUtil.scaleToNormal(view);
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
                }else if(direction == 33){
                    mTvSearch.requestFocus();
                }

                if(mMenuSelectItem != null && (direction == View.FOCUS_RIGHT || direction == 33)){
                    mMenuSelectItem.setBackgroundResource(R.drawable.shap_app_list_menu);
                }

                return true;
            }
        });
        mMenuRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMenuRecyclerView.getChildAt(focusPosition).requestFocus();
            }
        }, 100);
    }

    @Override
    public void refreshAppList(List<AppListInfo> appListData, int insertPosition) {

        if (mAppListAdapter == null) {
            mAppListAdapter = new AppListInfoAdapter(this, appListData);
            mAppListRecyclerView.setAdapter(mAppListAdapter);
            mAppListAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
                @Override
                public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                    if (hasFocus) {
                        mAppListSelectItem = view;
                        mPresenter.onAppListItemSelectChanged(position);
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.postDelayed(mRunnable, 30);
                    } else {
                        mFocusScaleUtil.scaleToNormal(view);
                    }
                }

                @Override
                public boolean onFocusMoveOutside(int currFocus, int direction) {
                    if (direction == View.FOCUS_LEFT) {
                        if(isComeFromSearch){
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
            mAppListSelectItem = null;
            mAppListAdapter.setDatas(appListData);
            mAppListAdapter.notifyDataSetChanged();
            /**
             * 防止刷新应用列表的时候丢失焦点的问题
             */
            mMenuRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAppListRecyclerView.scrollToPosition(0);
                }
            }, 20);
        }else{
            mAppListAdapter.setDatas(appListData);
            mAppListAdapter.notifyItemInserted(insertPosition);
        }

        if(mLoadFaliView.getVisibility() == View.VISIBLE && mMenuSelectItem != null){
            mMenuSelectItem.requestFocus();
            mMenuSelectItem.setBackgroundColor(Color.TRANSPARENT);
        }

        if (mAppListRecyclerView.getVisibility() != View.VISIBLE) {
            mAppListRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAppListRecyclerView.setVisibility(View.VISIBLE);
                    mTvAppListLine.setVisibility(View.VISIBLE);
                    mLoadFaliView.setVisibility(View.GONE);
                }
            }, 1000);
        }

    }

    @Override
    public void refreshLineText(SpannableStringBuilder spannable) {
        mTvAppListLine.setText(spannable);
    }

    @Override
    public void onLoadFail() {
        hideLoadingDialog();
        mAppListRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAppListRecyclerView.setVisibility(View.INVISIBLE);
                mLoadFaliView.setVisibility(View.VISIBLE);
                mTvAppListLine.setVisibility(View.GONE);
            }
        }, 1000);

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
            mAppListRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLoadDialog.dismiss();
                }
            }, REFRESH_APP_DELAY_TIME);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && mLoadDialog.isShowing()) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
