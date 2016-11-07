package com.can.appstore.uninstallmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.appdetail.AppInfo;
import com.can.appstore.appdetail.AppUtils;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by JasonF on 2016/10/13.
 */

public class UninstallManagerAdapter extends CanRecyclerViewAdapter<AppInfo> {
    private List<AppInfo> mInfos = new ArrayList<AppInfo>();
    private Context mContext;
    private LayoutInflater mInflater;

    public UninstallManagerAdapter(Context context, List<AppInfo> datas) {
        super(datas);
        mContext = context;
        mInfos = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_uninstall_manager_item, parent, false);
        UninstallViewHolder viewHolder = new UninstallViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void bindContentData(AppInfo mDatas, RecyclerView.ViewHolder holder, int position) {
        AppInfo appInfo = mInfos.get(position);
        ((UninstallViewHolder) holder).imgIcon.setImageDrawable(appInfo.getAppIcon());
        ((UninstallViewHolder) holder).tvName.setText(appInfo.getAppName());
        ((UninstallViewHolder) holder).tvSize.setText(AppUtils.FormetFileSize(appInfo.getSize()));
        ((UninstallViewHolder) holder).tvVersion.setText(appInfo.getVersionName());
    }

    class UninstallViewHolder extends TagViewHolder {
        ImageView imgIcon;
        TextView tvName;
        TextView tvSize;
        TextView tvVersion;

        public UninstallViewHolder(View itemView) {
            super(itemView);
            imgIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
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
