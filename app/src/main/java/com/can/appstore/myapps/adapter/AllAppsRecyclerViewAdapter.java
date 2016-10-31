package com.can.appstore.myapps.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.myapps.model.AppInfo;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by wei on 2016/10/27.
 */

public class AllAppsRecyclerViewAdapter extends CanRecyclerViewAdapter{
 List<AppInfo> list ;
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

    public AllAppsRecyclerViewAdapter(List datas) {
        super(datas);
        this.list = datas;
    }

    @Override
    //填充item
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allapps_list_item,parent,false);
        AllAppsViewHolder  viewHolder = new AllAppsViewHolder(view);
        return viewHolder;
    }


    //设置数据
    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        AllAppsViewHolder viewHolder = (AllAppsViewHolder) holder;
        viewHolder.mImageView.setImageDrawable(list.get(position).appIcon);
        viewHolder.mTextView.setText(list.get(position).appName);
        viewHolder.mLinearLayout.setBackgroundResource(mItemColors[position % 8]);
    }

    private  class AllAppsViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public LinearLayout mLinearLayout;

        public AllAppsViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.allapps_iv_icon);
            mTextView = (TextView) itemView.findViewById(R.id.allapps_tv_name);
            mLinearLayout= (LinearLayout) itemView.findViewById(R.id.allapps_ll_bg);

        }

    }
}
