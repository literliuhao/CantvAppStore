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
import com.can.appstore.R;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.PopularWord;
import com.can.appstore.search.SearchActivity;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

import static com.can.appstore.MyApp.mContext;


/**
 * Created by yibh on 2016/10/13 17:36 .
 */

public class SearchAppListAdapter extends CanRecyclerViewAdapter {
    public List mDataList;
    public List mDefaultList;  //"大家都在搜"的数据
    private OnInitialsListener mOnInitialsListener;
    public List<View> mHotKeyViewList = new ArrayList<>(); //存每个热词的View
    private SearchActivity mActivity;

    public SearchAppListAdapter(List datas, Context context) {
        super(datas);
        mDataList = datas;
        mDefaultList = datas;
        mActivity = (SearchActivity) context;
    }

    public static final int DEFAULT_APPLIST_TYPE = 11;    //默认大家都在搜的类型
    public static final int SEARCH_APPLIST_TYPE = 12;     //搜索出来的类型


    @Override
    protected RecyclerView.ViewHolder generateViewHolder(final ViewGroup parent, final int viewType) {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                switch (viewType) {
                    case DEFAULT_APPLIST_TYPE:
                        PopularWord defaultApp = (PopularWord) mDataList.get(position);
                        ToastUtil.toastShort("点击 " + defaultApp.getWord());
                        setInitials(defaultApp.getPinyin());
                        break;
                    case SEARCH_APPLIST_TYPE:
                        AppInfo searchApp = (AppInfo) mDataList.get(position);
                        ToastUtil.toastShort("点击 " + searchApp.getName());
                        AppDetailActivity.actionStart(mActivity, searchApp.getId());
                        break;
                }
            }
        });
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
    protected void bindContentData(Object mDatas, final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DefaultSearchViewHolder) {
            ((DefaultSearchViewHolder) holder).setContent(position);
        } else {
            final AppInfo app = (AppInfo) mDataList.get(position);
            ImageLoader.getInstance().load(mContext, ((SearchViewHolder) holder).mAppIcon, app.getIcon(), R.mipmap
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
            ((SearchViewHolder) holder).mView.setId(position + 10000);
            //第一行向上焦点是自己
            if (position < mActivity.SEARCH_APP_SPANCOUNT) {
                ((SearchViewHolder) holder).mView.setNextFocusUpId(((SearchViewHolder) holder).mView.getId());
            }
        }
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
        public void setContent(int position) {
            PopularWord app = (PopularWord) mDataList.get(position);
            mAppName.setText(app.getWord());
            //+1000是为了防止在搜索页出现相同的id
            mView.setId(position + 1000);
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
    public int getViewType(int position) {
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
    public void setDataList(List dataList) {
        mDataList = dataList;
        setDatas(dataList);
        notifyDataSetChanged();
    }

    /**
     * 设置默认数据
     */
    public void setDefaultApplist() {
        setDataList(mDefaultList);
    }

    /**
     * 设置默认数据
     *
     * @param list
     */
    public void setDefaultApplist(List list) {
        mDefaultList = list;
        setDefaultApplist();
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


}
