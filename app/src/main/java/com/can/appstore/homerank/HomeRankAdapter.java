package com.can.appstore.homerank;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.ToastUtils;

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
    protected RecyclerView.ViewHolder generateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_list_item, parent, false);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                ToastUtils.showMessage(parent.getContext(), ((AppInfo) mList.get(position)).getName());
            }
        });
        return new RankAppItemViewHolder(view);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RankAppItemViewHolder) {
            AppInfo mApp = (AppInfo) mList.get(position);
            ((RankAppItemViewHolder) holder).mAppName.setText(mApp.getName());
        }
    }

    class RankAppItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView mAppIcon;
        private TextView mAppName;

        public RankAppItemViewHolder(View itemView) {
            super(itemView);
            mAppIcon = (ImageView) itemView.findViewById(R.id.icon_view);
            mAppName = (TextView) itemView.findViewById(R.id.app_name_view);
        }
    }

}
