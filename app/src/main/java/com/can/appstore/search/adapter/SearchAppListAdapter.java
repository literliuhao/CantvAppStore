package com.can.appstore.search.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.search.SearchActivity;

import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;


/**
 * Created by yibh on 2016/10/13 17:36 .
 */

public class SearchAppListAdapter extends RecyclerView.Adapter<SearchAppListAdapter.SearchViewHolder> {
    public List mDataList;
    private SearchActivity mActivity;
    private int mChangePosition = 0;

    public SearchAppListAdapter(List datas, Context context) {
        mDataList = datas;
        mActivity = (SearchActivity) context;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
        View view = mLayoutInflater.inflate(R.layout.search_app_item, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {

        final AppInfo app = (AppInfo) mDataList.get(position);
        ImageLoader.getInstance().load(mActivity, holder.mAppIcon, app.getIcon(), R.mipmap
                .cibn_icon, R.mipmap.cibn_icon, new GlideLoadTask
                .SuccessCallback() {
            @Override
            public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean
                    isFromMemoryCache, boolean isFirstResource) {
                holder.mAppIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.mAppIcon.setImageDrawable(resource);
                holder.mAppIcon.setBackgroundColor(Color.TRANSPARENT);
                return true;
            }
        }, new GlideLoadTask.FailCallback() {
            @Override
            public boolean onFail(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                holder.mAppIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                holder.mAppIcon.setImageResource(R.mipmap.cibn_icon);
                holder.mAppIcon.setBackgroundResource(R.drawable.shap_app_list_icon_bg);
                return true;
            }
        });

        holder.mAppName.setText(app.getName());
        holder.mAppSize.setText(app.getSizeStr());
        holder.mAppDownloadCount.setText(app.getDownloadCount());
        //第一行向上焦点是自己
        if (position < mActivity.SEARCH_APP_SPANCOUNT) {
            holder.mView.setNextFocusUpId(holder.mView.getId());
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDetailActivity.actionStart(mActivity, app.getId(), AppConstants.RESEARCH_PAGE, mActivity.mSearchKeyStr);
            }
        });

        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (null != mYOnFocusChangeListener) {
                    mYOnFocusChangeListener.onItemFocusChanged(view, position, b);
                }
            }
        });

        //标签
        if (null != app.getMarker() && !app.getMarker().equals("")) {
            holder.mAppLabelImg.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().load(mActivity, holder.mAppLabelImg, app.getMarker());
        } else {
            holder.mAppLabelImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    /**
     * author: yibh
     * Date: 2016/10/13  17:57 .
     * 搜索出来的列表
     */
    public class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView mAppName;
        ImageView mAppIcon;
        TextView mAppSize;  //app大小
        TextView mAppDownloadCount; //下载量
        ImageView mAppLabelImg; //标签(左上角展示)
        View mView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            mAppName = (TextView) itemView.findViewById(R.id.app_name_view);
            mAppSize = (TextView) itemView.findViewById(R.id.app_size_view);
            mAppDownloadCount = (TextView) itemView.findViewById(R.id.app_dwoncount_view);
            mAppLabelImg = (ImageView) itemView.findViewById(R.id.label_img);
        }


    }


    /**
     * 设置数据并刷新
     *
     * @param dataList
     */
    public void setDataList(List dataList, boolean isFirstSearch) {
        mDataList = dataList;
        if (isFirstSearch) {
            mChangePosition = dataList.size();
            notifyDataSetChanged();
        } else {
            notifyItemRangeChanged(mChangePosition, dataList.size() - mChangePosition);
            mChangePosition = dataList.size();
        }
    }


    //焦点变化的监听
    private YOnFocusChangeListener mYOnFocusChangeListener;

    public interface YOnFocusChangeListener {
        void onItemFocusChanged(View view, int position, boolean hasFocus);
    }

    public void setOnFocusChangeListener(YOnFocusChangeListener onFocusChangeListener) {
        this.mYOnFocusChangeListener = onFocusChangeListener;
    }

}
