package com.can.appstore.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;

/**
 * Created by yibh on 2016/10/14 18:48 .
 */

public class HotRecommendAdapter extends RecyclerView.Adapter<HotRecommendAdapter.HotViewHolder> {
    public List mDataList;
    private Context mContext;
    public List<View> mViewList = new ArrayList<>(); //存每个Key的View

    public HotRecommendAdapter(List datas, Context context) {
        mDataList = datas;
        mContext = context;
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
            final AppInfo app = (AppInfo) mDataList.get(position);
            mAppName.setText(app.getName());
            ImageLoader.getInstance().load(MyApp.mContext, mAppIcon, app.getIcon());
            //+100是为了防止在搜索页出现相同的id
            mView.setId(position + 100);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppDetailActivity.actionStart(mContext,app.getId());
                }
            });
        }

    }

    public void setDataList(List list) {
        mDataList = list;
        notifyDataSetChanged();
    }


    @Override
    public HotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_app_item, parent, false);
        inflate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (null != mOnFocusChangeListener) {
                    mOnFocusChangeListener.onFocusChange(view, b);
                }
            }
        });
        mViewList.add(inflate);
        return new HotViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(HotViewHolder holder, final int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private View.OnFocusChangeListener mOnFocusChangeListener;

    public void setMyOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        this.mOnFocusChangeListener = onFocusChangeListener;
    }


}
