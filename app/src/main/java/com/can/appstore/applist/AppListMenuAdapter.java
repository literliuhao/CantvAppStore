package com.can.appstore.applist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by syl on 2016/10/18.
 */

public class AppListMenuAdapter extends CanRecyclerViewAdapter {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SEARCH = 1;
    private Context mContext;
    private List mData;

    public AppListMenuAdapter(Context context, List data) {
        super(data);
        mData = data;
        this.mContext = context;
    }

    @Override
    public int getViewType(int position) {
        AppListMenuInfo info = (AppListMenuInfo) mData.get(position);
        if(AppListPresenter.DEFAULT_TOPIC.equals(info.getId())){
            return TYPE_SEARCH;
        }
        return TYPE_NORMAL;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_list_item, parent, false);
        inflate.setFocusable(true);
        return new ItemViewHolder(inflate);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        AppListMenuInfo info = (AppListMenuInfo) mDatas;
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.mTextView.setText(info.getName());
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_app_list);
        }
    }
}
