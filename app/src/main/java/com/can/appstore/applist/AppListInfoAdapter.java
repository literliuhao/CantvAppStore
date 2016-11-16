package com.can.appstore.applist;

import android.content.Context;
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

public class AppListInfoAdapter extends CanRecyclerViewAdapter {
    private static final String TAG = "AppListInfoAdapter";
    private Context mContext;

    public AppListInfoAdapter(Context context, List datas) {
        super(datas);
        this.mContext = context;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_list_info_item, parent,
                false);
        inflate.setFocusable(true);
        return new AppListInfoAdapter.AppItemViewHolder(inflate);
    }


    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        AppListInfoAdapter.AppItemViewHolder itemViewHolder = (AppListInfoAdapter.AppItemViewHolder) holder;
        AppInfo appInfo = (AppInfo) mDatas;
        final ImageView imageView = itemViewHolder.iv_app_list_item;
        final ImageView errorImage = itemViewHolder.iv_app_list_error;
        ImageLoader.getInstance().load(mContext, imageView, appInfo.getIcon(), new GlideLoadTask.SuccessCallback() {
            @Override
            public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean
                    isFromMemoryCache, boolean isFirstResource) {
                Log.d(TAG, "onSuccess: ");
                imageView.setBackgroundColor(Color.TRANSPARENT);
                return false;
            }
        }, new GlideLoadTask.FailCallback() {
            @Override
            public boolean onFail(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                Log.d(TAG, "onFail: ");
                imageView.setBackgroundResource(R.drawable.shap_app_list_icon_bg);
                errorImage.setVisibility(View.VISIBLE);
                return false;
            }
        });

        itemViewHolder.iv_app_list_name.setText(appInfo.getName());
        itemViewHolder.tv_app_list_size.setText(appInfo.getSizeStr());
        itemViewHolder.tv_app_list_volume.setText(appInfo.getDownloadCount());
        ImageLoader.getInstance().load(mContext, itemViewHolder.iv_app_list_corner, appInfo.getMarker());
    }

    class AppItemViewHolder extends RecyclerView.ViewHolder {

        private RoundCornerImageView iv_app_list_item;
        private ImageView iv_app_list_error;
        private TextView iv_app_list_name;
        private TextView tv_app_list_size;
        private TextView tv_app_list_volume;
        private ImageView iv_app_list_corner;

        public AppItemViewHolder(View itemView) {
            super(itemView);
            iv_app_list_item = (RoundCornerImageView) itemView.findViewById(R.id.iv_app_list_item);
            iv_app_list_error = (ImageView) itemView.findViewById(R.id.iv_app_list_error);
            iv_app_list_name = (TextView) itemView.findViewById(R.id.tv_app_list_name);
            tv_app_list_size = (TextView) itemView.findViewById(R.id.tv_app_list_size);
            tv_app_list_volume = (TextView) itemView.findViewById(R.id.tv_app_list_volume);
            iv_app_list_corner = (ImageView) itemView.findViewById(R.id.iv_app_list_corner);
        }
    }
}
