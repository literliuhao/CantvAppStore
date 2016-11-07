package com.can.appstore.applist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by syl on 2016/10/19.
 */

public class AppListInfoAdapter extends CanRecyclerViewAdapter {

    private Context mContext;

    public AppListInfoAdapter(Context context, List datas) {
        super(datas);
        this.mContext = context;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_list_info_item, parent, false);
        inflate.setFocusable(true);
        return new AppListInfoAdapter.AppItemViewHolder(inflate);
    }



    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        AppListInfoAdapter.AppItemViewHolder itemViewHolder = (AppListInfoAdapter.AppItemViewHolder) holder;
        AppListInfo appInfo = (AppListInfo) mDatas;
        itemViewHolder.iv_app_list_item.setBackgroundResource(R.mipmap.touxiang);
        itemViewHolder.iv_app_list_name.setText(appInfo.getAppName());
        itemViewHolder.tv_app_list_size.setText(appInfo.getSize());
        itemViewHolder.tv_app_list_volume.setText(appInfo.getDownloadVolume());
        if (appInfo.isNew()) {
            itemViewHolder.iv_app_list_corner.setVisibility(View.VISIBLE);
        } else {
            itemViewHolder.iv_app_list_corner.setVisibility(View.GONE);
        }
    }

    class AppItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_app_list_item;
        private TextView iv_app_list_name;
        private TextView tv_app_list_size;
        private TextView tv_app_list_volume;
        private ImageView iv_app_list_corner;

        public AppItemViewHolder(View itemView) {
            super(itemView);
            iv_app_list_item = (ImageView) itemView.findViewById(R.id.iv_app_list_item);
            iv_app_list_name = (TextView) itemView.findViewById(R.id.tv_app_list_name);
            tv_app_list_size = (TextView) itemView.findViewById(R.id.tv_app_list_size);
            tv_app_list_volume = (TextView) itemView.findViewById(R.id.tv_app_list_volume);
            iv_app_list_corner = (ImageView) itemView.findViewById(R.id.iv_app_list_corner);
        }
    }
}
