package com.can.appstore.myapps.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.myapps.adapter.SystemAppsRvAdapter;
import com.can.appstore.myapps.model.AppInfo;
import com.can.appstore.myapps.model.MyAppsListDataUtil;
import com.can.appstore.search.ToastUtil;

import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * Created by wei on 2016/11/2.
 */

public class SystemAppsActivity extends Activity {

    TextView  systemCurRows;
    TextView  systemTotalRows;
    CanRecyclerView  mSystemRecyclerView;
    SystemAppsRvAdapter  mSystemAppsRvAdapter;

    MyAppsListDataUtil dataUtils;
    List<AppInfo> allAppList;

    FocusMoveUtil mFocusMoveUtils;
    FocusScaleUtil mFocusScaleUtil;
    View mFocusChild;
    MyFocusRunnable  mFocusRunnable;

    private class MyFocusRunnable  implements Runnable{
        @Override
        public void run() {
            if(mFocusChild != null){
                mFocusMoveUtils.startMoveFocus(mFocusChild,1.1f);
                mFocusScaleUtil.scaleToLarge(mFocusChild);
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
                if(hasFocus){
                    mFocusChild = view;
                    mSystemRecyclerView.postDelayed(mFocusRunnable,50);
                }else{
                    mFocusScaleUtil.scaleToNormal();
                }
            }
        });

    }

    private void addItemClickListener() {
        mSystemAppsRvAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                AppInfo appInfo = allAppList.get(position);
                PackageManager pm =getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);//获取启动的包名
                startActivity(intent);

            }
        });

        mSystemAppsRvAdapter.setItemKeyEventListener(new CanRecyclerViewAdapter.OnItemKeyEventListener() {
            @Override
            public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_MENU){
                    ToastUtil.toastShort("gao--"+v.getHeight()+"kuan--"+v.getWidth());
                    return true;
                }
                return false;
            }
        });


    }

    private void initData() {
        dataUtils = new MyAppsListDataUtil(this);
        allAppList = dataUtils.getAllAppList();
    }


    private void initView() {
        systemCurRows = (TextView) findViewById(R.id.systemapps_tv_currows);
        systemTotalRows = (TextView) findViewById(R.id.systemapps_tv_totalrows);
        mSystemRecyclerView = (CanRecyclerView) findViewById(R.id.systemapps_recyclerview);
        mSystemRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this,5, LinearLayoutManager.VERTICAL,false));
        mSystemRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent,40,62));
        mSystemAppsRvAdapter = new SystemAppsRvAdapter(allAppList);
        mSystemRecyclerView.setAdapter(mSystemAppsRvAdapter);

        mFocusMoveUtils = new FocusMoveUtil(this,getWindow().getDecorView(),R.drawable.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
        mFocusRunnable = new MyFocusRunnable();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFocusMoveUtils.release();
    }
}
