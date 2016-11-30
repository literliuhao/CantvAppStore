package com.can.appstore.speciallist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.entity.SpecialTopic;

import java.util.List;

import cn.can.tvlib.ui.view.GlideRoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by laifrog on 2016/10/25.
 */

public class SpecialAdapter extends CanRecyclerViewAdapter<SpecialTopic> {

    public SpecialAdapter(List<SpecialTopic> datas) {
        super(datas);
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        return new SubjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_special_topic_item, parent, false));
    }

    @Override
    protected void bindContentData(SpecialTopic data, RecyclerView.ViewHolder holder, int position) {
        final SubjectViewHolder subjectViewHolder = (SubjectViewHolder) holder;
        subjectViewHolder.titleTv.setText(data.getTitle());
        subjectViewHolder.iconImgvi.load(data.getIcon(), R.drawable.bg_item, R.mipmap.icon_load_default);
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private GlideRoundCornerImageView iconImgvi;
        private TextView titleTv;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            iconImgvi = (GlideRoundCornerImageView) itemView.findViewById(R.id.subject_item_imgvi);
            titleTv = (TextView) itemView.findViewById(R.id.subject_item_title_tv);
        }
    }
}
