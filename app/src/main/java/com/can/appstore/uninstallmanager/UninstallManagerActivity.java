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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailContract;
import com.can.appstore.appdetail.custom.TextProgressBar;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.uninstallmanager.adapter.UninstallManagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.PackageUtil;

/**
 * 本地卸载管理页面
 * Created by JasonF on 2016/10/13.
 */

public class UninstallManagerActivity extends BaseActivity implements UninstallManagerContract.View {
    public static final String TAG = "UninstallManagerActivi";
    public static final int MIN_DOWN_INTERVAL = 80;//响应点击事件的最小间隔事件
    private CanRecyclerView mCanRecyclerView;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mScaleUtil;
    private UninstallManagerAdapter mUninstallManagerAdapter;
    private View mFocusedListChild;
    private ListFocusMoveRunnable mListFocusMoveRunnable;
    private UninstallManagerPresenter mPresenter;
    private ArrayList<String> mSelectPackageName;
    private Button mBtBatchUninstall;
    private TextView mTvItemCurRows;
    private TextProgressBar mProgressStorage;
    private TextView mSelectCount;
    private TextView mNotUninstallApp;
    private LinearLayout mLinearLayoutSelectApp;
    private long mTime;
    private boolean isSelect;
    private boolean isFirstInto = true;
    private boolean focusSearchFailed;
    private boolean isLastRemove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "===onCreate===");
        setContentView(R.layout.activity_uninstall_manager);
        setFocus();
        mPresenter = new UninstallManagerPresenter(this, UninstallManagerActivity.this);
        initView();
        mPresenter.startLoad(getSupportLoaderManager());
    }

    public void setFocus() {
        mFocusMoveUtil = new FocusMoveUtil(UninstallManagerActivity.this, getWindow().getDecorView(), R.mipmap.btn_focus);
        mScaleUtil = new FocusScaleUtil();
        mScaleUtil.setFocusScale(1.0f);
        mListFocusMoveRunnable = new ListFocusMoveRunnable();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "===onResume===");
        super.onResume();
    }

    private void initView() {
        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.crlv_grid);
        mBtBatchUninstall = (Button) findViewById(R.id.bt_batch_uninstall);
        mTvItemCurRows = (TextView) findViewById(R.id.tv_cur_rows);
        mProgressStorage = (TextProgressBar) findViewById(R.id.progress_stroage);
        mSelectCount = (TextView) findViewById(R.id.tv_select_count);
        mNotUninstallApp = (TextView) findViewById(R.id.tv_no_data);
        mLinearLayoutSelectApp = (LinearLayout) findViewById(R.id.ll_select_app);
        mCanRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(UninstallManagerActivity.this, 3, CanRecyclerView.CanGridLayoutManager.VERTICAL, false), new CanRecyclerView.OnFocusSearchCallback() {
            @Override
            public void onSuccess(View view, View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }

            @Override
            public void onFail(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }
        });
        mPresenter.calculateCurStoragePropgress();
        mBtBatchUninstall.post(new Runnable() {
            @Override
            public void run() {
                mBtBatchUninstall.setFocusable(true);
                mBtBatchUninstall.requestFocus();
            }
        });
        addBatchUninstallListener();
    }

    private void addBatchUninstallListener() {
        mBtBatchUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUninstallManagerAdapter.getCurrentSelectMode() == CanRecyclerViewAdapter.MODE_SELECT) {
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
                } else if (mUninstallManagerAdapter.getCurrentSelectMode() == CanRecyclerViewAdapter.MODE_NORMAL) {
                    mUninstallManagerAdapter.switchSelectMode(CanRecyclerViewAdapter.MODE_SELECT);
                    showSelectAppCount();
                    isSelect = !isSelect;
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
                    mScaleUtil.scaleToNormal(view);
                    mBtBatchUninstall.setBackgroundResource(R.drawable.shape_bg_batch_uninstall_bt);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDow : " + keyCode);
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
            mBtBatchUninstall.setNextFocusRightId(mBtBatchUninstall.getId());
            return;
        } else {
            if (isFirstInto) {
                isFirstInto = false;
                mPresenter.onItemFocus(0);
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
        mFocusMoveUtil.hideFocusForShowDelay(500);
        mCanRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mCanRecyclerView.getChildAt(position);
                if (childAt != null) {
                    childAt.setFocusable(true);
                    childAt.requestFocus();
                } else {
                    mBtBatchUninstall.setFocusable(true);
                    mBtBatchUninstall.requestFocus();
                }
            }
        }, 200);
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
                    mCanRecyclerView.postDelayed(mListFocusMoveRunnable, 50);
                    view.setBackgroundResource(R.drawable.shape_bg_uninstall_manager_item_focus);
                } else {
                    mScaleUtil.scaleToNormal();
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
                super.onScrolled(recyclerView, dx, dy);
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
                mScaleUtil.scaleToLarge(mFocusedListChild);
                mScaleUtil.setFocusScale(1.0f);
                if (focusSearchFailed) {
                    mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f);
                } else {
                    mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.0f, 0);
                }
            }
        }
    }

    @Override
    protected void onHomeKeyDown() {
        mPresenter.dismissUninstallDialog();
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged: hasFocus " + hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

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
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UninstallManagerActivity.class);
        context.startActivity(intent);
    }
}
