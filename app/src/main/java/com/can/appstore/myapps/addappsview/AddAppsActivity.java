package com.can.appstore.myapps.addappsview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.entity.SelectedAppInfo;
import com.can.appstore.myapps.adapter.AddAppsRvAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * Created by wei on 2016/10/26.
 */

public class AddAppsActivity extends BaseActivity implements AddAppsContract.View {
    private static final String TAG = "AddAppsActivity";
    private CanRecyclerView mAddRecyclerView;
    private AddAppsPresenter mAddAppsPresenter;
    private AddAppsRvAdapter mAddAppsRecyclerViewAdapter;
    //数据源
    private ArrayList<SelectedAppInfo> mSelectAppInfo;
    private int canSelect = 0;
    //焦点框
    private FocusMoveUtil mFocusMoveUtil;
    private View mFocusChild;
    private MyFocusRunnable mFocusRunnable;
    private boolean focusSearchFailed;
    private long mTime;
    public static final int MIN_DOWN_INTERVAL = 80;//响应点击事件的最小间隔事件
    public static final int APP_INSTALL_REFRESH_DELAYE = 200;//应用安装刷新此时选择的位置

    //布局控件
    private TextView addBut;
    private TextView tv_select;
    private TextView tv_canSelect;
    private TextView tv_curRows;
    private TextView tv_totalRows;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_addapps);
        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusRunnable = new MyFocusRunnable();
        mAddAppsPresenter = new AddAppsPresenter(this, AddAppsActivity.this);
        mAddAppsPresenter.startLoad();
        initView();
    }


    private void initView() {
        addBut = (TextView) findViewById(R.id.bt_batch_addapps);
        tv_canSelect = (TextView) findViewById(R.id.tv_canadd_cut);
        tv_select = (TextView) findViewById(R.id.tv_addselect_count);
        tv_curRows = (TextView) findViewById(R.id.addapps_tv_currows);
        tv_totalRows = (TextView) findViewById(R.id.addapps_tv_totalrows);
        mAddRecyclerView = (CanRecyclerView) findViewById(R.id.addapps_recyclerview);
        mAddRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false), new CanRecyclerView.OnFocusSearchCallback() {
            @Override
            public void onSuccess(View view, View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }

            @Override
            public void onFail(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }
        });
        mAddRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent, 40, 85));
    }


    @Override
    public void loadAddAppInfoSuccess(List<SelectedAppInfo> infoList) {
        if (mAddAppsRecyclerViewAdapter == null) {
            Log.d(TAG, "loadAddAppInfoSuccess" + "首次");
            mAddAppsRecyclerViewAdapter = new AddAppsRvAdapter(infoList);
            mAddAppsPresenter.canSelectCount();
            baseSetting();
            addViewListener();
        } else {
            Log.d(TAG, "loadAddAppInfoSuccess" + "非首次刷新");
            mAddAppsRecyclerViewAdapter.notifyDataSetChanged();
            mAddRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAddAppsPresenter.refreshSelectDataPosition();
                }
            }, APP_INSTALL_REFRESH_DELAYE);
        }
        //设置右上角总行数
        tv_curRows.setText("0");
        int totalRows = 0;
        totalRows = mAddAppsPresenter.calculateCurTotalRows();
        tv_totalRows.setText("/" + totalRows + "行");
    }

    private void baseSetting() {
        mAddRecyclerView.setAdapter(mAddAppsRecyclerViewAdapter);
        mAddAppsRecyclerViewAdapter.switchSelectMode(CanRecyclerViewAdapter.MODE_SELECT);//设置为选择模式
        mFocusMoveUtil.hideFocusForShowDelay(50);
        mAddRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mAddRecyclerView.getChildAt(0);
                if (childAt != null) {
                    mFocusMoveUtil.setFocusView(childAt);
                    childAt.requestFocus();
                } else {
                    addBut.requestFocus();
                }
            }
        }, 50);

        mAddRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //滑动时屏蔽掉添加按钮
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == CanRecyclerView.SCROLL_STATE_SETTLING) {
                    addBut.setFocusable(false);
                } else if (newState == CanRecyclerView.SCROLL_STATE_IDLE) {
                    addBut.setFocusable(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mFocusRunnable.run();
            }
        });
    }

    @Override
    public void showCanSelectCount(int cansel, int alreadyshow) {
        this.canSelect = cansel;
        tv_canSelect.setText(getResources().getString(R.string.already_add_) + alreadyshow + getResources().getString(R.string._app_also_can_add_) + cansel + getResources().getString(R.string._individual));
    }

    /**
     * 保存选择添加的app
     *
     * @param list
     */
    @Override
    public void saveSelectInfo(List<SelectedAppInfo> list) {
        mAddAppsPresenter.saveSelectlist(list);
    }

    @Override
    public void setAlreadySelectApp(int[] alreadySelect) {
        if (mSelectAppInfo != null && mSelectAppInfo.size() > 0) {
            mSelectAppInfo.clear();
        }
        for (int i = 0; i < alreadySelect.length; i++) {
            Log.d(TAG, "setAlreadySelectApp" + alreadySelect[i]);
            mAddAppsRecyclerViewAdapter.setItemSelected(alreadySelect[i]);
        }
    }

    @Override
    public void setPresenter(Object presenter) {

    }


    class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusChild != null) {
                if (focusSearchFailed) {
                    mFocusMoveUtil.startMoveFocus(mFocusChild);
                } else {
                    mFocusMoveUtil.startMoveFocus(mFocusChild, 0);
                }
            }
        }
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

    private void addViewListener() {
        //按钮的点击事件
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "addBut" + "的点击时间");
                if (mSelectAppInfo == null || mSelectAppInfo.size() == 0) {
                    showToast(getResources().getString(R.string.no_select_anyone));
                } else {
                    saveSelectInfo(mSelectAppInfo);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isAdd", true);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        //按钮的焦点监听
        addBut.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(addBut);
                    tv_curRows.setText("1");
                }
            }
        });

        mAddAppsRecyclerViewAdapter.setOnItemSelectListener(new CanRecyclerViewAdapter.OnItemSelectChangeListener() {
            @Override
            public boolean onSelectChanged(int position, boolean selected, Object data) {
                Log.d(TAG, "setOnItemSelectListener.onSelectChanged" + "position" + position + "selected" + selected + "data" + data.toString());
                SelectedAppInfo appInfo = (SelectedAppInfo) data;
                if (mSelectAppInfo == null) {
                    mSelectAppInfo = new ArrayList<>();
                }
                if (selected) {
                    if (mSelectAppInfo.size() >= canSelect) {
                        showToast(getResources().getString(R.string.table_full_cant_add));
                        mAddAppsPresenter.mSelectAppInfo = mSelectAppInfo;
                        tv_select.setText("" + mSelectAppInfo.size());
                        return true;
                    } else {
                        mSelectAppInfo.add(appInfo);
                        mAddAppsPresenter.mSelectAppInfo = mSelectAppInfo;
                        tv_select.setText("" + mSelectAppInfo.size());
                        return false;
                    }
                } else {
                    for (int i = mSelectAppInfo.size() - 1; i >= 0; i--) {
                        if (mSelectAppInfo.get(i).packageName.equals(appInfo.packageName)) {
                            mSelectAppInfo.remove(i);
                        }
                    }
                    mAddAppsPresenter.mSelectAppInfo = mSelectAppInfo;
                    tv_select.setText("" + mSelectAppInfo.size());
                    return false;
                }
            }

        });


        //RecyclerView的焦点事件
        mAddAppsRecyclerViewAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusChild = view;
                    mAddRecyclerView.postDelayed(mFocusRunnable, 50);
                    int curRows = mAddAppsPresenter.calculateCurRows(position);
                    tv_curRows.setText("" + curRows);
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAddAppsPresenter != null) {
            mAddAppsPresenter.addListener();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAddAppsPresenter != null) {
            mAddAppsPresenter.unRegiestr();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAddAppsPresenter != null) {
            mAddAppsPresenter.release();
        }
        if (mFocusMoveUtil != null) {
            mFocusMoveUtil.release();
        }
    }
}