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
import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.PopularWord;
import com.can.appstore.search.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;


/**
 * Created by yibh on 2016/10/13 17:36 .
 */

public class SearchAppListAdapter extends RecyclerView.Adapter {
    public List mDataList;
    public List mDefaultList;  //"大家都在搜"的数据
    private OnInitialsListener mOnInitialsListener;
    public List<View> mHotKeyViewList = new ArrayList<>(); //存每个热词的View
    private SearchActivity mActivity;
    private boolean isDefault = true; //当前是否处于"大家都在搜"状态
    private int mChangePosition = 0;

    public SearchAppListAdapter(List datas, Context context) {
        mDataList = datas;
        mDefaultList = datas;
        mActivity = (SearchActivity) context;
    }

    public static final int DEFAULT_APPLIST_TYPE = 11;    //默认大家都在搜的类型
    public static final int SEARCH_APPLIST_TYPE = 12;     //搜索出来的类型

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case DEFAULT_APPLIST_TYPE:
                View inflate = mLayoutInflater.inflate(R.layout.search_app_default_item, parent, false);
                if (!mHotKeyViewList.contains(inflate)) {
                    mHotKeyViewList.add(inflate);
                }
                return new DefaultSearchViewHolder(inflate);
            case SEARCH_APPLIST_TYPE:
                View view = mLayoutInflater.inflate(R.layout.search_app_item, parent, false);
                return new SearchViewHolder(view);
        }
        return new RecyclerView.ViewHolder(null) {
        };
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DefaultSearchViewHolder) {
            ((DefaultSearchViewHolder) holder).setContent(position);
        } else {
            final AppInfo app = (AppInfo) mDataList.get(position);
            ImageLoader.getInstance().load(MyApp.getContext(), ((SearchViewHolder) holder).mAppIcon, app.getIcon(), R.mipmap
                    .cibn_icon, R.mipmap.cibn_icon, new GlideLoadTask
                    .SuccessCallback() {
                @Override
                public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean
                        isFromMemoryCache, boolean isFirstResource) {
                    ((SearchViewHolder) holder).mAppIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                    ((SearchViewHolder) holder).mAppIcon.setImageDrawable(resource);
                    ((SearchViewHolder) holder).mAppIcon.setBackgroundColor(Color.TRANSPARENT);
                    return true;
                }
            }, new GlideLoadTask.FailCallback() {
                @Override
                public boolean onFail(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    ((SearchViewHolder) holder).mAppIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    ((SearchViewHolder) holder).mAppIcon.setImageResource(R.mipmap.cibn_icon);
                    ((SearchViewHolder) holder).mAppIcon.setBackgroundResource(R.drawable.shap_app_list_icon_bg);
                    return true;
                }
            });

            ((SearchViewHolder) holder).mAppName.setText(app.getName());
            ((SearchViewHolder) holder).mAppSize.setText(app.getSizeStr());
            ((SearchViewHolder) holder).mAppDownloadCount.setText(app.getDownloadCount());
            //第一行向上焦点是自己
            if (position < mActivity.SEARCH_APP_SPANCOUNT) {
                ((SearchViewHolder) holder).mView.setNextFocusUpId(((SearchViewHolder) holder).mView.getId());
            }
            ((SearchViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppDetailActivity.actionStart(mActivity, app.getId());
                }
            });

            ((SearchViewHolder) holder).mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (null != mYOnFocusChangeListener) {
                        mYOnFocusChangeListener.onItemFocusChanged(view, position, b);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return isDefault ? mDefaultList.size() : mDataList.size();
    }

    /**
     * author: yibh
     * Date: 2016/10/13  17:56 .
     * "大家都在搜"默认的列表
     */
    public class DefaultSearchViewHolder extends RecyclerView.ViewHolder {

        private TextView mAppName;
        private View mView;

        public DefaultSearchViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAppName = (TextView) itemView.findViewById(R.id.default_name_view);
        }

        /**
         * 设置数据
         *
         * @param position
         */
        public void setContent(final int position) {
            final PopularWord app = (PopularWord) mDataList.get(position);
            mAppName.setText(app.getWord());
            //+1000是为了防止在搜索页出现相同的id
            mView.setId(position + 1000);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setInitials(app.getPinyin());
                }
            });
            mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (null != mYOnFocusChangeListener) {
                        mYOnFocusChangeListener.onItemFocusChanged(view, position, b);
                    }
                }
            });
        }
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
        View mView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            mAppName = (TextView) itemView.findViewById(R.id.app_name_view);
            mAppSize = (TextView) itemView.findViewById(R.id.app_size_view);
            mAppDownloadCount = (TextView) itemView.findViewById(R.id.app_dwoncount_view);
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (mDataList.get(position) instanceof PopularWord) {
            return DEFAULT_APPLIST_TYPE;
        } else {
            return SEARCH_APPLIST_TYPE;
        }
    }

    /**
     * 设置数据并刷新
     *
     * @param dataList
     */
    public void setDataList(List dataList, boolean isFirstSearch) {
        isDefault = false;
        mDataList = dataList;
        if (isFirstSearch) {
            mChangePosition = dataList.size();
            notifyDataSetChanged();
        } else {
            notifyItemRangeChanged(mChangePosition, dataList.size() - mChangePosition);
            mChangePosition = dataList.size();
        }
    }


    /**
     * 设置默认数据
     *
     * @param list
     */
    public void setDefaultApplist(List list) {
        isDefault = true;
        if (mDataList.size() > 0) {
            mDataList.clear();
        }
        mDefaultList = list;
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setDefaultApplist() {
        setDefaultApplist(mDefaultList);
    }

    /**
     * author: yibh
     * Date: 2016/10/14  18:18 .
     * 获取首字母的监听
     */
    public interface OnInitialsListener {
        void onInitials(String con);
    }

    public void setOnInitialsListener(OnInitialsListener onInitialsListener) {
        this.mOnInitialsListener = onInitialsListener;
    }

    /**
     * 设置首字母回调
     *
     * @param con
     */
    private void setInitials(String con) {
        if (null != mOnInitialsListener) {
            mOnInitialsListener.onInitials(con);
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
