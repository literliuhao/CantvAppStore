package com.can.appstore.index.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by liuhao on 2016/10/18.
 */
public class MyImageView extends ImageView {

    private int mColor;
    private int mBorder;
    private int mWidth;
    private Context mContext;

    public MyImageView(Context context) {
        super(context);
        mContext = context;
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //设置颜色
    public void setColour(int color) {
        mColor = color;
    }

    //设置边框宽度
    public void setBorder(int width) {
        mBorder = width;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画边框
        Rect rec = canvas.getClipBounds();
        Paint paint = new Paint();
        //设置边框颜色
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(mBorder);
        canvas.drawRect(rec, paint);
    }

    public void setImageURI(String s) {
        Glide.with(mContext).load(s).into(this);
    }
}