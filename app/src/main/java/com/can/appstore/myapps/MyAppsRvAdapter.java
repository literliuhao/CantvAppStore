package com.can.appstore.myapps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by wei on 2016/10/17.
 */

public class MyAppsRvAdapter extends CanRecyclerViewAdapter<AppInfo>{

    public MyAppsRvAdapter(List<AppInfo> datas) {
        super(datas);
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myapps_list_item,parent,false);
        MyAppsViewHolder mMyAppsViewHolder = new MyAppsViewHolder(view);
        return mMyAppsViewHolder;
    }

    @Override
    protected void bindContentData(AppInfo mDatas, RecyclerView.ViewHolder holder, int position) {
        MyAppsViewHolder myAppsViewHolder = (MyAppsViewHolder)holder;
        myAppsViewHolder.mImageView.setImageDrawable(mDatas.appIcon);
        myAppsViewHolder.mTextView.setText(mDatas.appName);

    }

    private  class MyAppsViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;

        public MyAppsViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.myapp_ivicon);
            mTextView = (TextView) itemView.findViewById(R.id.myapp_tvname);
        }

    }


}
