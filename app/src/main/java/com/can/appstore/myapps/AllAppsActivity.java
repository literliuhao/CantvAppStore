package com.can.appstore.myapps;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * Created by wei on 2016/10/26.
 */

public class AllAppsActivity extends Activity{

    List<AppInfo> allAppList = null;
    CanRecyclerView mAllAppsRecyclerView;
    AllAppsRecyclerViewAdapter  mAdapter;
    TextView tvCurRows;
    TextView tvTotalRows;
    LinearLayout ll_edit;

    FocusMoveUtil focusMoveUtil;
    FocusScaleUtil focusScaleUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_allapps);
        mAllAppsRecyclerView = (CanRecyclerView) findViewById(R.id.allapps_recyclerview);
        tvCurRows = (TextView) findViewById(R.id.allapps_tv_currows);
        tvTotalRows = (TextView) findViewById(R.id.allapps_tv_totalrows);
        initData();
        initView();
    }

    public void initData(){
        MyAppsListDataUtil listHelper = new MyAppsListDataUtil(this);
        allAppList  = listHelper.getAllAppList();

    }
    private void initView() {
        mAllAppsRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false));
        mAllAppsRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent,40,62));
        mAdapter = new AllAppsRecyclerViewAdapter(allAppList);
        mAllAppsRecyclerView.setAdapter(mAdapter);

        focusMoveUtil = new FocusMoveUtil(this,getWindow().getDecorView(),R.drawable.btn_focus);
        focusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();

        addListener();

    }

    private class MyFocusRunnable implements Runnable{
        @Override
        public void run() {
            if(mFocusedListChild !=  null){
                focusMoveUtil.startMoveFocus(mFocusedListChild, 1.1f);
                focusScaleUtil.scaleToLarge(mFocusedListChild);
            }
        }
    }

    CanRecyclerViewAdapter.OnFocusChangeListener myFocusChangesListener;
    private void addListener() {
        myFocusChangesListener = new CanRecyclerViewAdapter.OnFocusChangeListener (){
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if(hasFocus){
                    mFocusedListChild = view;
                    mAllAppsRecyclerView.postDelayed(myFocusRunnable,50);
                    //右上角行数
                    int total = allAppList.size() / 5;
                    if(allAppList.size() % 5 != 0){
                       total += 1;
                    }
                    int cur = position / 5  + 1;
                    tvCurRows.setText(cur+"/");
                    tvTotalRows.setText(total+"行");

                }else{
                    focusScaleUtil.scaleToNormal();
                }
            }
        };
        mAdapter.setOnFocusChangeListener(myFocusChangesListener);

        mAdapter.setItemKeyEventListener(new MyOnItemKeyEventListener());


    }

    private class MyOnItemKeyEventListener implements CanRecyclerViewAdapter.OnItemKeyEventListener{

        @Override
        public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_MENU){
                //判断系统应用机制 TODO
                mAdapter.setOnFocusChangeListener(null);
                ll_edit = (LinearLayout) v.findViewById(R.id.allapps_ll_edit);
                ll_edit.setVisibility(View.VISIBLE);
                editItem(v,position);
            }
            return false;
        }
    }

    private void editItem(final View item, final int position) {
        Button butStrartapp = (Button) item.findViewById(R.id.allapps_but_startapp);
        butStrartapp.requestFocus();
        butStrartapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm =getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(allAppList.get(position).packageName);//获取启动的包名
                startActivity(intent);
                hideEditView(item);
            }
        });
        butStrartapp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    return true;
                }
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    hideEditView(item);
                    return true;
                }
                return false;
            }
        });
        Button butUninstall = (Button) item.findViewById(R.id.allapps_but_uninstallapp);
        butUninstall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse("package:"+allAppList.get(position).packageName);//获取删除包名的URI
                        Intent i = new Intent(Intent.ACTION_DELETE,uri);
                        startActivity(i);
                        allAppList.remove(position);
                        hideEditView(item);
                    }
                });
        butUninstall.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    return true;
                }
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    hideEditView(item);
                    return true;
                }
                return false;
            }
        });
    }



    private void  hideEditView(View item){
        item.requestFocus();
        ll_edit.setVisibility(View.GONE);
        mAdapter.setOnFocusChangeListener(myFocusChangesListener);
    }


    @Override
    protected void onDestroy() {
        focusMoveUtil.release();
        super.onDestroy();
    }
}
