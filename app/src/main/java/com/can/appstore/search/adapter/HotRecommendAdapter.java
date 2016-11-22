package com.can.appstore.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.search.widget.YIBaseCompatFocusAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;

/**
 * Created by yibh on 2016/10/14 18:48 .
 */

public class HotRecommendAdapter extends YIBaseCompatFocusAdapter {
    private List mDataList;
    public List<View> mViewList = new ArrayList<>(); //存每个Key的View

    public HotRecommendAdapter(List datas) {
        super(datas);
        mDataList = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                AppInfo searchApp = (AppInfo) mDataList.get(position);
                ToastUtil.toastShort("点击 " + searchApp.getName());
            }
        });
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_app_item, parent, false);
        mViewList.add(inflate);
        return new HotViewHolder(inflate);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        ((HotViewHolder) holder).setContent(position);
    }

    public class HotViewHolder extends RecyclerView.ViewHolder {
        TextView mAppName;
        ImageView mAppIcon;
        View mView;

        public HotViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAppName = (TextView) itemView.findViewById(R.id.app_name_view);
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
        }

        /**
         * 设置数据
         *
         * @param position
         */
        public void setContent(int position) {
            AppInfo app = (AppInfo) mDataList.get(position);
            mAppName.setText(app.getName());
            ImageLoader.getInstance().load(MyApp.mContext, mAppIcon, app.getIcon());
            //+100是为了防止在搜索页出现相同的id
            mView.setId(position + 100);
        }

    }

}
