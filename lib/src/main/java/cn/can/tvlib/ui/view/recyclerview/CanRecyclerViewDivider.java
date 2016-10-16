package cn.can.tvlib.ui.view.recyclerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

public class CanRecyclerViewDivider extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mHorizontalDividerSize;
    private int mVerticalDividerSize;

    public CanRecyclerViewDivider(int dividerSizeInPixels) {
        this(Color.TRANSPARENT, dividerSizeInPixels, dividerSizeInPixels);
    }

    public CanRecyclerViewDivider(int dividerColor, int horizontalDividerSize, int verticalDividerSize) {
        this(new ColorDrawable(dividerColor), horizontalDividerSize, verticalDividerSize);
    }

    public CanRecyclerViewDivider(Drawable divider, int horizontalDividerSize, int verticalDividerSize) {
        mDivider = divider;
        if(horizontalDividerSize <= 0 || verticalDividerSize <= 0){
            throw new IllegalArgumentException("The divider size of RecyclerView must be larger than 0.");
        }
        mHorizontalDividerSize = horizontalDividerSize;
        mVerticalDividerSize = verticalDividerSize;
    }

    public void setHorizontalDividerSize(int horizontalDividerSize) {
        this.mHorizontalDividerSize = horizontalDividerSize;
    }

    public void setVerticalDividerSize(int verticalDividerSize) {
        this.mVerticalDividerSize = verticalDividerSize;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager lm = (LinearLayoutManager) layoutManager;
            if(lm.getOrientation() == LinearLayoutManager.VERTICAL){
                drawHorizontal(c, parent);
            } else {
                drawVertical(c, parent);
            }
            return;
        }
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mVerticalDividerSize;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mHorizontalDividerSize;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mVerticalDividerSize;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = parent.getLayoutManager().getPosition(view);
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        if (isLastRaw(parent, itemPosition, spanCount, childCount)){ //最后一行不绘制itemView下面分隔线
            outRect.set(0, 0, mVerticalDividerSize, 0);
        } else if (isLastColumn(parent, itemPosition, spanCount, childCount)){ //最后一列不绘制itemView右边分隔线
            outRect.set(0, 0, 0, mHorizontalDividerSize);
        } else {
            outRect.set(0, 0, mVerticalDividerSize, mHorizontalDividerSize);
        }
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            spanCount = 1;

        } else if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if(layoutManager instanceof LinearLayoutManager){
            return pos == childCount - 1;

        } else if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            return pos >= childCount;

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                return pos >= childCount;
            } else {
                return (pos + 1) % spanCount == 0;
            }
        }
        return false;
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if(layoutManager instanceof LinearLayoutManager){
            return pos == childCount - 1;

        }else if (layoutManager instanceof GridLayoutManager) {
            return (pos + 1) % spanCount == 0;

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (pos + 1) % spanCount == 0;
            } else {
                childCount = childCount - childCount % spanCount;
                return pos >= childCount;
            }
        }
        return false;
    }




}