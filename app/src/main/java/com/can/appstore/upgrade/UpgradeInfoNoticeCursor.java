package com.can.appstore.upgrade;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.can.appstore.R;

/**
 * Created by syl on 2016/10/31.
 */

public class UpgradeInfoNoticeCursor extends View {
    private static final String TAG = "UpgradeInfoNoticeCursor";
    private Context mContext;
    private Paint mPaint;
    private float mCursorScale;
    private float mCursorMoveY;
    private float mCursorWidth;
    private float mCursorHeight;
    private float mTotalHeight;
    private float mRoundedCorner;
    private int mCursorColor;
    private int mBarColor;

    public UpgradeInfoNoticeCursor(Context context) {
        super(context);
        mContext = context;
        initRes();
    }

    public UpgradeInfoNoticeCursor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.scrollbar);
        mCursorWidth = a.getDimension(R.styleable.scrollbar_cursorWidth,9);
        mCursorHeight = a.getDimension(R.styleable.scrollbar_cursorHeight,45);
        mRoundedCorner = a.getDimension(R.styleable.scrollbar_roundedCorner,5);
        mCursorColor = a.getColor(R.styleable.scrollbar_cursorColor,Color.parseColor("#ccffffff"));
        mBarColor = a.getColor(R.styleable.scrollbar_barColor,Color.parseColor("#44ffffff"));
        initRes();
    }

    private void initRes() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mTotalHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mCursorColor);
        RectF rect = new RectF(0, mCursorMoveY,
                mCursorWidth, mCursorMoveY + mCursorHeight);
        canvas.drawRoundRect(rect, mRoundedCorner, mRoundedCorner, mPaint);

        mPaint.setColor(mBarColor);
        RectF rect3 = new RectF(0, 0, mCursorWidth, mTotalHeight);
        canvas.drawRoundRect(rect3, mRoundedCorner, mRoundedCorner, mPaint);
    }

    public void setOffSet(float scrollY,float maxMoveY) {
        float totalH = mTotalHeight - mCursorHeight;
        float scale = totalH / maxMoveY;
        float cursorMoveY = scrollY * scale;
        mCursorMoveY = cursorMoveY;
        invalidate();
    }

    /**
     * 根据可滑动距离算出滑动条的大小
     * @param cursorScale 相对于总高度滑动条的比例
     */
    public void setCursorSize(float cursorScale){
        mCursorScale = cursorScale;
        Log.d(TAG, "setCursorSize: "+mCursorScale);
        if(mCursorScale == 0){
            return;
        }else if(mCursorScale > 1){
            mCursorScale = 1;
        }
        mCursorHeight = mCursorScale * mTotalHeight;
        invalidate();
    }
}
