package com.can.appstore.search.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.search.ToastUtil;
import com.can.appstore.search.widget.YIBaseCompatFocusAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by yibh on 2016/10/11 19:51 .
 */

public class KeyboardAdapter extends YIBaseCompatFocusAdapter {

    private List<String> mKeyList;
    private OnItemClickListener mOnItemClickListener;
    private List<View> mViewList = new ArrayList<>(); //存每个Key的View

    public KeyboardAdapter(List datas) {
        super(datas);
        mKeyList = datas;
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ac_search_left_key_item, parent, false);
        mViewList.add(mView);
        return new KeyboardViewHolder(mView);
    }

    @Override
    protected void bindContentData(Object mDatas, final RecyclerView.ViewHolder holder, final int position) {
        ((KeyboardViewHolder) holder).mTextView.setText(mKeyList.get(position));
        ((KeyboardViewHolder) holder).mView.setId(position);
        if (position == 0) {
            //延时处理,为了焦点框能够出现
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((KeyboardViewHolder) holder).mView.requestFocus();
                }
            }, 200);
        }

        if (position == mKeyList.size() - 1) {
            setNextFocus();
        }


        setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClick(position, mKeyList.get(position));
                    ToastUtil.toastShort("key点击: " + mKeyList.get(position));
                }
            }
        });

    }


    public class KeyboardViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private View mView;

        public KeyboardViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.key_item_view_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(int index, String content);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * author: yibh
     * Date: 2016/11/1  17:27 .
     * 1.左侧键盘向左点击会到键盘最右
     * 2.底部键盘向下点击会到键盘最上
     */
    public void setNextFocus() {
        for (int i = 0; i < mViewList.size(); i++) {
            //1
            if (((i + 1) % 6 == 1) && (mViewList.size() == mKeyList.size())) {
                mViewList.get(i).setNextFocusLeftId(mViewList.get(i + 5).getId());
            }
            //2
            if (i >= 30) {
                mViewList.get(i).setNextFocusDownId(mViewList.get(i - 30).getId());
            }
        }
    }

}
