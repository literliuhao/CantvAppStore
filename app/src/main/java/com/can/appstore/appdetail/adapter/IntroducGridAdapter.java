package com.can.appstore.appdetail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.view.RoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by JasonF on 2016/10/28.
 */

public class IntroducGridAdapter extends CanRecyclerViewAdapter {
    private Context mContext;
    private List<String> mIntroduceList;
    private LayoutInflater mInflater;

    public IntroducGridAdapter(Context context, List<String> datas) {
        super(datas);
        this.mContext = context;
        this.mIntroduceList = datas;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View introducItem = mInflater.inflate(R.layout.adapter_app_detail_introduc_item, parent, false);
        IntroducGridViewHolder introducGridViewHolder = new IntroducGridViewHolder(introducItem);
        return introducGridViewHolder;
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        final IntroducGridViewHolder introducGridViewHolder = (IntroducGridViewHolder) holder;
        introducGridViewHolder.introducItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageLoader.getInstance().load(mContext, introducGridViewHolder.introducItem, mIntroduceList.get(position), android.R.anim.fade_in,
                R.mipmap.icon_load_default, R.mipmap.icon_loading_fail, new GlideLoadTask.SuccessCallback() {
                    @Override
                    public boolean onSuccess(GlideDrawable resource, String model,
                                             Target<GlideDrawable> target,
                                             boolean isFromMemoryCache,
                                             boolean isFirstResource) {
                        introducGridViewHolder.introducItem.setScaleType(ImageView.ScaleType.FIT_XY);
                        introducGridViewHolder.introducItem.setImageDrawable(resource);
                        return true;
                    }
                }, null);
    }

    class IntroducGridViewHolder extends CanRecyclerView.ViewHolder {
        RoundCornerImageView introducItem;

        public IntroducGridViewHolder(View itemView) {
            super(itemView);
            introducItem = (RoundCornerImageView) itemView.findViewById(R.id.iv_introduc_item);
        }
    }
}
