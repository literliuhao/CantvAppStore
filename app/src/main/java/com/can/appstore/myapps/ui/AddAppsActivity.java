package com.can.appstore.myapps.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.myapps.adapter.AllAppsRecyclerViewAdapter;
import com.can.appstore.myapps.model.AppInfo;
import com.can.appstore.myapps.model.MyAppsListDataUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by wei on 2016/10/26.
 */

public class AddAppsActivity extends Activity {
    MyAppsListDataUtil mMyAppListData;
    List<AppInfo> isShown;
    List<AppInfo> addShowList = new ArrayList<AppInfo>();
    private List<AppInfo> mAllAppList;

    int canSelect = 0;
    int alreadyShown= 0;

    Button addBut;
    TextView  tv_select;
    TextView  tv_canSelect;
    TextView  tv_curRows;
    TextView  tv_totalRows;
    CanRecyclerView mAddRecyclerView;

    AllAppsRecyclerViewAdapter  mAllAppsRecyclerViewAdapter ;

    FocusMoveUtil mFocusMoveUtil ;
    FocusScaleUtil mFocusScaleUtil;
    View mFocusChild;
    MyFocusRunnable  mFocusRunnable;
    private ArrayList<String> mSelectPackageName;


    class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if(mFocusChild != null){
                mFocusMoveUtil.startMoveFocus(mFocusChild,1.1f);
                mFocusScaleUtil.scaleToLarge(mFocusChild);
            }
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_addapps);
        initData();
        initView();
    }

    private void initData() {
        mMyAppListData = new MyAppsListDataUtil(this);
        mAllAppList = mMyAppListData.getAllAppList();
        isShown = mMyAppListData.getShowList();
        for (AppInfo  app:mAllAppList) {
            if(! isShown.contains(app)){
                addShowList.add(app);
            }
        }
        canSelect = 18 - isShown.size()+1;
        alreadyShown = isShown.size()-3;

    }
    private  void initView(){
        addBut = (Button) findViewById(R.id.bt_batch_addapps);
        tv_canSelect = (TextView)findViewById(R.id.tv_canadd_cut);
        tv_canSelect.setText("已添加"+alreadyShown+"个，还可以添加"+canSelect+"个");
        tv_select = (TextView) findViewById(R.id.tv_addselect_count);
        tv_curRows = (TextView) findViewById(R.id.addapps_tv_currows);
        tv_totalRows = (TextView)findViewById(R.id.addapps_tv_totalrows);
        int totalRows = 0 ;
        if(addShowList.size() % 4 == 0) {
            totalRows = addShowList.size() / 4;
        }else{
            totalRows = addShowList.size() / 4 + 1;
        }
        tv_totalRows.setText("/"+totalRows+"行");
        mAddRecyclerView = (CanRecyclerView)findViewById(R.id.addapps_recyclerview);

        mFocusMoveUtil = new FocusMoveUtil(this,getWindow().getDecorView(),R.drawable.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
        mFocusRunnable = new MyFocusRunnable();

       mAddRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this,4, GridLayoutManager.VERTICAL,false));
       mAddRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent,40,85));
        mAllAppsRecyclerViewAdapter = new AllAppsRecyclerViewAdapter(addShowList);
        mAddRecyclerView.setAdapter(mAllAppsRecyclerViewAdapter);

        addButtonFocusListener();
        addRecyclerViewFocusListener();
        addRecyclerViewOnclickListener();
    }

    private void addRecyclerViewOnclickListener() {
        mAllAppsRecyclerViewAdapter.setOnItemSelectListener(new CanRecyclerViewAdapter.OnItemSelectChangeListener(){
            @Override
            public void onSelectChanged(int position, boolean selected, Object data) {
                Log.d(TAG, "onSelectChanged = " + position + ",    " + selected);
                AppInfo info = (AppInfo) data;
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
                tv_select.setText(mSelectPackageName.size() + "");
            }
        });
    }

    private void addRecyclerViewFocusListener() {
        mAllAppsRecyclerViewAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if(hasFocus){
                    mFocusChild = view;
                    mAddRecyclerView.postDelayed(mFocusRunnable,50);
                    int curRows = position / 4 + 1;
                    tv_curRows.setText(""+curRows);
                }else{
                    mFocusScaleUtil.scaleToNormal();
                }
            }
        });
    }

    private void addButtonFocusListener() {
        addBut.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(addBut, 1.1f);
                    mFocusScaleUtil.scaleToLarge(addBut);
                } else {
                    mFocusScaleUtil.scaleToNormal(addBut);
                }
            }
        });
    }


}