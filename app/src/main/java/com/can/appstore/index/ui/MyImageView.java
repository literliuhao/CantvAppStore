package com.can.appstore.index.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * Created by liuhao on 2016/10/18.
 */
public class MyImageView extends ImageView {

    private int mColor;
    private int mBorder;
    private RectF mRect;
    private float cornerRadius;
    private Path path;
    private String mIcon;
    private Context mContext;

    public MyImageView(Context context) {
        super(context);
        mContext = context;
        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 8, context.getResources().getDisplayMetrics());
        mRect = new RectF();
        path = new Path();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColour(int color) {
        mColor = color;
    }

    /**
     * 设置边框宽度
     *
     * @param width
     */
    public void setBorder(int width) {
        mBorder = width;
    }

    /**
     * 简单加入边框，后续做view扩展
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画边框
        Rect rec = canvas.getClipBounds();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        //设置边框颜色
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        paint.setStrokeWidth(mBorder);
        canvas.drawRect(rec, paint);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.clipPath(path);
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    public Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(min / 2, min / 2, min, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    public Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRect.right = w;
        mRect.bottom = h;
    }

    /**
     * 简单使用Glide加载图片，后续扩展
     *
     * @param s
     */
    public void setImageURI(String s) {
        mIcon = s;

    }


}