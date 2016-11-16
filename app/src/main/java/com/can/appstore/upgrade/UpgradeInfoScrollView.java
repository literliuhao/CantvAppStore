package com.can.appstore.upgrade;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * Created by syl on 2016/10/31.
 */

public class UpgradeInfoScrollView extends ScrollView{

    private Context mContext;
    private float mNoticeScrollViewTotleHeight;
    private float mNoticeScrollViewVisibleHeight;
    private int mPaddingTop;
    private int mPaddingBottom;
    private UpgradeInfoNoticeCursor mCursor;

    public UpgradeInfoScrollView(Context context) {
        super(context);
        mContext = context;
    }

    public UpgradeInfoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mNoticeScrollViewTotleHeight = this.getChildAt(0).getMeasuredHeight();
        mNoticeScrollViewVisibleHeight = this.getHeight();
        mPaddingBottom = this.getPaddingBottom();
        mPaddingTop = this.getPaddingTop();
        mCursor.setCursorSize(mNoticeScrollViewVisibleHeight/mNoticeScrollViewTotleHeight);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float currentScrollY = this.getScrollY();
        float maxMoveY = (mNoticeScrollViewTotleHeight - mNoticeScrollViewVisibleHeight +mPaddingTop + mPaddingBottom);
        mCursor.setOffSet(currentScrollY,maxMoveY);
    }

    public void setCursor(UpgradeInfoNoticeCursor user_notice_cursor) {
        mCursor = user_notice_cursor;
    }

}
