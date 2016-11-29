package com.can.appstore.myapps.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

/**
 * Created by wei on 2016/10/31.
 */

public class AddAppsRvAdapter extends CanRecyclerViewAdapter {
    private List<PackageUtil.AppInfo> data;
    private final int[] COLORS = {
            R.drawable.index_item1_shape,
            R.drawable.index_item2_shape,
            R.drawable.index_item3_shape,
            R.drawable.index_item4_shape,
            R.drawable.index_item6_shape,
            R.drawable.index_item7_shape,
            R.drawable.index_item5_shape,
            R.drawable.index_item8_shape
    };

    public AddAppsRvAdapter(List datas) {
        super(datas);
        this.data = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addapps_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        AppInfo app = data.get(position);
        myViewHolder.imgIcon.setImageDrawable(app.appIcon);
        myViewHolder.tvName.setText(app.appName);
        myViewHolder.llbg.setBackgroundResource(COLORS[position % 8]);

    }

    private class MyViewHolder extends TagViewHolder {
        ImageView imgIcon;
        TextView tvName;
        LinearLayout llbg;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgIcon = (ImageView) itemView.findViewById(R.id.addapps_item_ivicon);
            tvName = (TextView) itemView.findViewById(R.id.addapps_item_tvname);
            llbg = (LinearLayout) itemView.findViewById(R.id.addapps_llbg);
        }

        @Override
        protected int specifyTagViewId() {
            return R.id.addapps_iv_check;
        }

        @Override
        public void refreshTagViewOnSelectChanged(boolean selected) throws IllegalStateException {
            if (selected) {
                getTagView().setBackgroundResource(R.drawable.select);
            } else {
                getTagView().setBackgroundResource(R.drawable.unselect);
            }
        }
    }


}
