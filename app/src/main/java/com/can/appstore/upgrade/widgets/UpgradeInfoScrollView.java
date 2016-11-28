package com.can.appstore.upgrade.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by syl on 2016/10/31.
 */

public class UpgradeInfoScrollView extends ScrollView{
    private static final String TAG = "UpgradeInfoScrollView";
    private float mNoticeScrollViewTotalHeight;
    private float mNoticeScrollViewVisibleHeight;
    private int mPaddingTop;
    private int mPaddingBottom;
    private UpgradeInfoNoticeCursor mCursor;

    public UpgradeInfoScrollView(Context context) {
        super(context);
    }

    public UpgradeInfoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mPaddingBottom = this.getPaddingBottom();
        mPaddingTop = this.getPaddingTop();
        mNoticeScrollViewTotalHeight = this.getChildAt(0).getMeasuredHeight();
        mNoticeScrollViewVisibleHeight = this.getHeight()-mPaddingTop-mPaddingBottom;
        mCursor.setCursorSize(mNoticeScrollViewVisibleHeight/ mNoticeScrollViewTotalHeight);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float currentScrollY = this.getScrollY();
        float maxMoveY = (mNoticeScrollViewTotalHeight - mNoticeScrollViewVisibleHeight);
        mCursor.setOffSet(currentScrollY,maxMoveY);
    }

    public void setCursor(UpgradeInfoNoticeCursor user_notice_cursor) {
        mCursor = user_notice_cursor;
    }

}
