package com.can.appstore.update;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.installpkg.InstallManagerAdapter;
import com.can.appstore.update.model.AppInfoBean;

import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by shenpx on 2016/10/12 0012.
 */

public class UpdateManagerAdapter extends CanRecyclerViewAdapter<AppInfoBean> {

    private List<AppInfoBean> mDatas;

    public UpdateManagerAdapter(List<AppInfoBean> datas) {
        super(datas);
        mDatas = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_updatemanager_item, parent, false);
        UpdateViewHolder holder = new UpdateViewHolder(view);
        return holder;
    }

    @Override
    protected void bindContentData(AppInfoBean date, RecyclerView.ViewHolder holder, int position) {
        UpdateViewHolder updateHolder = (UpdateViewHolder)holder;
        updateHolder.appName.setText(mDatas.get(position).getAppName());
        updateHolder.appSize.setText(mDatas.get(position).getAppSize());
        updateHolder.appVersioncode.setText(mDatas.get(position).getVersionName());
        updateHolder.appIcon.setImageDrawable(mDatas.get(position).getIcon());
//        ImageLoader.getInstance().load(MyApp.mContext,updateHolder.appIcon,mDatas.get(position).getIconUrl(),0,0,null,null);
        updateHolder.updatedIcon.setVisibility(mDatas.get(position).getInstall() ? View.VISIBLE : View.INVISIBLE);
        updateHolder.downloading.setVisibility(mDatas.get(position).getIsInstalling() ? (mDatas.get(position).getInstall() ? View.INVISIBLE : View.VISIBLE) : View.INVISIBLE);
    }

    class UpdateViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appVersioncode;
        TextView appSize;
        ProgressBar progressbar;
        ImageView updatedIcon;
        TextView downloading;

        public UpdateViewHolder(View view) {
            super(view);
            appName = (TextView) view.findViewById(R.id.tv_updateapp_name);
            appSize = (TextView) view.findViewById(R.id.tv_updateapp_size);
            appVersioncode = (TextView) view.findViewById(R.id.tv_updateapp_versioncode);
            appIcon = (ImageView) view.findViewById(R.id.iv_updateapp_icon);
            progressbar = (ProgressBar) view.findViewById(R.id.pb_updateapp_progressbar);
            updatedIcon = (ImageView) view.findViewById(R.id.iv_updateapp_updatedicon);
            downloading = (TextView) view.findViewById(R.id.tv_updateapp_downloading);
        }
    }
}
