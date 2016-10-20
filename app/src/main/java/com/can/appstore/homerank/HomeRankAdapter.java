package com.can.appstore.homerank;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by yibh on 2016/10/20 17:11 .
 */

public class HomeRankAdapter extends CanRecyclerViewAdapter {
    private List mList;
    public HomeRankAdapter(List datas) {
        super(datas);
        this.mList = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_list_item, parent, false);
        return new RankAppItemViewHolder(view);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {

    }

    class RankAppItemViewHolder extends RecyclerView.ViewHolder{

        public RankAppItemViewHolder(View itemView) {
            super(itemView);
        }
    }

}
