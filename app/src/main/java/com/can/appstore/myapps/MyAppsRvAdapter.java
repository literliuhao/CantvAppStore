package com.can.appstore.myapps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by wei on 2016/10/13.
 */

public class MyAppsRvAdapter extends CanRecyclerViewAdapter<AppInfo> {


    List<AppInfo> mAppInfoList = null;
//    private final LayoutInflater mLayoutInflater;

    public MyAppsRvAdapter( List<AppInfo> datas) {
        super(datas);
//        mLayoutInflater = LayoutInflater.from(context);
        mAppInfoList = datas;
    }


    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myapps_list_item, parent, false);
//        View view = mLayoutInflater.inflate(R.layout.myapps_list_item, parent, false);
        MyAppsViewHolder myAppsViewHolder = new MyAppsViewHolder(view);
        return myAppsViewHolder;
    }



    @Override
    protected void bindContentData(AppInfo mDatas, RecyclerView.ViewHolder holder, int position) {
        MyAppsViewHolder myHolder = (MyAppsViewHolder) holder;
        AppInfo app = (AppInfo) mDatas;
        myHolder.tv_name.setText(app.appName);
        myHolder.iv_icon.setImageDrawable(app.appIcon);


    }

    private class MyAppsViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_icon;
        public TextView tv_name;

        public MyAppsViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.myapp_ivicon);
            tv_name = (TextView) itemView.findViewById(R.id.myapp_tvname);
        }

    }


}
