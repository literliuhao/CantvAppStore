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
 * Created by wei on 2016/10/27.
 */

public class AllAppsRecyclerViewAdapter extends CanRecyclerViewAdapter {
    private List<PackageUtil.AppInfo> list;
    //圆角色块
    private final int[] COLORS = {
            R.drawable.index_item1_shape,
            R.drawable.index_item2_shape,
            R.drawable.index_item3_shape,
            R.drawable.index_item4_shape,
            R.drawable.index_item6_shape,
            R.drawable.index_item7_shape,
            R.drawable.index_item5_shape,
            R.drawable.index_item8_shape
    };


    public AllAppsRecyclerViewAdapter(List datas) {
        super(datas);
        this.list = datas;
    }

    @Override
    //填充item
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allapps_list_item, parent, false);
        AllAppsViewHolder viewHolder = new AllAppsViewHolder(view);
        return viewHolder;
    }


    //设置数据
    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        AllAppsViewHolder viewHolder = (AllAppsViewHolder) holder;
        viewHolder.mImageView.setImageDrawable(list.get(position).appIcon);
        viewHolder.mTextView.setText(list.get(position).appName);
        viewHolder.mLinearLayout.setBackgroundResource(COLORS[position % 8]);
    }

    private class AllAppsViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;
        public LinearLayout mLinearLayout;

        public AllAppsViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.allapps_iv_icon);
            mTextView = (TextView) itemView.findViewById(R.id.allapps_tv_name);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.allapps_ll_bg);

        }
    }
}
