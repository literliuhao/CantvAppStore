package com.can.appstore.specialdetail;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;

import java.util.List;

import cn.can.tvlib.ui.view.GlideRoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * sss
 * Created by Fuwen on 2016/10/24.
 * 专题详情适配器
 */

public class SpecialDetailAdapter extends CanRecyclerViewAdapter<AppInfo> {
    private Context mContext;

    public SpecialDetailAdapter(List<AppInfo> appInfos, Context context) {
        super(appInfos);
        this.mContext = context;
    }

    @Override
    protected MyViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.special_detail_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    protected void bindContentData(AppInfo appInfo, RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.mAppImgView.load(appInfo.getIcon());
        viewHolder.mAppNameView.setText(appInfo.getName());
        viewHolder.mAppSize.setText(appInfo.getSizeStr());
        viewHolder.mAppDownloadNum.setText(appInfo.getDownloadCount());
    }

    /**
     * 为 adapter 提供 viewHolder
     */
    private class MyViewHolder extends RecyclerView.ViewHolder {

        GlideRoundCornerImageView mAppImgView;
        TextView mAppNameView;
        TextView mAppSize;
        TextView mAppDownloadNum;

        MyViewHolder(View itemView) {
            super(itemView);
            mAppImgView = (GlideRoundCornerImageView) itemView.findViewById(R.id.special_detail_item_appicon);
            mAppNameView = (TextView) itemView.findViewById(R.id.special_detail_item_appname);
            mAppSize = (TextView) itemView.findViewById(R.id.special_detail_item_appsize);
            mAppDownloadNum = (TextView) itemView.findViewById(R.id.special_detail_item_appdownloadnum);
        }
    }
}
