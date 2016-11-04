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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.upgrade.UpgradeService;

import java.util.List;

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
    public static final int RECYCLERVIEW_DIVIDER = 1; //列表行距
    public static final int SPAN_COUNT = 3;     //应用列表每行app个数
    public static int REFRESH_APP_DELAY_TIME = 500;   //刷新应用列表信息延迟时间
    public static float MENU_FOCUS_SCALE = 1.1f;  //菜单焦点放大倍数
    public static float LIST_FOCUS_SCALE = 1.1f;   //应用列表焦点放大倍数；
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
    private GridLayoutManager mGridLayoutManager;
    private View mAppListSelectItem;
    private View mMenuSelectItem;
    private Runnable mRunnable;
    private Handler mHandler;
    private Dialog mDialog;
    private boolean isAppListLoading;

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

        mLoadFaliView = (LinearLayout) findViewById(R.id.ll_app_list_load_fail);
        mLoadRetryView = (TextView) findViewById(R.id.tv_load_retry);
    }

    private void initRecyclerView() {
        //左侧recyclerView
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMenuRecyclerView.setLayoutManager(mLinearLayoutManager);
        // 设置item分
        mMenuRecyclerView.addItemDecoration(new CanRecyclerViewDivider(RECYCLERVIEW_DIVIDER));
        // 设置item动画
        mMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //右侧recyclerView
        mGridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        mAppListRecyclerView.setLayoutManager(mGridLayoutManager);
        // 设置item分
        mAppListRecyclerView.addItemDecoration(new CanRecyclerViewDivider(RECYCLERVIEW_DIVIDER));
        // 设置item动画
        mAppListRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initHandlerRunnable() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mAppListSelectItem != null) {
                    mFocusMoveUtil.startMoveFocus(mAppListSelectItem, LIST_FOCUS_SCALE);
                    mFocusScaleUtil.scaleToLarge(mAppListSelectItem, LIST_FOCUS_SCALE, LIST_FOCUS_SCALE);
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
     * 初始化部分监听，adpter相关监听在adpter初始化后添加
     */
    private void initListener() {
        mAppListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
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
                    mMenuRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //                if(dx == 0 && dy == 0){
                //                    return;
                //                }
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 50);
            }
        });
        mLoadRetryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showMessage(AppListActivity.this, "重新加载数据");
                mPresenter.loadAppListData();
            }
        });

        mLoadRetryView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT && mMenuSelectItem != null) {
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
                    if (position != 0 || mFromType != APPLICATION) {
                        mMenuSelectItem = view;
                    }
                    mPresenter.onMenuItemSelect(position);
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
                        if (mMenuSelectItem != null) {
                            mMenuSelectItem.setBackgroundResource(R.drawable.shap_app_list_menu);
                        }
                    } else {
                        mLoadRetryView.requestFocus();
                        mFocusMoveUtil.startMoveFocus(mLoadRetryView, 1.0f);
                    }
                }
                return true;
            }
        });
        menuAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                if (position == 0 && mFromType == APPLICATION) {
                    ToastUtils.showMessage(AppListActivity.this, "跳转搜索页面");
                }
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

        if (mAppListRecyclerView.getVisibility() != View.VISIBLE) {
            mAppListRecyclerView.setVisibility(View.VISIBLE);
            mTvAppListLine.setVisibility(View.VISIBLE);
            mLoadFaliView.setVisibility(View.GONE);
        }
        if (mAppListAdapter == null) {
            mAppListAdapter = new AppListInfoAdapter(this, appListData);
            mAppListRecyclerView.setAdapter(mAppListAdapter);
            mAppListAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
                @Override
                public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                    if (hasFocus) {
                        mAppListSelectItem = view;
                        view.setBackgroundColor(Color.TRANSPARENT);
                        mPresenter.onAppListItemSelectChanged(position);
                        //if(!isAppListLoading){
                        mHandler.postDelayed(mRunnable, 50);
                        // }
                    } else {
                        view.setBackgroundResource(R.drawable.shap_app_list);
                        mFocusScaleUtil.scaleToNormal(view);
                    }
                }

                @Override
                public boolean onFocusMoveOutside(int currFocus, int direction) {
                    if (direction == View.FOCUS_LEFT) {
                        mMenuSelectItem.requestFocus();
                        mMenuSelectItem.setBackgroundColor(Color.parseColor("#00FFFFFF"));
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
                    //                    Intent intent = new Intent( AppListActivity.this, PortalActivity.class);
                    //                    AppListActivity.this.startActivity(intent);
                }
            });
        } else if (insertPosition == AppListPresenter.REFRESH_APP) {
            //            isAppListLoading = true;
            mMenuSelectItem.requestFocus();
            mMenuSelectItem.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            mAppListAdapter.setDatas(appListData);
            mAppListAdapter.notifyDataSetChanged();
            /**
             * 防止刷新应用列表的时候丢失焦点的问题
             */
            mAppListSelectItem = null;
            mMenuRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAppListRecyclerView.scrollToPosition(0);
                }
            }, 20);

            //            if(true){
            //                mMenuRecyclerView.postDelayed(new Runnable() {
            //                    @Override
            //                    public void run() {
            //                        isAppListLoading = false;
            //                        mAppListRecyclerView.getChildAt(mLinearLayoutManager
            // .findFirstVisibleItemPosition()).requestFocus();
            //                    }
            //                },500);
            //            }else{
            //                isAppListLoading = false;
            //            }
        } else {
            mAppListAdapter.setDatas(appListData);
            mAppListAdapter.notifyItemInserted(insertPosition);
        }
    }

    @Override
    public void refreshLineText(SpannableStringBuilder spannable) {
        mTvAppListLine.setText(spannable);
    }

    @Override
    public void onLoadFail() {
        mAppListRecyclerView.setVisibility(View.GONE);
        mLoadFaliView.setVisibility(View.VISIBLE);
        mTvAppListLine.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = PromptUtils.showLoadingDialog(this);
        }
        mDialog.show();
    }

    @Override
    public void hideLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mAppListRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDialog.dismiss();
                }
            }, REFRESH_APP_DELAY_TIME);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
        }
    }
}
