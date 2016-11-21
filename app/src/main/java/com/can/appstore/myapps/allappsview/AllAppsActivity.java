package com.can.appstore.myapps.allappsview;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.myapps.adapter.AllAppsRecyclerViewAdapter;
import com.can.appstore.widgets.CanDialog;

import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.can.appstore.MyApp.mContext;

/**
 * Created by wei on 2016/10/26.
 */

public class AllAppsActivity extends BaseActivity implements AllAppsContract.View {

    private List<AppInfo> allAppList = null;
    private CanRecyclerView mAllAppsRecyclerView;
    private AllAppsRecyclerViewAdapter mAdapter;
    private AllAppsPresenter mAllAppsPresenter;

    private TextView tvCurRows;
    private TextView tvTotalRows;
    private LinearLayout ll_edit;
    //item的操作按钮
    private Button butStrartapp;
    private Button butUninstall;
    //卸载对话框
    private CanDialog mCanDialog;

    //焦点框和焦点处理
    private FocusMoveUtil focusMoveUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;
    private boolean focusSearchFailed;
    private CanRecyclerViewAdapter.OnFocusChangeListener myFocusChangesListener;
    private long mTime;
    public static final int MIN_DOWN_INTERVAL = 80;//响应点击事件的最小间隔事件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_allapps);
        mAllAppsRecyclerView = (CanRecyclerView) findViewById(R.id.allapps_recyclerview);
        tvCurRows = (TextView) findViewById(R.id.allapps_tv_currows);
        tvTotalRows = (TextView) findViewById(R.id.allapps_tv_totalrows);
        mAllAppsPresenter = new AllAppsPresenter(this, AllAppsActivity.this);
        mAllAppsPresenter.startLoad();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAllAppsPresenter != null) {
            mAllAppsPresenter.addListener();
        }
    }

    private void initView() {
        focusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        myFocusRunnable = new MyFocusRunnable();
        mAllAppsRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false), new CanRecyclerView.OnFocusSearchCallback() {
            @Override
            public void onSuccess(View view, View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }

            @Override
            public void onFail(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }
        });
        mAllAppsRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent, 40, 0));
    }


    @Override
    public void loadAllAppInfoSuccess(List<AppInfo> infoList) {
        if (mAdapter == null) {
            allAppList = infoList;
            mAdapter = new AllAppsRecyclerViewAdapter(infoList);
            baseSetting();
            addFocusListener();
        } else {
            mAdapter.notifyDataSetChanged();
        }
        //设置右上角总行数
        int total = mAllAppsPresenter.calculateCurTotalRows();
        tvTotalRows.setText(total + "行");
    }

    private void baseSetting() {
        mAllAppsRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemKeyEventListener(new MyOnItemKeyEventListener());
        mAdapter.setOnItemClickListener(new myOnItemClickListener());

        focusMoveUtil.hideFocusForShowDelay(50);
        mAllAppsRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mAllAppsRecyclerView.getChildAt(0);
                focusMoveUtil.setFocusView(childAt);
                childAt.requestFocus();
            }
        }, 50);
        mAllAppsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                myFocusRunnable.run();
            }
        });
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

    private void editItem(final View item, final int position) {

        butStrartapp.requestFocus();
        butStrartapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAllAppsPresenter.startApp(position);
                hideEditView(item);
            }
        });
        butStrartapp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (butUninstall.getVisibility() == View.GONE) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    }
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hideEditView(item);
                    return true;
                }
                return false;
            }
        });

        butUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAllAppsPresenter.getUninstallAppInfo(position);
                hideEditView(item);
            }
        });
        butUninstall.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hideEditView(item);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void showUninstallDialog(AppInfo app) {
        String ok = mContext.getResources().getString(R.string.ok);
        String cancle = mContext.getResources().getString(R.string.cancle);
        String makesureUninstall = mContext.getResources().getString(R.string.makesure_uninstall_apk);
        Drawable mIcon = app.appIcon;
        final String mName = app.appName;
        final String mPackName = app.packageName;
        mCanDialog = new CanDialog(this);
        mCanDialog.setIcon(mIcon)
                .setTitle(mName)
                .setTitleMessage(makesureUninstall)
                .setNegativeButton(cancle)
                .setPositiveButton(ok)
                .setOnCanBtnClickListener(new CanDialog.OnClickListener() {
                    @Override
                    public void onClickPositive() {
                        silentUninstall(mName, mPackName);
                        dismissUninstallDialog();
                    }

                    @Override
                    public void onClickNegative() {
                        dismissUninstallDialog();
                    }
                });
        mCanDialog.show();
    }

    private void dismissUninstallDialog() {
        if (mCanDialog != null) {
            mCanDialog.dismiss();
        }
    }

    private void silentUninstall(String name, String packname) {
        mAllAppsPresenter.silentUninstall(name, packname);
    }


    @Override
    public void showLoading() {
        mAllAppsPresenter.showLoading("加载中，请稍后...");
    }

    @Override
    public void hideLoading() {
        mAllAppsPresenter.hideLoading();
    }


    @Override
    protected void onHomeKeyListener() {
        if(mCanDialog != null){
            mCanDialog.dismiss();
        }
       finish();
    }

    private class myOnItemClickListener implements CanRecyclerViewAdapter.OnItemClickListener {
        @Override
        public void onClick(View view, int position, Object data) {
            mAllAppsPresenter.startApp(position);
        }
    }

    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                if (focusSearchFailed) {
                    focusMoveUtil.startMoveFocus(mFocusedListChild);
                } else {
                    focusMoveUtil.startMoveFocus(mFocusedListChild, 0);
                }
            }
        }
    }


    private void addFocusListener() {
        myFocusChangesListener = new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mAllAppsRecyclerView.postDelayed(myFocusRunnable, 50);
                    int cur = mAllAppsPresenter.calculateCurRows(position);
                    tvCurRows.setText(cur + "/");
                }
            }
        };
        mAdapter.setOnFocusChangeListener(myFocusChangesListener);
    }

    private class MyOnItemKeyEventListener implements CanRecyclerViewAdapter.OnItemKeyEventListener {
        @Override
        public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                mAdapter.setOnFocusChangeListener(null);
                ll_edit = (LinearLayout) v.findViewById(R.id.allapps_ll_edit);
                butStrartapp = (Button) ll_edit.findViewById(R.id.allapps_but_startapp);
                butUninstall = (Button) ll_edit.findViewById(R.id.allapps_but_uninstallapp);
                ll_edit.setVisibility(View.VISIBLE);
                if (allAppList.get(position).isSystemApp) {
                    butUninstall.setVisibility(View.GONE);
                } else {
                    butUninstall.setVisibility(View.VISIBLE);
                }
                editItem(v, position);
            }
            return false;
        }
    }

    public void hideEditView(View item) {
        item.requestFocus();
        ll_edit.setVisibility(View.GONE);
        mAdapter.setOnFocusChangeListener(myFocusChangesListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAllAppsPresenter != null) {
            mAllAppsPresenter.unRegiestr();
        }
    }

    @Override
    protected void onDestroy() {
        focusMoveUtil.release();
        mAllAppsPresenter.release();
        super.onDestroy();
    }
}
