package com.can.appstore.uninstallmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.R;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil;
import cn.can.tvlib.common.text.StringUtils;
import cn.can.tvlib.ui.view.RoundCornerImageView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by JasonF on 2016/10/13.
 */

public class UninstallManagerAdapter extends CanRecyclerViewAdapter<PackageUtil.AppInfo> {
    private static final String TAG = "UninstallManagerAdapter";
    private List<PackageUtil.AppInfo> mInfos = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;

    public UninstallManagerAdapter(Context context, List<PackageUtil.AppInfo> datas) {
        super(datas);
        mContext = context;
        mInfos = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_uninstall_manager_item, parent, false);
        return new UninstallViewHolder(view);
    }

    @Override
    protected void bindContentData(PackageUtil.AppInfo mDatas, RecyclerView.ViewHolder holder, int position) {
        PackageUtil.AppInfo appInfo = mInfos.get(position);
        Log.d(TAG, "bindContentData: " + appInfo.toString());
        UninstallViewHolder uninstallViewHolder = ((UninstallViewHolder) holder);
        uninstallViewHolder.imgIcon.setImageDrawable(appInfo.appIcon);
        uninstallViewHolder.tvName.setText(appInfo.appName);
        uninstallViewHolder.tvSize.setText(StringUtils.formatFileSize(appInfo.size, false));
        uninstallViewHolder.tvVersion.setText(appInfo.versionName);
    }

    private class UninstallViewHolder extends TagViewHolder {
        RoundCornerImageView imgIcon;
        TextView tvName;
        TextView tvSize;
        TextView tvVersion;

        UninstallViewHolder(View itemView) {
            super(itemView);
            imgIcon = (RoundCornerImageView) itemView.findViewById(R.id.iv_icon);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvSize = (TextView) itemView.findViewById(R.id.tv_size);
            tvVersion = (TextView) itemView.findViewById(R.id.tv_version);
        }

        @Override
        protected int specifyTagViewId() {
            return R.id.iv_check;
        }

        @Override
        public void refreshTagViewOnSelectChanged(boolean selected) {
            if (selected) {
                getTagView().setBackgroundResource(R.drawable.select);
            } else {
                getTagView().setBackgroundResource(R.drawable.unselect);
            }
        }
    }
}
