package com.can.appstore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.applist.AppListActivity;
import com.can.appstore.download.DownloadLeadAcitivity;
import com.can.appstore.message.MessageStartActivity;
import com.can.appstore.uninstallmanager.UninstallManagerActivity;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * Created by syl on 2016/11/3.
 * 应用入口  item点击事件中添加自己的跳转
 */

public class PortalActivity extends Activity {

    private RecyclerView mRecyclerView;
    private HomeAdapter adapter;
    private FocusMoveUtil mFocusMoveUtil;
    private FocusScaleUtil mFocusScaleUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_test);
        List list = new ArrayList();
        for (int i = 0; i < 1000; i++) {
            // TODO: 2016/11/4  添加跳转页面名称
            if (i == 0) {
                list.add("应用列表页面");
            } else if (i == 1) {
                list.add("下载列表和专题列表");
            } else if (i == 2) {
                list.add("应用详情1");
            } else if (i == 3) {
                list.add("应用详情2");
            } else if (i == 4) {
                list.add("卸载管理");
            } else if (i == 5) {
                list.add("应用详情3");
            } else if (i == 6){
                list.add("消息中心");
            } else if (i == 7){
                list.add("首页");
            }else {
                list.add(i + "");
            }
        }

        //设置布局管理器
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //设置adapter
        adapter = new HomeAdapter(list);
        mRecyclerView.setAdapter(adapter);
        //        mRecyclerView.setAdapter(new HomeAdapter());
        //添加分割线
        mRecyclerView.addItemDecoration(new CanRecyclerViewDivider(100));


        adapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                Log.d("", "mRecyclerView" + position);
                // TODO: 2016/11/4  添加跳转页面点击事件
                if (position == 0) {
                    Intent intent = new Intent(PortalActivity.this, AppListActivity.class);
                    intent.putExtra(AppListActivity.PAGE_TYPE, AppListActivity.APPLICATION);
                    intent.putExtra(AppListActivity.TOPIC_ID, "");
                    intent.putExtra(AppListActivity.TYPE_ID, "");
                    PortalActivity.this.startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(PortalActivity.this, DownloadLeadAcitivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(PortalActivity.this, AppDetailActivity.class);
                    intent.putExtra("appID", "1");
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(PortalActivity.this, AppDetailActivity.class);
                    intent.putExtra("appID", "2");
                    startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(PortalActivity.this, UninstallManagerActivity.class);
                    startActivity(intent);
                } else if (position == 5) {
                    Intent intent = new Intent(PortalActivity.this, AppDetailActivity.class);
                    intent.putExtra("appID", "3");
                    startActivity(intent);
                }else if(position == 6){
                    Intent intent = new Intent(PortalActivity.this, MessageStartActivity.class);
                    startActivity(intent);
                }else if(position==7){
                    Intent intent = new Intent(PortalActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });


        mFocusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView().findViewById(android
                .R.id.content), R.mipmap.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("", "mRecyclerView" + "===postDelayed");
                mRecyclerView.getChildAt(0).requestFocus();
            }
        }, 50);

        mRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("", "mRecyclerView==setOnFocusChangeListener=" + hasFocus);
            }
        });

        adapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                Log.d("", "mRecyclerView" + position);
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(view, 1.0f);
                    mFocusScaleUtil.scaleToLarge(view, 1.0f, 1.0f);
                } else {
                    mFocusScaleUtil.scaleToNormal(view);
                }
            }
        });
    }

    //        class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    //
    //            @Override
    //            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //                View view = LayoutInflater.from(PortalActivity.this).inflate(R.layout.layout_item,parent, false);
    //                view.setFocusable(true);
    //                MyViewHolder holder = new MyViewHolder(view);
    //                Log.d("","ssssssssssssss");
    //                return holder;
    //            }
    //
    //            @Override
    //            public void onBindViewHolder(MyViewHolder holder, int position) {
    //                holder.tv.setText(position+"");
    //            }
    //
    //            @Override
    //            public int getItemCount() {
    //                return 1000;
    //            }
    //
    //            class MyViewHolder extends RecyclerView.ViewHolder {
    //
    //                TextView tv;
    //
    //                public MyViewHolder(View view) {
    //                    super(view);
    //                    tv = (TextView) view.findViewById(R.id.tv_test);
    //                }
    //            }
    //        }


    class HomeAdapter extends CanRecyclerViewAdapter {

        public HomeAdapter(List datas) {
            super(datas);
        }

        @Override
        protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PortalActivity.this).inflate(R.layout.layout_item, parent, false);
            view.setFocusable(true);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
            String s = (String) mDatas;
            MyViewHolder ho = (MyViewHolder) holder;
            ho.tv.setText(s);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_test);
        }
    }
}
