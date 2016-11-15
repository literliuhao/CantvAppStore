package com.can.appstore.specialtopic.adapter;

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
import com.can.appstore.entity.SpecialTopic;

import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.view.RoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by laifrog on 2016/10/25.
 */

public class SpecialAdapter extends CanRecyclerViewAdapter<SpecialTopic> {


    private Context mContext;

    public SpecialAdapter(List<SpecialTopic> datas, Context context) {
        super(datas);
        mContext = context;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_special_topic_item, parent, false);
        SubjectViewHolder viewHolder = new SubjectViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void bindContentData(SpecialTopic data, RecyclerView.ViewHolder holder, int position) {
        final SubjectViewHolder subjectViewHolder = (SubjectViewHolder) holder;
        subjectViewHolder.titleTv.setText(data.getTitle());
        subjectViewHolder.iconImgvi.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageLoader.getInstance().load(mContext, subjectViewHolder.iconImgvi, data.getIcon(),android.R.anim.fade_in,
                R.mipmap.icon_load_default, R.mipmap.icon_loading_fail, new GlideLoadTask.SuccessCallback() {
                    @Override
                    public boolean onSuccess(GlideDrawable resource, String model,
                                             Target<GlideDrawable> target,
                                             boolean isFromMemoryCache,
                                             boolean isFirstResource) {
                        subjectViewHolder.iconImgvi.setScaleType(ImageView.ScaleType.FIT_XY);
                        subjectViewHolder.iconImgvi.setImageDrawable(resource);
                        return true;
                    }
                }, null);
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private RoundCornerImageView iconImgvi;
        private TextView titleTv;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            iconImgvi = (RoundCornerImageView) itemView.findViewById(R.id.subject_item_imgvi);
            titleTv = (TextView) itemView.findViewById(R.id.subject_item_title_tv);
        }
    }
}
