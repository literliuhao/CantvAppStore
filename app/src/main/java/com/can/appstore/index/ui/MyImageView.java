package com.can.appstore.index.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.can.appstore.R;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.imageloader.transformation.GlideRoundTransform;

/**
 * Created by liuhao on 2016/10/18.
 */
public class MyImageView extends ImageView {

    private int mColor;
    private int mBorder;
    private RectF mRect;
    private float cornerRadius;
    private Path path;
    private Context mContext;

    public MyImageView(Context context) {
        super(context);
        mContext = context;
        setScaleType(ScaleType.FIT_XY);
        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 40, context.getResources().getDisplayMetrics());
        mRect = new RectF();
        path = new Path();
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
        path.addRoundRect(mRect, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.clipPath(path);
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
        ImageLoader.getInstance().buildTask(this, s).bitmapTransformation(new GlideRoundTransform(mContext, 25)).placeholder(R.mipmap.icon_load_default).errorHolder(R.mipmap.icon_loading_fail).successCallback(new GlideLoadTask.SuccessCallback() {
            @Override
            public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                finalImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                MyImageView.this.setImageDrawable(resource);
                return true;
            }
        }).build().start(mContext);
        ;
    }
}