package com.can.appstore.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.can.appstore.R;

/**
 * Created by JasonF on 2016/10/24.
 */

public class TextProgressBar extends ProgressBar {
    private String str;
    private Paint mPaint;
    private int textSize;
    private int textColor;
    private Rect mRect;

    public TextProgressBar(Context context) {
        this(context, null);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.str != null) {
            canvas.drawText(str,getWidth() /2 - mRect.centerX() ,getHeight()/2 - mRect.centerY() ,mPaint);
        }
    }

    // 初始化，画笔
    private void initPaint() {
        this.mRect = new Rect();
        this.mPaint = new TextPaint();
        this.mPaint.setAntiAlias(true);// 设置抗锯齿
        this.mPaint.setColor(getResources().getColor(R.color.color_80_ffffff));
        this.mPaint.setTextAlign(Paint.Align.LEFT);
        this.mPaint.setTextSize(textSize);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.mPaint.setColor(textColor);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = getResources().getDimensionPixelSize(textSize);
        this.mPaint.setTextSize(this.textSize);
        if(this.str!=null){
            mPaint.getTextBounds(str, 0,str.length(), mRect);
        }
    }

    public void setTextFakeBoldText(boolean isBold) {
        mPaint.setFakeBoldText(isBold);
        if(this.str!=null){
            mPaint.getTextBounds(str, 0,str.length(), mRect);
        }
    }

    // 设置文字内容
    public void setText(String text) {
        this.str = text;
        if(this.str!=null){
            mPaint.getTextBounds(str, 0,str.length(), mRect);
        }
        invalidate();
    }
}
