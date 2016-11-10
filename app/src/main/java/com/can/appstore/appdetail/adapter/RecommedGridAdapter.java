package com.can.appstore.appdetail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by JasonF on 2016/10/25.
 */

public class RecommedGridAdapter extends CanRecyclerViewAdapter {
    private Context mContext;
    private List<AppInfo> mRecommedApps;
    private LayoutInflater mInflater;
    private String mRecommendAppsInfo;

    public RecommedGridAdapter(Context context, List<AppInfo> datas) {
        super(datas);
        this.mContext = context;
        this.mRecommedApps = datas;
        mRecommendAppsInfo = mContext.getResources().getString(R.string.recommend_apps_info);
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View recommendItem = mInflater.inflate(R.layout.adapter_app_detail_recommend_item, parent, false);
        RecommendGridViewHolder viewHolder = new RecommendGridViewHolder(recommendItem);
        return viewHolder;
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
                AppInfo appInfo = mRecommedApps.get(position);
                String text = String.format(mRecommendAppsInfo, appInfo.getSizeStr(), appInfo.getDownloadCount());
                ((RecommendGridViewHolder) holder).itemName.setText(appInfo.getName());
                ((RecommendGridViewHolder) holder).itemSize.setText(text);
        //        ImageLoader.getInstance().buildTask(((RecommendGridViewHolder) holder).itemIcon,appInfo.getUrl()).bitmapTransformation(new GlideRoundTransform(mContext,12)).build().start(mContext);
//        String text = String.format(mRecommendAppsInfo, "83M", "1万+");
//        ((RecommendGridViewHolder) holder).itemName.setText("创意家居");
//        ((RecommendGridViewHolder) holder).itemSize.setText(text);
    }

    class RecommendGridViewHolder extends CanRecyclerView.ViewHolder {
        TextView itemSize;
        TextView itemName;
        ImageView itemIcon;

        public RecommendGridViewHolder(View itemView) {
            super(itemView);
            itemSize = (TextView) itemView.findViewById(R.id.tv_recommend_item_size);
            itemName = (TextView) itemView.findViewById(R.id.tv_recommend_item_name);
            itemIcon = (ImageView) itemView.findViewById(R.id.iv_recommend_item_icon);
        }
    }
}
