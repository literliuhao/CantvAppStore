package com.can.appstore.uninstallmanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.appdetail.AppUtils;
import com.can.appstore.appdetail.custom.TextProgressBar;
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

public class UninstallManagerActivity extends Activity implements UninstallManagerContract.View {
    public static final String TAG = "UninstallManagerActivi";

    private CanRecyclerView mCanRecyclerView;
    private CanRecyclerView.LayoutManager mLayoutManager;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mScaleUtil;
    private UninstallManagerAdapter mUninstallManagerAdapter;
    private boolean focusSearchFailed;
    private View mFocusedListChild;
    private ListFocusMoveRunnable mListFocusMoveRunnable;
    private UninstallManagerPresenter mPresenter;
    private ArrayList<String> mSelectPackageName;
    private boolean flag;
    private Button mBtBatchUninstall;
    private TextView mTvItemCurRows;
    private TextView mTvItemTotalRows;
    private TextProgressBar mProgressStorage;
    private TextView mSelectCount;
    private LinearLayout mLinearLayoutSelectApp;
    private int mCurSelectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall_manager);
        mFocusMoveUtil = new FocusMoveUtil(UninstallManagerActivity.this, getWindow().getDecorView(), R.mipmap.btn_focus);
        mScaleUtil = new FocusScaleUtil();
        mListFocusMoveRunnable = new ListFocusMoveRunnable();
        mPresenter = new UninstallManagerPresenter(this, UninstallManagerActivity.this);
        mPresenter.startLoad();
        initView();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "===onResume===");
        super.onResume();
        if (mPresenter != null) {
            mPresenter.addListener();
        }
        //        mFocusMoveUtil.hideFocusForShowDelay(50);
        //        mCanRecyclerView.postDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                View childAt = mCanRecyclerView.getChildAt(mCurSelectPosition);
        //                if (childAt != null) {
        //                    childAt.requestFocus();
        //                }
        //            }
        //        }, 50);

    }

    private void initView() {
        mCanRecyclerView = (CanRecyclerView) findViewById(R.id.crlv_grid);
        mBtBatchUninstall = (Button) findViewById(R.id.bt_batch_uninstall);
        mTvItemCurRows = (TextView) findViewById(R.id.tv_cur_rows);
        mTvItemTotalRows = (TextView) findViewById(R.id.tv_total_rows);
        mProgressStorage = (TextProgressBar) findViewById(R.id.progress_stroage);
        mSelectCount = (TextView) findViewById(R.id.tv_select_count);
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
        addBatchUninstallListener();
    }

    private void addBatchUninstallListener() {
        mBtBatchUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUninstallManagerAdapter.getCurrentSelectMode() == CanRecyclerViewAdapter.MODE_SELECT) {
                    if (flag && mSelectPackageName != null && mSelectPackageName.size() > 0) {
                        mPresenter.batchUninstallApp(mSelectPackageName);
                    } else {
                        mUninstallManagerAdapter.switchSelectMode(CanRecyclerViewAdapter.MODE_NORMAL);
                        if (mSelectPackageName != null) {
                            mSelectPackageName.clear();
                        }
                        hideSelectAppCount();
                        flag = !flag;
                    }
                } else if (mUninstallManagerAdapter.getCurrentSelectMode() == CanRecyclerViewAdapter.MODE_NORMAL) {
                    mUninstallManagerAdapter.switchSelectMode(CanRecyclerViewAdapter.MODE_SELECT);
                    showSelectAppCount();
                    flag = !flag;
                }
            }
        });

        mBtBatchUninstall.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mFocusMoveUtil.startMoveFocus(view, 1.1f);
                    mScaleUtil.scaleToLarge(mBtBatchUninstall);
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
            mUninstallManagerAdapter.switchSelectMode(flag ? CanRecyclerViewAdapter.MODE_NORMAL : CanRecyclerViewAdapter.MODE_SELECT);
            if (flag) {
                hideSelectAppCount();
            } else {
                showSelectAppCount();
            }
            flag = !flag;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && flag && event.getAction() == KeyEvent.ACTION_DOWN) {
            mUninstallManagerAdapter.switchSelectMode(CanRecyclerViewAdapter.MODE_NORMAL);
            hideSelectAppCount();
            if (mSelectPackageName != null) {
                mSelectPackageName.clear();
            }
            flag = !flag;
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
    public void showLoading() {
        mPresenter.showLoading(getResources().getString(R.string.loading));
    }

    @Override
    public void hideLoading() {
        mPresenter.hideLoading();
    }

    @Override
    public void onClickHomeKey() {
        finish();
    }

    @Override
    public void loadAllAppInfoSuccess(List<PackageUtil.AppInfo> infoList) {
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

    public void addSetting() {
        CanRecyclerViewDivider canRecyclerViewDivider = new CanRecyclerViewDivider(0, getResources().getDimensionPixelSize(R.dimen.dimen_32px), getResources().getDimensionPixelSize(R.dimen.dimen_40px));
        mCanRecyclerView.addItemDecoration(canRecyclerViewDivider);
        mCanRecyclerView.setHasFixedSize(true);
        mCanRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCanRecyclerView.setAdapter(mUninstallManagerAdapter);
        mFocusMoveUtil.hideFocusForShowDelay(50);
        mCanRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mCanRecyclerView.getChildAt(0);
                if (childAt != null) {
                    mFocusMoveUtil.setFocusView(childAt);
                    childAt.requestFocus();
                } else {
                    mBtBatchUninstall.requestFocus();
                }
            }
        }, 50);

    }

    /**
     * 添加RecyclerView的一些监听事件
     */
    private void addRecyclerViewListener() {
        //添加焦点移动的监听,需要在adapter上设置
        mUninstallManagerAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public boolean onFocusMoveOutside(int currFocus, int direction) {
                return false;
            }

            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    Log.d(TAG, "onItemFocusChanged " + position);
                    mCanRecyclerView.postDelayed(mListFocusMoveRunnable, 50);
                    mCurSelectPosition = position;
                    view.setBackgroundResource(R.drawable.shape_bg_uninstall_manager_item_focus);
                } else {
                    mScaleUtil.scaleToNormal();
                    view.setBackgroundResource(R.drawable.shape_bg_uninstall_manager_item);
                }
                mTvItemCurRows.setText(mPresenter.calculateCurRows(position) + "");
                String totalRowsStr = String.format(getResources().getString(R.string.rows_str), mPresenter.calculateCurTotalRows());
                mTvItemTotalRows.setText(totalRowsStr);
            }
        });

        mUninstallManagerAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "keycode = " + keyCode + "isHideCheckBox : " + flag);
                return false;
            }
        });

        mUninstallManagerAdapter.setOnItemSelectListener(new CanRecyclerViewAdapter.OnItemSelectChangeListener() {//选择模式的点击事件
            @Override
            public void onSelectChanged(int position, boolean selected, Object data) {
                Log.d(TAG, "onSelectChanged = " + position + ",    " + selected);
                PackageUtil.AppInfo info = (PackageUtil.AppInfo) data;
                if (mSelectPackageName == null) {
                    mSelectPackageName = new ArrayList<String>();
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
            }
        });

        mUninstallManagerAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {//不是选择模式的点击事件
            @Override
            public void onClick(View view, int position, Object data) {
                //进入普通点击事件  点击弹出卸载对话框
                PackageUtil.AppInfo info = (PackageUtil.AppInfo) data;
                Log.d(TAG, "onSelectChanged = " + position + "packageName" + info.packageName);
                AppUtils.uninstallpkg(UninstallManagerActivity.this, info.packageName);
            }
        });

        mCanRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mListFocusMoveRunnable.run();
            }
        });
    }

    private class ListFocusMoveRunnable implements Runnable {

        @Override
        public void run() {
            if (mFocusedListChild != null) {
                mScaleUtil.scaleToLarge(mFocusedListChild);
                if (focusSearchFailed) {
                    mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.1f);
                } else {
                    mFocusMoveUtil.startMoveFocus(mFocusedListChild, 1.1f, 0);
                }
            }
        }
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
}
