package cn.can.tvlib.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import cn.can.tvlib.R;


/**
 * ================================================
 * 作    者：zhangbingyaun
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：将图片处理为圆角矩形，
 * 注意：ScaleType只支持FIT_XY、CENTER_INSIDE两种模式，其他模式无效
 * 一般情况下：FIT_XY模式用于加载资源图片，CENTER_INSIDE模式用于显示图片加载过程中的占位图logo
 * 修订历史：
 * <p>
 * ================================================
 */
public class RoundCornerImageView extends ImageView {

    private static final String TAG = "RoundCornerImageView";

    public static final int DEF_CORNER_RADIUS = 12;

    protected int cornerRadius = DEF_CORNER_RADIUS;
    private int mMaskColor;
    private int mMaskSize;
    private Drawable mBgDrawable;
    private Bitmap mSrcBmp;

    private Paint mPaint;
    private RectF mSrcRect;
    private Paint mMaskPaint;
    private RectF mMaskRect;
    private Path mMaskClipPath;
    private RectF mMaskClipRect;
    private RectF mDrawRect;
    private boolean maskParamsLegal;
    private boolean showMask;
    private static boolean changeBgEnable = true;
    private int srcResId;
    private int bgResId;

    // 加载动画
    private boolean animLoad;
    private int mAlpha;
    private Runnable mAlphaRunnable;
    private int animTimeInterval = 5000;
    private int animStep = 10;

    public RoundCornerImageView(Context context){
        this(context, null);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerView);
        cornerRadius = typedArray.getDimensionPixelSize(R.styleable.RoundCornerView_cornerSize, cornerRadius);
        mMaskColor = typedArray.getColor(R.styleable.RoundCornerView_maskColor, 0);
        mMaskSize = typedArray.getDimensionPixelSize(R.styleable.RoundCornerView_maskSize, 0);
        typedArray.recycle();

        //自己控制背景的绘制
        changeBgEnable = false;
        super.setBackgroundResource(0);
        changeBgEnable = true;

        maskParamsLegal = mMaskSize > 0 && mMaskColor != 0;
        showMask = maskParamsLegal;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mSrcRect = new RectF();
        mDrawRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mSrcRect.right = getWidth();
        mSrcRect.bottom = getHeight();

