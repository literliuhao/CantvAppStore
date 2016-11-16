package com.can.appstore.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.search.bean.DefaultApp;
import com.can.appstore.search.bean.SearchApp;
import com.can.appstore.search.widget.YIBaseCompatFocusAdapter;

import java.util.List;


/**
 * Created by yibh on 2016/10/13 17:36 .
 */

public class SearchAppListAdapter extends YIBaseCompatFocusAdapter {
    private List mDataList;
    private List mDefaultList;  //"大家都在搜"的数据
    private OnInitialsListener mOnInitialsListener;

    public SearchAppListAdapter(List datas) {
        super(datas);
        mDataList = datas;
        mDefaultList = datas;
    }

    private static final int DEFAULT_APPLIST_TYPE = 11;    //默认大家都在搜的类型
    private static final int SEARCH_APPLIST_TYPE = 12;     //搜索出来的类型


    @Override
    protected RecyclerView.ViewHolder generateViewHolder(final ViewGroup parent, final int viewType) {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                switch (viewType) {
                    case DEFAULT_APPLIST_TYPE:
                        DefaultApp defaultApp = (DefaultApp) mDataList.get(position);
                        ToastUtil.toastShort("点击 " + defaultApp.mName);
                        setInitials(defaultApp.mInitials);
                        break;
                    case SEARCH_APPLIST_TYPE:
                        SearchApp searchApp = (SearchApp) mDataList.get(position);
                        ToastUtil.toastShort("点击 " + searchApp.mName);
                        break;
                }
            }
        });
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case DEFAULT_APPLIST_TYPE:
                View inflate = mLayoutInflater.inflate(R.layout.search_app_default_item, parent, false);
                return new DefaultSearchViewHolder(inflate);
            case SEARCH_APPLIST_TYPE:
                View view = mLayoutInflater.inflate(R.layout.search_app_item, parent, false);
                return new SearchViewHolder(view);
        }
        return new RecyclerView.ViewHolder(null) {
        };
    }


    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DefaultSearchViewHolder) {
            ((DefaultSearchViewHolder) holder).setContent(position);
        } else {
            SearchApp app = (SearchApp) mDataList.get(position);
//            ((SearchViewHolder) holder).mAppIcon.setBackground();
        ((SearchViewHolder) holder).mAppName.setText(app.mName);
//            ((SearchViewHolder) holder).mAppSize.setText("");
//            ((SearchViewHolder) holder).mAppDownloadCount.setText("");
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
            DefaultApp app = (DefaultApp) mDataList.get(position);
            mAppName.setText(app.mName);
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

        public SearchViewHolder(View itemView) {
            super(itemView);
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            mAppName = (TextView) itemView.findViewById(R.id.app_name_view);
            mAppSize = (TextView) itemView.findViewById(R.id.app_size_view);
            mAppDownloadCount = (TextView) itemView.findViewById(R.id.app_dwoncount_view);
        }

//        /**
//         * 设置数据
//         *
//         * @param position
//         */
//        public void setContent(int position) {
//            SearchApp app = (SearchApp) mDataList.get(position);
//            mAppName.setText(app.mName);
//        }

    }

    @Override
    public int getViewType(int position) {
        if (mDataList.get(position) instanceof DefaultApp) {
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
