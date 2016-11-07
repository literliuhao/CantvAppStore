package com.can.appstore.appdetail.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
    public void setProgress(int progress) {
        super.setProgress(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mPaint.setTextSize(textSize);
        if (this.str != null) {
            this.mPaint.getTextBounds(this.str, 0, this.str.length(), rect);
            int x = (getWidth() / 2) - rect.centerX();// 让显示的字体处于中心位置;;
            int y = (getHeight() / 2) - rect.centerY();// 让显示的字体处于中心位置;;
            canvas.drawText(this.str, x, y, this.mPaint);
        }
    }

    // 初始化，画笔
    private void initPaint() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);// 设置抗锯齿
        this.mPaint.setColor(getResources().getColor(R.color.stroage_text_color));
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    // 设置文字内容
    public void setText(String text) {
        this.str = text;
        invalidate();
    }
}
