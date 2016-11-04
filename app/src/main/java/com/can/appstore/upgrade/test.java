package com.can.appstore.upgrade;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * Created by 4 on 2016/11/3.
 */

public class test extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        initView();
    }

    private void initView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_test);
        List list = new ArrayList();
        for (int i = 0; i < 1000; i++) {
            list.add(i + "");
        }

        //设置布局管理器
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //设置adapter
        mRecyclerView.setAdapter(new HomeAdapter(list));
//        mRecyclerView.setAdapter(new HomeAdapter());
        //添加分割线
        mRecyclerView.addItemDecoration(new CanRecyclerViewDivider(1));
    }

//        class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
//
//            @Override
//            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(test.this).inflate(R.layout.layout_item,parent, false);
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
            View view = LayoutInflater.from(test.this).inflate(R.layout.layout_item, parent, false);
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
