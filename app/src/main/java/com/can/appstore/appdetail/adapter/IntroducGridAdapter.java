package com.can.appstore.appdetail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.GlideRoundCornerImageView;
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
        return new IntroducGridViewHolder(introducItem);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        Log.d("fjm", "bindContentData: ");
        final IntroducGridViewHolder introducGridViewHolder = (IntroducGridViewHolder) holder;
        introducGridViewHolder.introducItem.load(mIntroduceList.get(position), R.drawable.shap_image_bg,
                R.mipmap.cibn_picture, R.mipmap.icon_loading_fail, true);
    }

    public void setData(List<String> thumbs) {
        mDatas.clear();
        mIntroduceList.clear();
        mDatas.addAll(thumbs);
        mIntroduceList = thumbs;
    }

    private class IntroducGridViewHolder extends CanRecyclerView.ViewHolder {
        GlideRoundCornerImageView introducItem;

        IntroducGridViewHolder(View itemView) {
            super(itemView);
            introducItem = (GlideRoundCornerImageView) itemView.findViewById(R.id.iv_introduc_item);
        }
    }
}
