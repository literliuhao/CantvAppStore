package com.can.appstore.upgrade.widgets;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.can.appstore.R;

/**
 * Created by 4 on 2016/11/1.
 */

public class UpgradeProgressBar extends View {

    private Paint mPaint;
    private int mProgress;
    private int mMax;
    private float mBarWidth;
    private float mBarHeight;
    private float mBarStroke;
    private float mBarCorner;
    private int mBarColor;
    private int mProgressColor;

    public UpgradeProgressBar(Context context) {
        super(context);
    }

    public UpgradeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        TypedArray typedArray   = context.obtainStyledAttributes(attrs, R.styleable.upgradeprogressbar);
        mBarWidth = typedArray.getDimension(R.styleable.upgradeprogressbar_barwidth,0);
        mBarHeight = typedArray.getDimension(R.styleable.upgradeprogressbar_barheight,0);
        mBarStroke = typedArray.getDimension(R.styleable.upgradeprogressbar_barstroke,0);
        mBarCorner = typedArray.getDimension(R.styleable.upgradeprogressbar_barcorner,0);
        mBarColor = typedArray.getColor(R.styleable.upgradeprogressbar_barcolor,0);
        mProgressColor = typedArray.getColor(R.styleable.upgradeprogressbar_progresscolor,0);
    }

    public UpgradeProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec+(int)mBarStroke);
        }else{
           // int reqHeight = (int) (mProgressBarHeight + mTimeLabelHeight + mTimeLabelMargin);
            int reqHeight = (int) (mBarHeight+mBarStroke);
            if (heightMode == MeasureSpec.AT_MOST) {
                reqHeight = resolveSize(reqHeight, heightMeasureSpec);
            }
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(reqHeight, MeasureSpec.EXACTLY));

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path mBarClipPath = new Path();

        RectF barClipRect = new RectF();
        barClipRect.left = 0;
        barClipRect.top= 0;
        barClipRect.right = mBarWidth+mBarStroke;
        barClipRect.bottom = mBarHeight+mBarStroke;
        mBarClipPath.addRoundRect(barClipRect, mBarCorner, mBarCorner, Path.Direction.CW);

        RectF mBgRect  = new RectF(mBarStroke/2,mBarStroke/2,mBarStroke/2+mBarWidth,mBarStroke/2+mBarHeight);

        mPaint.setColor(mBarColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBarStroke);
        canvas.drawRoundRect(mBgRect, mBarCorner, mBarCorner, mPaint);

        canvas.save();
        canvas.clipPath(mBarClipPath);

        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,mProgress*(mBarWidth+mBarStroke)/mMax,mBarHeight+mBarStroke,mPaint);
        canvas.restore();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    public void setProgress(int progress){
        mProgress = progress;
        invalidate();
    }
    public void setMax(int max){
        mMax = max;
        invalidate();
    }
}