        if (maskParamsLegal) {
            initMaskRect(w, h);
        }
    }

    private void initMaskRect(int w, int h) {
        int maskTop = h - mMaskSize;
        if (maskTop < 0) {
            maskTop = 0;
        }
        if(mMaskRect == null){
            mMaskRect = new RectF(0, maskTop, w, h);
        } else {
            mMaskRect.set(0, maskTop, w, h);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mAlphaRunnable != null){
            removeCallbacks(mAlphaRunnable);
            mAlpha = 255;
        }
        recycleSrcBmp();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ScaleType scaleType = getScaleType();
        if (scaleType == ScaleType.CENTER_CROP || scaleType == ScaleType.CENTER || scaleType == ScaleType.FIT_END
                || scaleType == ScaleType.FIT_START || scaleType == ScaleType.FIT_CENTER || scaleType == ScaleType.MATRIX) {
            throw new IllegalStateException("Unsupported scaleType of RoundCornerImageView, please user 'fit_xy' or 'center_inside'.");
        }

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        if(viewWidth == 0 || viewHeight == 0){
            return;
        }

        Bitmap bmp = createDrawBmp(viewWidth, viewHeight, scaleType);
        if(bmp == null){
            if(showMask){
                drawMask(viewWidth, viewHeight, canvas);
            }
            return;
        }

        mPaint.setAlpha(255);
        mPaint.setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(mSrcRect, cornerRadius, cornerRadius, mPaint);

        if(animLoad && mAlpha < 255){
            if(mAlphaRunnable == null){
                initAlphaRunnable();
            }
            post(mAlphaRunnable);
        }
    }

    private void initAlphaRunnable() {
        mAlphaRunnable = new Runnable() {
            @Override
            public void run() {
                if(animLoad){
                    if(mAlpha < 255){
                        mAlpha += animStep;
                        if(mAlpha > 255){
                            mAlpha = 255;
                        }
                        invalidate();
                        postDelayed(this, animTimeInterval);
                    }
                } else {
                    mAlpha = 255;
                    invalidate();
                }
            }
        };
    }

    private Bitmap createDrawBmp(int viewWidth, int viewHeight, ScaleType scaleType) {
        Drawable bg = mBgDrawable;
        Bitmap srcBmp = null;
        if(getDrawable() != null){
            Drawable  drawable = getDrawable().getCurrent();
            if (drawable instanceof BitmapDrawable) {
                srcBmp = ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof GlideBitmapDrawable) {
                srcBmp = ((GlideBitmapDrawable) drawable).getBitmap();
            }
        }
//        if(srcBmp == null && bg == null){
//            return null;
//        }

        Bitmap finalBmp = null;
        try {
            finalBmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_4444);
        } catch (OutOfMemoryError e) {
            System.gc();
            try {
                finalBmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_4444);
            } catch (OutOfMemoryError e1) {
            }
        }
        if(finalBmp == null){
            return null;
        }

        Canvas canvas = new Canvas(finalBmp);
        //draw bg
        if (bg != null) {
            bg.setBounds(0, 0, viewWidth, viewHeight);
            bg.draw(canvas);
        }

        //draw src
        RectF drawRect = this.mDrawRect;
        if (srcBmp != null) {
            int bmpWidth = srcBmp.getWidth();
            int bmpHeight = srcBmp.getHeight();

            if (scaleType == ScaleType.FIT_XY && (bmpWidth != viewWidth || bmpHeight != viewHeight)) {
                drawRect.set(0, 0, viewWidth, viewHeight);
            } else if (scaleType == ScaleType.CENTER_INSIDE) {
                int left = (viewWidth - bmpWidth) / 2;
                int top = (viewHeight - bmpHeight) / 2;
                drawRect.set(left, top, left + bmpWidth, top + bmpHeight);
            }
        }

        if (srcBmp != null) {
            canvas.save();
            mPaint.setAlpha(animLoad ? mAlpha : 255);
            canvas.drawBitmap(srcBmp, null, drawRect, mPaint);
            canvas.restore();
        } else {
            mAlpha = 255;
        }

        if (showMask) {
            drawMask(viewWidth, viewHeight, canvas);
        }
        return finalBmp;
    }

    private void drawMask(int viewWidth, int viewHeight, Canvas canvas) {
        if(mMaskPaint == null){
            initMaskPaint();
        }
        if(mMaskRect == null) {
            initMaskRect(viewWidth, viewHeight);
        }
        canvas.save();
        canvas.drawRect(mMaskRect, mMaskPaint);
        canvas.restore();
    }

    private void initMaskPaint() {
        mMaskPaint = new Paint(mPaint);
        mMaskPaint.setStyle(Paint.Style.FILL);
        mMaskPaint.setColor(mMaskColor);
    }

    public void setMaskColor(int maskColor, int maskSize, boolean showImmidate) {
        mMaskColor = maskColor;
        mMaskSize = maskSize;
        maskParamsLegal = mMaskSize > 0 && mMaskColor != 0;
        if (maskParamsLegal) {
            initMaskRect(getWidth(), getHeight());
        }
        showMask = maskParamsLegal && showImmidate;
        postInvalidate();
    }

    public void showMask() {
        if (!showMask && maskParamsLegal) {
            showMask = true;
            postInvalidate();
        }
    }

    public void hideMask() {
        if (showMask && maskParamsLegal) {
            showMask = false;
            postInvalidate();
        }
    }

    public void setAnimLoad(boolean animLoad) {
        this.animLoad = animLoad;
    }

    @Override
    public void setImageResource(int resId) {
        int srcResId = this.srcResId;
        if(srcResId == resId){
            return;
        }
        mAlpha = animLoad ? 0 : 255;
        recycleSrcBmp();
        this.srcResId = resId;
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mAlpha = animLoad ? 0 : 255;
        recycleSrcBmp();
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mAlpha = animLoad ? 0 : 255;
        recycleSrcBmp();
        super.setImageBitmap(bm);
    }

    @Override
    public void setBackgroundColor(int color) {
//        super.setBackgroundColor(color);
        if(!changeBgEnable){
            return;
        }
        mBgDrawable = new ColorDrawable(color);
        invalidate();
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
//        super.setBackgroundDrawable(background);
        if(!changeBgEnable){
            return;
        }
        mBgDrawable = background;
        invalidate();
    }

    @Override
    public void setBackground(Drawable background) {
//        super.setBackground(background);
        if(!changeBgEnable){
            return;
        }
        mBgDrawable = background;
        invalidate();
    }

    @Override
    public void setBackgroundResource(int resId) {
//        super.setBackgroundResource(resid);
        int bgResId = this.bgResId;
        if(bgResId == resId){
            return;
        }
        if(!changeBgEnable){
            return;
        }
        this.bgResId = resId;
        mBgDrawable = getResources().getDrawable(resId);
        invalidate();
    }

    private void recycleSrcBmp(){
        if(mSrcBmp != null){
            mSrcBmp.recycle();
            mSrcBmp = null;
        }
    }

    public void setCornerRadius(int radius) {
        cornerRadius = radius;
    }
}

