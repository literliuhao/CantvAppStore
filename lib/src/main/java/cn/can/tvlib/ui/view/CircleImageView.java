package cn.can.tvlib.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.lang.ref.SoftReference;

import cn.can.tvlib.R;

import static android.R.attr.height;
import static android.R.attr.width;

/**
 * Created by zhangbingyuan on 2016/10/19.
 */

public class CircleImageView extends ImageView {

    private Paint mPaint;
    private RectF mRect;
    private Paint mRingPaint;
    private int mRingWidth;
    private int mRingColor;//圆环颜色
    private SoftReference<Bitmap> mBmp;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.CircleImageView);
        mRingWidth = typedArray.getDimensionPixelOffset(R.styleable.CircleImageView_ringWidth, 0) << 1;
        mRingColor = typedArray.getColor(R.styleable.CircleImageView_ringColor, 0);
        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mRingPaint = new Paint(mPaint);
        mRingPaint.setStyle(Paint.Style.FILL);
        mRingPaint.setStrokeWidth(mRingWidth);
        mRingPaint.setColor(mRingColor);

        mRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        switch (widthMode){
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                width = measuredWidth;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = 100;
                break;
        }

        int height = 0;
        switch (heightMode){
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                height = measuredHeight;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = 100;
                break;
        }

        int reqSize = Math.min(width, height);
        
        setMeasuredDimension(reqSize, reqSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRect.right = w;
        mRect.bottom = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable bg = getBackground();
        if(bg != null){
            bg.setBounds(0, 0 ,getWidth(), getHeight());
            bg.draw(canvas);
        }

        if(getDrawable() != null){
            Drawable drawable = getDrawable();
            Bitmap bmp = null;
            if(drawable instanceof BitmapDrawable){
                bmp = ((BitmapDrawable)drawable).getBitmap();
            }else if(drawable instanceof GlideBitmapDrawable){
                bmp = ((GlideBitmapDrawable)drawable).getBitmap();
            }
            if(bmp == null){
                return;
            }
            int bmpW = bmp.getWidth();
            int bmpH = bmp.getHeight();
            
            ScaleType scaleType = getScaleType();
            if(scaleType == ScaleType.FIT_XY){
                
                int width = getWidth();
                int height = getHeight();

                Bitmap currBmp = null;
                if(mBmp != null){
                    currBmp = mBmp.get();
                }
                if(bmp != currBmp) {
                    if(mBmp != null){
                        mBmp.clear();
                        mBmp = null;
                    }

                    if (bmpW != width || bmpH != height) {
                        
                        try {
                            bmp = Bitmap.createScaledBitmap(bmp, width + 1, height + 1, true);
                        } catch (OutOfMemoryError e) {
                            System.gc();
                            try {
                                bmp = Bitmap.createScaledBitmap(bmp, width + 1, height + 1, true);
                            } catch (Exception e1) {
                            }
                        }
                    }
                    
                    mBmp = new SoftReference<>(bmp);
                    mPaint.setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                }
                mRect.right = width;
                mRect.bottom = height;
                float centerX = mRect.centerX();
                float centerY = mRect.centerY();
                float radius = width >> 1;
                
                canvas.drawCircle(centerX, centerY, radius, mPaint);
                canvas.drawCircle(centerX, centerY, radius, mRingPaint);

            } else if(scaleType == ScaleType.CENTER_INSIDE){
                
                mRect.right = bmpW;
                mRect.bottom = bmpH;
                canvas.save();
                canvas.translate((width - bmpW) >> 1, (height - bmpH) >> 1);
                canvas.drawBitmap(bmp, new Rect(0, 0, bmpW, bmpH), mRect, new Paint());
                canvas.restore();
            }
        }
    }
}
