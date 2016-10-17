package cn.can.tvlib.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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

    private LayoutManager mLayoutManager;

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

    public void setLayoutManager(LayoutManager layout, OnFocusSearchCallback callback) {
        layout.setOnFocusSearchFailCallback(callback);
        mLayoutManager = layout;
        super.setLayoutManager(layout);
    }

    public void setAdapter(CanRecyclerViewAdapter adapter) {
        super.setAdapter(adapter);
    }

    public static class LayoutManager extends GridLayoutManager {

        private OnFocusSearchCallback mFocusSearchCallback;

        public void setOnFocusSearchFailCallback(OnFocusSearchCallback callback) {
            mFocusSearchCallback = callback;
        }

        public LayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public LayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public LayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
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
}
