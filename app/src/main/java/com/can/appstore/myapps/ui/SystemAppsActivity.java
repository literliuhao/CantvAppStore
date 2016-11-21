package com.can.appstore.myapps.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.myapps.adapter.SystemAppsRvAdapter;
import com.can.appstore.myapps.model.MyAppsListDataUtil;

import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

/**
 * 预置的有系统权限的应用，如：微信相册，文件管理器..等等
 * Created by wei on 2016/11/2.
 */

public class SystemAppsActivity extends BaseActivity {

    private TextView systemCurRows;
    private TextView systemTotalRows;
    private CanRecyclerView mSystemRecyclerView;
    private SystemAppsRvAdapter mSystemAppsRvAdapter;

    private MyAppsListDataUtil dataUtils;
    private List<AppInfo> systemAppList;

    private FocusMoveUtil mFocusMoveUtils;
    private View mFocusChild;
    private MyFocusRunnable mFocusRunnable;

    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusChild != null) {
                mFocusMoveUtils.startMoveFocus(mFocusChild);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_systemapps);
        initData();
        initView();
        addItemClickListener();
        addFocusListener();
    }

    private void addFocusListener() {
        mSystemAppsRvAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusChild = view;
                    mSystemRecyclerView.postDelayed(mFocusRunnable, 50);
                    systemCurRows.setText(position / 5 + 1 + "/");
                }
            }
        });
    }

    private void addItemClickListener() {
        mSystemAppsRvAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                AppInfo appInfo = systemAppList.get(position);
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);//获取启动的包名
                startActivity(intent);
            }
        });

    }

    private void initData() {
        dataUtils = new MyAppsListDataUtil(this);
        systemAppList = dataUtils.getSystemApp(systemAppList);
    }


    private void initView() {
        systemCurRows = (TextView) findViewById(R.id.systemapps_tv_currows);
        systemTotalRows = (TextView) findViewById(R.id.systemapps_tv_totalrows);
        int total = systemAppList.size() / 5 + 1;
        systemTotalRows.setText("" + total + "行");
        mSystemRecyclerView = (CanRecyclerView) findViewById(R.id.systemapps_recyclerview);
        mSystemRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this, 5, LinearLayoutManager.VERTICAL, false));
        mSystemRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent, 40, 62));
        mSystemAppsRvAdapter = new SystemAppsRvAdapter(systemAppList);
        mSystemRecyclerView.setAdapter(mSystemAppsRvAdapter);

        mFocusMoveUtils = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        mFocusRunnable = new MyFocusRunnable();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFocusMoveUtils.release();
    }
}
