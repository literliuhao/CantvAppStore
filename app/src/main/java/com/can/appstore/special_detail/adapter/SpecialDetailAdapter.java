package com.can.appstore.special_detail.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.SearchActivity;
import com.can.appstore.special_detail.bean.AppDetail;

import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.CanRecyclerViewFocusHelper;
import cn.can.tvlib.ui.view.CircleImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by atang on 2016/10/24.
 * 专题详情适配器
 */

public class SpecialDetailAdapter extends CanRecyclerViewAdapter<AppDetail> {
    private Context mContext;

    private List<AppDetail> mAppDetails;

    private CanRecyclerViewFocusHelper mCanFocusHelper;

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
        ImageLoader.getInstance().load(mContext, viewHolder.mAppImgView, appDetail.getAppIcon());
        viewHolder.mAppNameView.setText(appDetail.getAppName());

        //暂时写死在layout 中
//        viewHolder.mAppSize.setText(appDetail.getAppSize());
//        viewHolder.mAppDownloadNum.setText(appDetail.getAppDownloadNum());
    }

    /**
     * 为 adapter 提供 viewHolder
     */
    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mAppImgView;
        TextView mAppNameView;
        TextView mAppSize;
        TextView mAppDownloadNum;

        public MyViewHolder(View itemView) {
            super(itemView);
            mAppImgView = (ImageView) itemView.findViewById(R.id.special_detail_item_appicon);
            mAppNameView = (TextView) itemView.findViewById(R.id.special_detail_item_appname);
            mAppSize = (TextView) itemView.findViewById(R.id.special_detail_item_appsize);
            mAppDownloadNum = (TextView) itemView.findViewById(R.id.special_detail_item_appdownloadnum);
        }
    }
}
