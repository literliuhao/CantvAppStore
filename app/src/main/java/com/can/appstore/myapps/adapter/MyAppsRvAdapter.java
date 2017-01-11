package com.can.appstore.myapps.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.myapps.ui.CustomFolderIcon;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;


/**
 * Created by wei on 2016/10/17.
 */

public class MyAppsRvAdapter extends CanRecyclerViewAdapter<PackageUtil.AppInfo> {

    private List<PackageUtil.AppInfo> mList = null;
    private List<Drawable> mCustomSys = null;
    private final int[] COLORS = {
            R.drawable.index_item1_shape,
            R.drawable.index_item2_shape,
            R.drawable.index_item3_shape,
            R.drawable.index_item4_shape,
            R.drawable.index_item7_shape,
            R.drawable.index_item8_shape,
            R.drawable.index_item6_shape,
            R.drawable.index_item5_shape
    };


    //item的两种类型
    private int NOMAL_TYPE = 0X001;
    private int CUSTOM_TYPE = 0X002;

    public MyAppsRvAdapter(List<PackageUtil.AppInfo> datas) {
        super(datas);
        mList = datas;
    }

    /**
     * 设置系统设置的自定义item的数据
     *
     * @param list
     */
    public void setCustomData(List<Drawable> list) {
        if (list != null) {
            mCustomSys = list;
        } else {
            mCustomSys = new ArrayList<Drawable>();
        }
        notifyItemChanged(1);
    }


    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NOMAL_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myapps_list_item, parent, false);
            MyAppsViewHolder mMyAppsViewHolder = new MyAppsViewHolder(view);
            return mMyAppsViewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myapps_list_item_custom, parent, false);
            MyCustomViewHolder mMyCustomViewHolder = new MyCustomViewHolder(view);
            return mMyCustomViewHolder;
        }
    }

    @Override
    protected void bindContentData(final PackageUtil.AppInfo mDatas, RecyclerView.ViewHolder holder, final int position) {
        if (position == 1) {
            MyCustomViewHolder mMyCustomViewHolder = (MyCustomViewHolder) holder;
            mMyCustomViewHolder.mCustomFolderIcon.addMyIcon(mCustomSys);
            mMyCustomViewHolder.mTextView.setText(R.string.system_app);
        } else {
            MyAppsViewHolder myAppsViewHolder = (MyAppsViewHolder) holder;
            myAppsViewHolder.mImageView.setImageDrawable(mList.get(position).appIcon);
            myAppsViewHolder.mTextView.setText(mList.get(position).appName);
            //添加按钮的背景设为透明
            if (position == (mList.size() - 1) && mDatas.packageName.isEmpty()) {
                myAppsViewHolder.mLinearLayout.setBackgroundResource(R.drawable.addapp_bj);
            } else {
                myAppsViewHolder.mLinearLayout.setBackgroundResource(COLORS[position % 8]);
            }
        }


    }


    @Override
    public int getViewType(int position) {
        if (position == 1) {
            return CUSTOM_TYPE;
        } else {
            return NOMAL_TYPE;
        }
    }

    private class MyAppsViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;
        public LinearLayout mLinearLayout;

        public MyAppsViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.myapp_ivicon);
            mTextView = (TextView) itemView.findViewById(R.id.myapp_tvname);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.myapps_item_rlbg);

        }
    }

    private class MyCustomViewHolder extends RecyclerView.ViewHolder {
        public CustomFolderIcon mCustomFolderIcon;
        public TextView mTextView;

        public MyCustomViewHolder(View itemView) {
            super(itemView);
            mCustomFolderIcon = (CustomFolderIcon) itemView.findViewById(R.id.my_icons);
            mTextView = (TextView) itemView.findViewById(R.id.myapps_custom_tv_name);
        }
    }


}
