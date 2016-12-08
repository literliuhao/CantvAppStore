package com.can.appstore.installpkg;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.update.model.AppInfoBean;

import java.util.List;

import cn.can.tvlib.ui.view.RoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by shenpx on 2016/10/12 0012.
 */

public class InstallManagerAdapter extends CanRecyclerViewAdapter<AppInfoBean> {

    private List<AppInfoBean> mDatas;

    public InstallManagerAdapter(List<AppInfoBean> datas) {
        super(datas);
        mDatas = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_installmanager_item, parent, false);
        InstallViewHolder holder = new InstallViewHolder(view);
        return holder;
    }

    @Override
    protected void bindContentData(AppInfoBean data, RecyclerView.ViewHolder holder, int position) {
        InstallViewHolder Installholder = (InstallViewHolder) holder;
        Installholder.appName.setText(mDatas.get(position).getAppName());
        Installholder.appSize.setText(mDatas.get(position).getAppSize());
        Installholder.appVersioncode.setText(mDatas.get(position).getVersionName());
        Installholder.appIcon.setImageDrawable(mDatas.get(position).getIcon());
        Installholder.installIcon.setVisibility(mDatas.get(position).getInstall() ? View.VISIBLE : View.INVISIBLE);
        Installholder.installing.setVisibility(mDatas.get(position).getIsInstalling() ? (mDatas.get(position).getInstall() ? View.INVISIBLE : View.VISIBLE) : View.INVISIBLE);
        Installholder.installing.setText(mDatas.get(position).getInstalledFalse() ? "安装失败" : "安装中");
    }

    class InstallViewHolder extends RecyclerView.ViewHolder {
        RoundCornerImageView appIcon;
        TextView appName;
        TextView appVersioncode;
        TextView appSize;
        ImageView installIcon;
        TextView installing;

        public InstallViewHolder(View view) {
            super(view);
            appName = (TextView) view.findViewById(R.id.tv_installpkg_name);
            appSize = (TextView) view.findViewById(R.id.tv_installpkg_size);
            appVersioncode = (TextView) view.findViewById(R.id.tv_installpkg_versioncode);
            appIcon = (RoundCornerImageView) view.findViewById(R.id.iv_installpkg_icon);
            installIcon = (ImageView) view.findViewById(R.id.iv_installpkg_installicon);
            installing = (TextView) view.findViewById(R.id.tv_install_installing);
        }
    }
}
