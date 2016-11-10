package com.can.appstore.appdetail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by JasonF on 2016/10/28.
 */

public class IntroducGridAdapter extends CanRecyclerViewAdapter {
    private Context mContext;
    private List<String> mIntroduceList;
    private LayoutInflater mInflater;
    int[] imgRes = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};

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
        ((IntroducGridViewHolder) holder).introducItem.setImageResource(imgRes[position]);
        // TODO 加载图片
    }

    class IntroducGridViewHolder extends CanRecyclerView.ViewHolder {
        ImageView introducItem;

        public IntroducGridViewHolder(View itemView) {
            super(itemView);
            introducItem = (ImageView) itemView.findViewById(R.id.iv_introduc_item);
        }
    }
}
