package com.can.appstore.search;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.can.appstore.R;

/**
 * Created by yibh on 2016/10/12 10:47 .
 * 带外框的自定义TextView
 */
public class YAroundTextView extends TextView {

    private Paint mPaint;
    private float mWidth;
    private float mHeight;
    private int mAroundColor = Color.GRAY;

    public YAroundTextView(Context context) {
        this(context, null);
    }

    public YAroundTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YAroundTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1f);

        setFocusable(true);
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.search_key_item_selector);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        int currentTextColor = getCurrentTextColor();
//        mPaint.setColor(currentTextColor);
        mPaint.setColor(mAroundColor);
        canvas.translate(mWidth / 2, mHeight / 2);   //将原点移动到文字中点
        //得到当前文字的长度,根据长度来设置外边框的长
        CharSequence text = getText();
        int length = text.length();
        mWidth = mHeight * length;
        if (this.getLayoutParams().width != (int) mWidth) {
            this.getLayoutParams().width = (int) mWidth;
            requestLayout();
        }
        RectF rectF = new RectF(-mWidth / 2, -mHeight / 2, mWidth / 2, mHeight / 2);
        canvas.drawRect(rectF, mPaint);
    }

    /**
     * 设置外框颜色
     *
     * @param color
     */
    public void setAroundColor(int color) {
        mAroundColor = color;
        invalidate();
    }
}
