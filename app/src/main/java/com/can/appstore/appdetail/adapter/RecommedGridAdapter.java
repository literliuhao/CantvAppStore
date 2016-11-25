package com.can.appstore.appdetail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;

import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.view.GlideRoundCornerImageView;
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
    private int mRoundSize;

    public RecommedGridAdapter(Context context, List<AppInfo> datas) {
        super(datas);
        this.mContext = context;
        this.mRecommedApps = datas;
        mRecommendAppsInfo = mContext.getResources().getString(R.string.recommend_apps_info);
        mRoundSize = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_20px);
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
        final RecommendGridViewHolder recommendGridViewHolder = ((RecommendGridViewHolder) holder);
        String text = String.format(mRecommendAppsInfo, appInfo.getSizeStr(), appInfo.getDownloadCount());
        recommendGridViewHolder.itemName.setText(appInfo.getName());
        recommendGridViewHolder.itemSize.setText(text);
        recommendGridViewHolder.itemIcon.load(appInfo.getIcon(), R.drawable.shap_app_list_icon_bg, R.mipmap.cibn_icon, R.mipmap.icon_loading_fail, true);
    }

    class RecommendGridViewHolder extends CanRecyclerView.ViewHolder {
        TextView itemSize;
        TextView itemName;
        GlideRoundCornerImageView itemIcon;

        public RecommendGridViewHolder(View itemView) {
            super(itemView);
            itemSize = (TextView) itemView.findViewById(R.id.tv_recommend_item_size);
            itemName = (TextView) itemView.findViewById(R.id.tv_recommend_item_name);
            itemIcon = (GlideRoundCornerImageView) itemView.findViewById(R.id.iv_recommend_item_icon);
        }
    }
}
