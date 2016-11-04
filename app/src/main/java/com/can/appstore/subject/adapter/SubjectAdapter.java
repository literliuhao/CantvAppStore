package com.can.appstore.subject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.subject.model.SubjectInfo;

import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.view.RoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by laifrog on 2016/10/25.
 */

public class SubjectAdapter extends CanRecyclerViewAdapter<SubjectInfo> {


    private Context mContext;

    public SubjectAdapter(List<SubjectInfo> datas,Context context) {
        super(datas);
        mContext=context;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subject_item,parent,false);
        SubjectViewHolder viewHolder=new SubjectViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void bindContentData(SubjectInfo data, RecyclerView.ViewHolder holder, int position) {
        SubjectViewHolder subjectViewHolder=(SubjectViewHolder) holder;
        subjectViewHolder.titleTv.setText(data.getTitle());
        subjectViewHolder.iconImgvi.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageLoader.getInstance().load(mContext,subjectViewHolder.iconImgvi,data.getIcon());
    }


    public static class  SubjectViewHolder extends RecyclerView.ViewHolder{
        private RoundCornerImageView iconImgvi;
        private TextView titleTv;
        public SubjectViewHolder(View itemView) {
            super(itemView);
            iconImgvi= (RoundCornerImageView) itemView.findViewById(R.id.subject_item_imgvi);
            titleTv= (TextView) itemView.findViewById(R.id.subject_item_title_tv);
        }
    }
}
