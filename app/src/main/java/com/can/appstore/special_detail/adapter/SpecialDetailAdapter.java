package com.can.appstore.special_detail.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.special_detail.bean.AppDetail;

import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by atang on 2016/10/24.
 */

public class SpecialDetailAdapter extends CanRecyclerViewAdapter<AppDetail> {
    private Context mContext;

    private List<AppDetail> mAppDetails;

    public SpecialDetailAdapter(List<AppDetail> appDetails, Context context) {
        super(appDetails);
        this.mContext = context;
        this.mAppDetails = appDetails;
    }

    @Override
    protected MyViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.special_detail_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void bindContentData(AppDetail appDetail, RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        if (appDetail == null || viewHolder == null) {
            return;
        }
//        ImageLoader.getInstance().load(mContext, viewHolder.mAppImgView, appDetail.getAppIcon());
        viewHolder.mAppImgView.setImageResource(R.mipmap.ic_launcher);
        viewHolder.mAppNameView.setText(appDetail.getAppName());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mAppImgView;
        TextView mAppNameView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mAppImgView = (ImageView) itemView.findViewById(R.id.special_detail_item_appicon);
            mAppNameView = (TextView) itemView.findViewById(R.id.special_detail_item_appname);
        }
    }
}
