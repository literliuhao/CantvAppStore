package cn.can.tvlib.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：RecyclerView封装
 * 修订历史：
 *      1.0
 *          1. 添加焦点搜索失败的回调
 *          2. 解决item获取焦点放大时相互遮挡的问题
 * ================================================
 */
public class CanRecyclerView extends RecyclerView {

    private CanGridLayoutManager mCanGridLayoutManager;

    public interface OnFocusSearchCallback {

        public void onSuccess(View view, View focused, int focusDirection, Recycler recycler, State state);

        public void onFail(View focused, int focusDirection, Recycler recycler, State state);
    }

    public CanRecyclerView(Context context) {
        this(context, null, 0);
    }

    public CanRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        super.setOnFocusChangeListener(l);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int focusIndex = getFocusChildIndex();
        if (focusIndex < 0) {
            return i;
        }
        if (i == childCount - 1) {
            return focusIndex;
        } else if (i < focusIndex) {
            return i;
        } else {
            return i + 1;
        }
    }

    private int getFocusChildIndex() {
        int focusIndex = -1;
        for (int i = 0; i < getChildCount(); i++) {
            if (getFocusedChild() == getChildAt(i)) {
                focusIndex = i;
            }
        }
        return focusIndex;
    }

    public void setLayoutManager(CanGridLayoutManager layout, OnFocusSearchCallback callback) {
        layout.setOnFocusSearchFailCallback(callback);
        mCanGridLayoutManager = layout;
        super.setLayoutManager(layout);
    }

    public void setAdapter(CanRecyclerViewAdapter adapter) {
        super.setAdapter(adapter);
    }

    public static class CanGridLayoutManager extends GridLayoutManager {

        private OnFocusSearchCallback mFocusSearchCallback;

        public void setOnFocusSearchFailCallback(OnFocusSearchCallback callback) {
            mFocusSearchCallback = callback;
        }

        public CanGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public CanGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public CanGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        public View onFocusSearchFailed(View focused, int focusDirection, Recycler recycler, State state) {
            View view = super.onFocusSearchFailed(focused, focusDirection, recycler, state);
            if (mFocusSearchCallback != null) {
                if (view != null) {
                    mFocusSearchCallback.onSuccess(view, focused, focusDirection, recycler, state);
                } else {
                    mFocusSearchCallback.onFail(focused, focusDirection, recycler, state);
                }
            }
            return view;
        }
    }

    public static class CanLinearLayoutManager extends LinearLayoutManager {

        private OnFocusSearchCallback mFocusSearchCallback;

        public CanLinearLayoutManager(Context context) {
            super(context);
        }

        public CanLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public CanLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public void setOnFocusSearchFailCallback(OnFocusSearchCallback callback) {
            mFocusSearchCallback = callback;
        }

        @Override
        public View onFocusSearchFailed(View focused, int focusDirection, Recycler recycler, State state) {
            View view = super.onFocusSearchFailed(focused, focusDirection, recycler, state);
            if (mFocusSearchCallback != null) {
                if (view != null) {
                    mFocusSearchCallback.onSuccess(view, focused, focusDirection, recycler, state);
                } else {
                    mFocusSearchCallback.onFail(focused, focusDirection, recycler, state);
                }
            }
            return view;
        }

        @Override
        public void onLayoutChildren(Recycler recycler, State state) {
            try {
                //notifyitemremove 时报RecyclerView: java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid item position
                //未测试会不会引起其他坑
                super.onLayoutChildren(recycler, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
