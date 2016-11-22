package com.can.appstore.applist.adpter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import cn.can.tvlib.ui.view.RoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by syl on 2016/10/19.
 */

public class AppListInfoAdapter extends CanRecyclerViewAdapter<AppInfo> {
    private static final String TAG = "AppListInfoAdapter";

    public AppListInfoAdapter(List datas) {
        super(datas);
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_list_info_item, parent,
                false);
        inflate.setFocusable(true);
        return new AppItemViewHolder(inflate);
    }


    @Override
    protected void bindContentData(AppInfo mDatas, RecyclerView.ViewHolder holder, int position) {
        AppItemViewHolder itemViewHolder = (AppItemViewHolder) holder;
        final ImageView imageView = itemViewHolder.iv_app_list_item;
        imageView.setBackgroundResource(R.drawable.shap_app_list_icon_bg);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageLoader.getInstance().load(getAttachedView().getContext(), imageView, mDatas.getIcon(), R.mipmap
                .cibn_icon, R.mipmap.cibn_icon, new GlideLoadTask
                .SuccessCallback() {
            @Override
            public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean
                    isFromMemoryCache, boolean isFirstResource) {
                Log.d(TAG, "onSuccess: ");
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageDrawable(resource);
                imageView.setBackgroundColor(Color.TRANSPARENT);
                return true;
            }
        }, new GlideLoadTask.FailCallback() {
            @Override
            public boolean onFail(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                Log.d(TAG, "onFail: ");
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setImageResource(R.mipmap.cibn_icon);
                imageView.setBackgroundResource(R.drawable.shap_app_list_icon_bg);
                return true;
            }
        });

        itemViewHolder.iv_app_list_name.setText(mDatas.getName());
        itemViewHolder.tv_app_list_size.setText(mDatas.getSizeStr());
        itemViewHolder.tv_app_list_volume.setText(mDatas.getDownloadCount());
        ImageLoader.getInstance().load(getAttachedView().getContext(), itemViewHolder.iv_app_list_corner, mDatas
                .getMarker());
    }

    class AppItemViewHolder extends RecyclerView.ViewHolder {

        private RoundCornerImageView iv_app_list_item;
        private TextView iv_app_list_name;
        private TextView tv_app_list_size;
        private TextView tv_app_list_volume;
        private ImageView iv_app_list_corner;

        public AppItemViewHolder(View itemView) {
            super(itemView);
            iv_app_list_item = (RoundCornerImageView) itemView.findViewById(R.id.iv_app_list_item);
            iv_app_list_name = (TextView) itemView.findViewById(R.id.tv_app_list_name);
            tv_app_list_size = (TextView) itemView.findViewById(R.id.tv_app_list_size);
            tv_app_list_volume = (TextView) itemView.findViewById(R.id.tv_app_list_volume);
            iv_app_list_corner = (ImageView) itemView.findViewById(R.id.iv_app_list_corner);
        }
    }
}
