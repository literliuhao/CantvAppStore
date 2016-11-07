package com.can.appstore.speciallist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.widget.TextView;
import com.can.appstore.R;
import java.util.ArrayList;
import java.util.List;
import cn.can.tvlib.ui.focus.CanRecyclerViewFocusHelper;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * Created by syl on 2016/10/24.
 * 专题列表页面
 */

public class SpecialListActivity extends Activity{

    private TextView mTvSpecialTitle;
    private TextView mTvSpecialLine;
    private CanRecyclerView mRecyclerview;
    public static final int SPAN_COUNT = 4;
    public static final int RECYCLERVIEW_DIVIDER = 60;
    public static final float MENU_FOCUS_SCALE = 1.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_special_list);
        
        initView();
        initRecyclerView();
        initFocusView();
        initData();
    }

    private void initView() {
        mTvSpecialTitle = (TextView) findViewById(R.id.tv_special_title);
        mTvSpecialLine = (TextView) findViewById(R.id.tv_special_line);
        mRecyclerview = (CanRecyclerView) findViewById(R.id.rv_special_list);
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager =  new GridLayoutManager(this, SPAN_COUNT);
        mRecyclerview.setLayoutManager(gridLayoutManager);
        // 设置item分
        mRecyclerview.addItemDecoration(new CanRecyclerViewDivider(RECYCLERVIEW_DIVIDER));
        // 设置item动画
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
    }

    private void initData() {
        List<String> list = new ArrayList<>();
        for(int i = 0;i < 200;i++){
            list.add("ss");
        }
        SpecialListAdapter adpter = new SpecialListAdapter(list,this);
        mRecyclerview.setAdapter(adpter);
    }
    private void initFocusView() {
        mRecyclerview.setFocusable(false);
        FocusMoveUtil focusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView().findViewById(android.R.id.content), R.mipmap.image_focus);
        FocusScaleUtil focusScaleUtil = new FocusScaleUtil();
        CanRecyclerViewFocusHelper helper = new CanRecyclerViewFocusHelper(focusMoveUtil, focusScaleUtil);
        helper.attachToRecyclerView(mRecyclerview, R.mipmap.image_focus, MENU_FOCUS_SCALE);
    }

}
