package com.can.appstore.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.search.bean.SearchApp;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by yibh on 2016/10/14 18:48 .
 */

public class HotRecommendAdapter extends CanRecyclerViewAdapter {
    private List mDataList;

    public HotRecommendAdapter(List datas) {
        super(datas);
        mDataList = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                SearchApp searchApp = (SearchApp) mDataList.get(position);
                ToastUtil.toastShort("点击 " + searchApp.mName);
            }
        });
//        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_app_item, parent, false);
//        return new HotViewHolder(inflate);
        return null;
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        ((HotViewHolder) holder).setContent(position);
    }

    public class HotViewHolder extends RecyclerView.ViewHolder {
        TextView mAppName;
        ImageView mAppIcon;

        public HotViewHolder(View itemView) {
            super(itemView);
            mAppName = (TextView) itemView.findViewById(R.id.app_name_view);
//            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
        }

        /**
         * 设置数据
         *
         * @param position
         */
        public void setContent(int position) {
            SearchApp app = (SearchApp) mDataList.get(position);
            mAppName.setText(app.mName);
        }

    }

}
