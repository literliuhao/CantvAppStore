package com.can.appstore.myapps.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.PackageUtil;

/**
 * Created by wei on 2016/11/2.
 */

public class SystemAppsRvAdapter  extends CanRecyclerViewAdapter {
    List<PackageUtil.AppInfo> list;
    int[] mItemColors = {
            R.drawable.bj_01,
            R.drawable.bj_02,
            R.drawable.bj_03,
            R.drawable.bj_04,
            R.drawable.bj_05,
            R.drawable.bj_06,
            R.drawable.bj_07,
            R.drawable.bj_08,
    };
    public SystemAppsRvAdapter(List datas) {
        super(datas);
        this.list = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.systemapps_list_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.imgIcon.setImageDrawable(list.get(position).appIcon);
        myViewHolder.tvName.setText(list.get(position).appName);
        myViewHolder.llbg.setBackgroundResource(mItemColors[position % 8]);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgIcon;
        TextView tvName;
        LinearLayout llbg;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgIcon = (ImageView)itemView.findViewById(R.id.systemapps_item_ivicon);
            tvName = (TextView)itemView.findViewById(R.id.systemapps_item_tvname);
            llbg = (LinearLayout) itemView.findViewById(R.id.systemapps_llbg);
        }


    }
}
