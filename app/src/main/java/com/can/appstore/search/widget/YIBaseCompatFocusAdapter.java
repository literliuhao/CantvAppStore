package com.can.appstore.search.widget;

import android.view.View;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by yibh on 2016/10/31 20:07 .
 */

public abstract class YIBaseCompatFocusAdapter extends CanRecyclerViewAdapter {
    private View.OnFocusChangeListener mOnFocusChangeListener;


    public YIBaseCompatFocusAdapter(List datas) {
        super(datas);
        setHasStableIds(true);
    }

    /**
     * 为了和非Adapter的View能够通用,所以写了这个方法,如:搜索页的"删除","清空"按钮
     *
     * @param listener
     */
    public void setMyOnFocusChangeListener(View.OnFocusChangeListener listener) {
        this.mOnFocusChangeListener = listener;
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (null != mOnFocusChangeListener) {
                    mOnFocusChangeListener.onFocusChange(view, hasFocus);
                }
            }
        });
    }

}
