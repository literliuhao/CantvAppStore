package com.can.appstore.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.entity.PopularWord;
import com.can.appstore.search.SearchActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibh on 2016/12/29.
 */

public class HotKeyAdapter extends RecyclerView.Adapter<HotKeyAdapter.HotKeyViewHolder> {

    public List mDataList;
    private OnInitialsListener mOnInitialsListener;
    public List<View> mHotKeyViewList = new ArrayList<>(); //存每个热词的View
    private SearchActivity mActivity;

    public HotKeyAdapter(List datas, Context context) {
        mDataList = datas;
        mActivity = (SearchActivity) context;
    }

    @Override
    public HotKeyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_app_default_item, parent, false);
        if (!mHotKeyViewList.contains(view)) {
            mHotKeyViewList.add(view);
        }
        return new HotKeyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HotKeyViewHolder holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        /**修复 bugly #568 空指针bug 2017-2-3 11:31:38 xzl*/
        return mDataList != null ? mDataList.size() : 0;
    }

    /**
     * author: yibh
     * Date: 2016/10/13  17:56 .
     * "大家都在搜"默认的列表
     */
    public class HotKeyViewHolder extends RecyclerView.ViewHolder {

        private TextView mAppName;
        private View mView;

        public HotKeyViewHolder(View itemView) {
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
                    if (null != mOnFocusChangeListener) {
                        mOnFocusChangeListener.onFocusChange(view, b);
                    }
                }
            });
        }
    }

    /**
     * 刷新数据
     *
     * @param list
     */
    public void refresh(List list) {
        mDataList = list;
        notifyDataSetChanged();
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

    private View.OnFocusChangeListener mOnFocusChangeListener;

    public void setMyOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

}
