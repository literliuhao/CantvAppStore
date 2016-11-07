package com.can.appstore.speciallist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by 4 on 2016/10/24.
 */

public class SpecialListAdapter extends CanRecyclerViewAdapter{

    private Context mContext;

    public SpecialListAdapter(List datas, Context context) {
        super(datas);
        mContext = context;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_special_list_item,parent,false);
        view.setFocusable(true);
        return new ItemViewHolder(view);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
         //   mTextView = (TextView) itemView.findViewById(R.id.tv_app_list);
        }
    }
}
