package com.can.appstore.uninstallmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailContract;
import com.can.appstore.appdetail.custom.TextProgressBar;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.uninstallmanager.adapter.UninstallManagerAdapter;
import com.dataeye.sdk.api.app.DCEvent;
import com.dataeye.sdk.api.app.channel.DCPage;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.PackageUtil;

import static cn.can.tvlib.ui.view.recyclerview.CanRecyclerView.KEYCODE_EFFECT_INTERVAL_UNLIMIT;

/**
 * 本地卸载管理页面
 * Created by JasonF on 2016/10/13.
 */

public class UninstallManagerActivity extends BaseActivity implements UninstallManagerContract.View {
    public static final String TAG = "UninstallManagerActivi";
    public static final int APP_INSTALL_REFRESH_DELAYE = 200;//应用安装刷新此时选择的位置
    public static final int UNINSTALL_LAST_POSITION_DELAYE = 600;//卸载最后一个位置延时请求焦点
    private CanRecyclerView mCanRecyclerView;
    private FocusMoveUtil mFocusMoveUtil;
    private UninstallManagerAdapter mUninstallManagerAdapter;
    private View mFocusedListChild;
    private ListFocusMoveRunnable mListFocusMoveRunnable;
    private UninstallManagerPresenter mPresenter;
    private ArrayList<String> mSelectPackageName;
    private TextView mBtBatchUninstall;
    private TextView mTvItemCurRows;
    private TextProgressBar mProgressStorage;
    private TextView mSelectCount;
    private TextView mNotUninstallApp;
    private LinearLayout mLinearLayoutSelectApp;
    private boolean isSelect;
    private boolean isLastRemove = false;
    private CanRecyclerView.CanGridLayoutManager mLayoutManager;
    public static final int KEYCODE_EFFECT_INTERVAL_UNLIMIT = 0;
    public static final int KEYCODE_EFFECT_INTERVAL_NORMAL = 200;
    private long mLastKeyCodeTimePoint;
    private int keyCodeEffectInterval = KEYCODE_EFFECT_INTERVAL_NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===onCreate===");
        setContentView(R.layout.activity_uninstall_manager);
        mPresenter = new UninstallManagerPresenter(this, UninstallManagerActivity.this);
        initView();
        initFocus();
        mPresenter.startLoad(getSupportLoaderManager());
    }

    public void initFocus() {
        mFocusMoveUtil = new FocusMoveUtil(UninstallManagerActivity.this, getWindow().getDecorView(), R.mipmap.btn_focus);
        measureFocusActiveRegion();
        mListFocusMoveRunnable = new ListFocusMoveRunnable();
    }

    private void measureFocusActiveRegion() {
        mCanRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mCanRecyclerView.getLocationInWindow(location);
                //noinspection deprecation
                mFocusMoveUtil.setFocusActiveRegion(0, location[1] + mCanRecyclerView.getPaddingTop(), getWindowManager().
                        getDefaultDisplay().getWidth(), location[1] + mCanRecyclerView.getMeasuredHeight()
                        - getResources().getDimensionPixelSize(R.dimen.dimen_32px));
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "===onResume===");
        super.onResume();
        DCPage.onEntry(AppConstants.UNINSTALL_MANAGE);
        DCEvent.onEvent(AppConstants.UNINSTALL_MANAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DCPage.onExit(AppConstants.UNINSTALL_MANAGE);
        DCEvent.onEventDuration(AppConstants.UNINSTALL_MANAGE, mDuration);
    }

    private void initView() {
        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.crlv_grid);
        mBtBatchUninstall = (TextView) findViewById(R.id.bt_batch_uninstall);
        mTvItemCurRows = (TextView) findViewById(R.id.tv_cur_rows);
        mProgressStorage = (TextProgressBar) findViewById(R.id.progress_stroage);
        mSelectCount = (TextView) findViewById(R.id.tv_select_count);
        mNotUninstallApp = (TextView) findViewById(R.id.tv_no_data);
        mLinearLayoutSelectApp = (LinearLayout) findViewById(R.id.ll_select_app);
        mLayoutManager = new CanRecyclerView.CanGridLayoutManager(UninstallManagerActivity.this, 3, CanRecyclerView.CanGridLayoutManager.VERTICAL, false);
        mCanRecyclerView.setLayoutManager(mLayoutManager);
        mCanRecyclerView.setKeyCodeEffectInterval(CanRecyclerView.KEYCODE_EFFECT_INTERVAL_NORMAL);
        mPresenter.calculateCurStoragePropgress();
        addBatchUninstallListener();
    }

    private void uninstallButtonRequestFocus() {
        mBtBatchUninstall.post(new Runnable() {
            @Override
            public void run() {
                mBtBatchUninstall.setFocusable(true);
                mBtBatchUninstall.requestFocus();
            }
        });
    }

    private void initOnePositionGetFocus() {
        mCanRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = mCanRecyclerView.getChildAt(0);
                if (view != null) {
                    view.setFocusable(true);
                    view.requestFocus();
                }
            }
        }, 10);
    }

    private void addBatchUninstallListener() {
        mBtBatchUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUninstallManagerAdapter != null) {
                    if (mUninstallManagerAdapter.getCurrentSelectMode() == CanRecyclerViewAdapter.MODE_SELECT
                            && mUninstallManagerAdapter.getItemCount() > 0) {
                        if (isSelect && mSelectPackageName != null && mSelectPackageName.size() > 0) {
                            mPresenter.batchUninstallApp(mSelectPackageName);
                        } else {
                            mUninstallManagerAdapter.switchSelectMode(CanRecyclerViewAdapter.MODE_NORMAL);
                            if (mSelectPackageName != null) {
                                mSelectPackageName.clear();
                            }
                            hideSelectAppCount();
                            isSelect = !isSelect;
                        }
                    } else if (mUninstallManagerAdapter.getCurrentSelectMode() == CanRecyclerViewAdapter.MODE_NORMAL
                            && mUninstallManagerAdapter.getItemCount() > 0) {
                        mUninstallManagerAdapter.switchSelectMode(CanRecyclerViewAdapter.MODE_SELECT);
                        showSelectAppCount();
                        isSelect = !isSelect;
                    }
                }
            }
        });

        mBtBatchUninstall.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "onFocusChange: view" + view + " hasfocus : " + b);
                if (b && !isLastRemove) {
                    mFocusedListChild = view;
                    mListFocusMoveRunnable.run();
                    mBtBatchUninstall.setBackgroundResource(R.drawable.shape_bg_uninstall_manager_item_focus);
                } else {
                    mBtBatchUninstall.setBackgroundResource(R.drawable.shape_bg_batch_uninstall_bt);
                }
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCodeEffectInterval != KEYCODE_EFFECT_INTERVAL_UNLIMIT) {
            long time = System.currentTimeMillis();
            if (mLastKeyCodeTimePoint == 0) {
                mLastKeyCodeTimePoint = System.currentTimeMillis();
                return super.dispatchKeyEvent(event);
            } else if (time - mLastKeyCodeTimePoint < keyCodeEffectInterval) {
                return true;
            } else {
                mLastKeyCodeTimePoint = System.currentTimeMillis();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDow : " + keyCode);
        if (mUninstallManagerAdapter != null && mUninstallManagerAdapter.getItemCount() > 0) {
            if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
                mUninstallManagerAdapter.switchSelectMode(isSelect ? CanRecyclerViewAdapter.MODE_NORMAL : CanRecyclerViewAdapter.MODE_SELECT);
                if (isSelect) {
                    hideSelectAppCount();
                } else {
                    showSelectAppCount();
                }
                isSelect = !isSelect;
            } else if (keyCode == KeyEvent.KEYCODE_BACK && isSelect && event.getAction() == KeyEvent.ACTION_DOWN) {
                mUninstallManagerAdapter.switchSelectMode(CanRecyclerViewAdapter.MODE_NORMAL);
                hideSelectAppCount();
                if (mSelectPackageName != null) {
                    mSelectPackageName.clear();
                }
                isSelect = !isSelect;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSelectAppCount() {
        mLinearLayoutSelectApp.setVisibility(View.VISIBLE);
        mSelectCount.setText("0");
    }

    private void hideSelectAppCount() {
        mLinearLayoutSelectApp.setVisibility(View.INVISIBLE);
    }

    @Override
    public void loadAllAppInfoSuccess(List<PackageUtil.AppInfo> infoList) {
        Log.d(TAG, "loadAllAppInfoSuccess: infoList : " + infoList);
        if (infoList.size() == 0) {
            mNotUninstallApp.setVisibility(View.VISIBLE);
            mCanRecyclerView.setVisibility(View.INVISIBLE);
            mTvItemCurRows.setVisibility(View.INVISIBLE);
            hideSelectAppCount();
            mBtBatchUninstall.setNextFocusRightId(mBtBatchUninstall.getId());
            uninstallButtonRequestFocus();
            return;
        } else {
            if (mPresenter.isFirstIntoRefresh) {
                mPresenter.isFirstIntoRefresh = false;
                mPresenter.onItemFocus(0);
                initOnePositionGetFocus();
            }
            mCanRecyclerView.setVisibility(View.VISIBLE);
            mTvItemCurRows.setVisibility(View.VISIBLE);
            mNotUninstallApp.setVisibility(View.INVISIBLE);
            mBtBatchUninstall.setNextFocusRightId(mCanRecyclerView.getId());
        }
        if (mUninstallManagerAdapter == null) {
            mUninstallManagerAdapter = new UninstallManagerAdapter(UninstallManagerActivity.this, infoList);
            addRecyclerViewListener();
            addSetting();
        } else {
            mUninstallManagerAdapter.notifyDataSetChanged();
            mCanRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPresenter.refreshSelectPosition();
                }
            }, APP_INSTALL_REFRESH_DELAYE);
        }
    }

    @Override
    public void showCurStorageProgress(int progress, String storage) {
        mProgressStorage.setProgress(progress);
        mProgressStorage.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_18px));
        mProgressStorage.setText(storage);
    }

    @Override
    public void refreshSelectCount(int count) {
        mSelectCount.setText(count + "");
    }

    @Override
    public void uninstallLastPosition(final int position) {
        Log.d(TAG, "uninstallLastPosition: " + position);
        if (position >= 0) {
            isLastRemove = true;
        }
        mFocusMoveUtil.hideFocusForShowDelay(610);
        mCanRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mCanRecyclerView.getChildAt(position - mLayoutManager.findFirstVisibleItemPosition());
                if (childAt != null) {
                    childAt.setFocusable(true);
                    childAt.requestFocus();
                } else {
                    mBtBatchUninstall.setFocusable(true);
                    mBtBatchUninstall.requestFocus();
                }
            }
        }, UNINSTALL_LAST_POSITION_DELAYE);
    }

    @Override
    public void clickNegativeRefreshPage(int position, int count) {
        Log.d(TAG, "clickNegativeRefreshPage: position : " + position + "  count : " + count);
        mUninstallManagerAdapter.setItemUnselected(position);
        mSelectCount.setText(count + "");
    }

    @Override
    public void refreshSelectPosition(int[] selectPosition) {
        if (mSelectPackageName != null && mSelectPackageName.size() > 0) {
            mSelectPackageName.clear();
        }
        for (int aSelectPosition : selectPosition) {
            Log.d(TAG, "refreshSelectPosition: " + aSelectPosition);
            mUninstallManagerAdapter.setItemSelected(aSelectPosition);
        }
    }

    @Override
    public void refreshRows(SpannableStringBuilder rows) {
        mTvItemCurRows.setText(rows);
    }

    public void addSetting() {
        CanRecyclerViewDivider canRecyclerViewDivider = new CanRecyclerViewDivider(0, getResources().getDimensionPixelSize(R.dimen.dimen_32px), getResources().getDimensionPixelSize(R.dimen.dimen_40px));
        mCanRecyclerView.addItemDecoration(canRecyclerViewDivider);
        mCanRecyclerView.setHasFixedSize(true);
        mCanRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCanRecyclerView.setAdapter(mUninstallManagerAdapter);
    }

    private void addRecyclerViewListener() {
        mUninstallManagerAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public boolean onFocusMoveOutside(int currFocus, int direction) {
                return false;
            }

            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "onItemFocusChanged " + position);
                    mFocusedListChild = view;
                    if (position == mUninstallManagerAdapter.getItemCount() - 1) {
                        isLastRemove = false;
                    }
                    mListFocusMoveRunnable.run();
                    view.setBackgroundResource(R.drawable.shape_bg_uninstall_manager_item_focus);
                } else {
                    view.setBackgroundResource(R.drawable.shape_bg_uninstall_manager_item);
                }
                if (mPresenter != null) {
                    mPresenter.onItemFocus(position);
                }
                view.setSelected(hasFocus);
            }
        });

        mUninstallManagerAdapter.setOnItemSelectListener(new CanRecyclerViewAdapter.OnItemSelectChangeListener() {//选择模式的点击事件
            @Override
            public boolean onSelectChanged(int position, boolean selected, Object data) {
                Log.d(TAG, "onSelectChanged = " + position + ",    " + selected);
                PackageUtil.AppInfo info = (PackageUtil.AppInfo) data;
                if (mSelectPackageName == null) {
                    mSelectPackageName = new ArrayList<>();
                }
                if (selected) {
                    mSelectPackageName.add(info.packageName);
                } else {
                    for (int i = 0; i < mSelectPackageName.size(); i++) {
                        if (mSelectPackageName.get(i).equals(info.packageName)) {
                            mSelectPackageName.remove(i);
                        }
                    }
                }
                mPresenter.mSelectPackageName = mSelectPackageName;
                mSelectCount.setText(mSelectPackageName.size() + "");
                return false;
            }
        });

        mUninstallManagerAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {//不是选择模式的点击事件
            @Override
            public void onClick(View view, int position, Object data) {
                PackageUtil.AppInfo info = (PackageUtil.AppInfo) data;
                mPresenter.showUninstallDialog(info.appIcon, info.appName, info.packageName, false);
            }
        });

        mCanRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: ");
                if (newState == CanRecyclerView.SCROLL_STATE_SETTLING) {
                    mBtBatchUninstall.setFocusable(false);
                } else if (newState == CanRecyclerView.SCROLL_STATE_IDLE) {
                    mBtBatchUninstall.setFocusable(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.d(TAG, "onScrolled: ");
                mListFocusMoveRunnable.run();
            }
        });
    }

    @Override
    public void setPresenter(AppDetailContract.Presenter presenter) {

    }

    private class ListFocusMoveRunnable implements Runnable {

        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mFocusMoveUtil.startMoveFocus(mFocusedListChild, 2);
            }
        }
    }

    @Override
    protected void onHomeKeyDown() {
        mPresenter.dismissUninstallDialog();
        //finish();
        super.onHomeKeyDown();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged: hasFocus " + hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "===onStop===");
        super.onStop();
        mPresenter.unRegiestr();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "===onDestroy===");
        super.onDestroy();
        mFocusMoveUtil.release();
        mPresenter.release();
    }

    /**
     * 打开卸载管理
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UninstallManagerActivity.class);
        context.startActivity(intent);
    }
}
