package cn.can.tvlib.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
 * ================================================<br/>
 * 作    者：zhangbingyaun<br/>
 * 版    本：2.0<br/>
 * 创建日期：2016.10.12<br/>
 * 描    述：将图片处理为圆角矩形，<br/>
 * 修订历史：1.0 ： setup<br/>
 *          2.0 ： 优化图片加载逻辑，去除对蒙版功能的支持，蒙版功能参见RoundCornerImageView2<br/>
 * <p>
 * ================================================
 */
public class RoundCornerImageView extends ImageView {

    private static final String TAG = "RoundCornerImageView";

    public static final int DEFAULT_CORNER_RADIUS = 12;

    protected int mBgResId;
    protected Drawable mBgDrawable;
    protected int mPlaceHolderResId;
    protected Bitmap mPlaceHolderBmp;
    protected int cornerRadius;

    private Paint mPaint;
    private Path mClipPath;
    private static boolean changeBgEnable = true;
    private boolean needCalDrawConfig = true;
    private RectF mImageDrawRectF;
    private int mCanvasTranslateX;
    private int mCanvasTranslateY;
    private RectF mPlaceHolderRect;
    private Matrix mImageMtx;
    private BitmapFactory.Options mImgLoadOptions;
    private Runnable mInvilidateRunnable = new Runnable() {
        @Override
        public void run() {
            postInvalidate();
        }
    };

    // 加载动画
    private boolean animLoad;
    private int mImageAlpha;
    private int animTimeInterval = 10;
    private int animStep = 10;
    private Runnable mAlphaRunnable = new Runnable() {
        @Override
        public void run() {
            if(animLoad) {
                mImageAlpha += animStep;
                if(mImageAlpha > 255){
                    mImageAlpha = 255;
                }
                invalidateSelf();
            } else {
                mImageAlpha = 255;
                invalidateSelf();
            }
        }
    };

    public RoundCornerImageView(Context context) {
        this(context, null);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int placeHolderResId;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerView);
        cornerRadius = typedArray.getDimensionPixelSize(R.styleable.RoundCornerView_cornerSize, DEFAULT_CORNER_RADIUS);
        placeHolderResId = typedArray.getResourceId(R.styleable.RoundCornerView_placeholder, 0);
        typedArray.recycle();

        mImgLoadOptions = new BitmapFactory.Options();
        mImgLoadOptions.inPremultiplied = true;
        mImgLoadOptions.inMutable = true;
        mImgLoadOptions.inPurgeable = true;
        mImgLoadOptions.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
        if (placeHolderResId != 0) {
            mPlaceHolderBmp = BitmapFactory.decodeResource(getResources(), placeHolderResId, mImgLoadOptions);
        }

        //自己控制背景的绘制
        changeBgEnable = false;
        super.setBackgroundResource(0);
        changeBgEnable = true;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFilterBitmap(true);
        mImageDrawRectF = new RectF();
        mImageMtx = new Matrix();
        mClipPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        initPlaceHolderRect(w, h);
        refreshClipPath();
    }

    private void initPlaceHolderRect(int w, int h) {
        if (mPlaceHolderBmp != null) {
            RectF bounds = mPlaceHolderRect;
            if (mPlaceHolderRect == null) {
                bounds = new RectF();
            }
            int drawableW = mPlaceHolderBmp.getWidth();
            int drawableH = mPlaceHolderBmp.getHeight();
            bounds.left = (w - drawableW) >> 1;
            bounds.top = (h - drawableH) >> 1;
            bounds.right = bounds.left + drawableW;
            bounds.bottom = bounds.top + drawableH;
            mPlaceHolderRect = bounds;
        }
    }

    private void refreshClipPath() {
        mClipPath.reset();
        mClipPath.addRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), cornerRadius, cornerRadius, Path.Direction.CCW);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mAlphaRunnable != null) {
            removeCallbacks(mAlphaRunnable);
            mImageAlpha = 255;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ScaleType scaleType = getScaleType();
        if (scaleType == ScaleType.MATRIX) {
            throw new IllegalStateException("Unsupported scaleType of RoundCornerImageView, please user 'fit_xy' or 'center_inside'.");
        }

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        if (viewWidth == 0 || viewHeight == 0) {
            return;
        }

        Drawable bgDrawable = this.mBgDrawable;
        if (bgDrawable != null) {
            bgDrawable.setBounds(0, 0, viewWidth, viewHeight);
            canvas.save();
            canvas.clipPath(mClipPath);
            bgDrawable.draw(canvas);
            canvas.restore();
        }

        Bitmap placeHolderBmp = this.mPlaceHolderBmp;
        if (placeHolderBmp != null) {
            mPaint.setAlpha(255);
            canvas.drawBitmap(placeHolderBmp, null, mPlaceHolderRect, mPaint);
        }

        Drawable imgDrawable = getDrawable();
        if(imgDrawable == null) {
            return;
        }

        Bitmap imgBmp = null;
        Drawable drawable = imgDrawable.getCurrent();
        if (drawable instanceof BitmapDrawable) {
            imgBmp = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof GlideBitmapDrawable) {
            imgBmp = ((GlideBitmapDrawable) drawable).getBitmap();
        } else if(drawable instanceof ColorDrawable) {
            canvas.clipPath(mClipPath);
            drawable.setBounds(0, 0, viewWidth, viewHeight);
            drawable.draw(canvas);
            return;
        }

        if(imgBmp != null){
            if(needCalDrawConfig){
                needCalDrawConfig = false;
                calculateDrawConfig(imgBmp, viewWidth, viewHeight, scaleType);
            }

            mPaint.setAlpha(animLoad ? mImageAlpha : 255);
            BitmapShader bitmapShader = new BitmapShader(imgBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            bitmapShader.setLocalMatrix(mImageMtx);
            mPaint.setShader(bitmapShader);

            canvas.translate(mCanvasTranslateX, mCanvasTranslateY);
//            canvas.drawRect(mImageDrawRectF, mPaint);
            canvas.drawRoundRect(mImageDrawRectF, cornerRadius, cornerRadius, mPaint);
        }

        if (mImageAlpha < 255) {
            post(mAlphaRunnable);
        }
    }

    private void calculateDrawConfig(Bitmap imgBmp, int viewWidth, int viewHeight, ScaleType scaleType) {
        int bmpW = imgBmp.getWidth();
        int bmpH = imgBmp.getHeight();

        float scaleX = 1f;
        float scaleY = 1f;
        int left = 0;
        int top = 0;
        int right = viewWidth;
        int bottom = viewHeight;
        mCanvasTranslateX = 0;
        mCanvasTranslateY = 0;

        if(scaleType == ScaleType.FIT_XY){
            scaleX = viewWidth * 1f / bmpW;
            scaleY = viewHeight * 1f / bmpH;

        } else if(scaleType == ScaleType.CENTER_INSIDE){
            if(bmpW > viewWidth && bmpH > viewHeight){
                float ratioW = viewWidth * 1f / bmpW;
                float ratioH = viewHeight * 1f / bmpH;
                if(ratioW < ratioH){
                    scaleX = ratioW;
                    scaleY = ratioW;
                } else {
                    scaleX = ratioH;
                    scaleY = ratioH;
                }
            } else if (bmpW > viewWidth){
                scaleX = viewWidth * 1f / bmpW;
                scaleY = scaleX;
            } else if (bmpH > viewHeight){
                scaleY = viewHeight * 1f / bmpH;
                scaleX = scaleY;
            }
            right = (int) (bmpW * scaleX + .5f);
            bottom = (int) (bmpH * scaleY + .5f);
            mCanvasTranslateX = (viewWidth - right) >> 1;
            mCanvasTranslateY = (viewHeight - bottom) >> 1;

        } else if(scaleType == ScaleType.CENTER_CROP) {
            if(bmpW < viewWidth && bmpH < viewHeight) {
                float ratioW = viewWidth * 1f / bmpW;
                float ratioH = viewHeight * 1f / bmpH;
                if(ratioW < ratioH){
                    scaleX = ratioH;
                    scaleY = ratioH;
                    right = (int) (bmpW * scaleX + .5f);
                    left = (right - viewWidth) >> 1;
                    right -= left;
                    mCanvasTranslateX = -left;
                } else {
                    scaleX = ratioW;
                    scaleY = ratioW;
                    bottom = (int) (bmpH * scaleY + .5f);
                    top = (bottom - viewHeight) >> 1;
                    bottom -= top;
                    mCanvasTranslateY = -top;
                }
            } else if(bmpW > viewWidth && bmpH > viewHeight){
                float ratioW = viewWidth * 1f / bmpW;
                float ratioH = viewHeight * 1f / bmpH;
                if(ratioW < ratioH){
                    scaleX = ratioH;
                    scaleY = ratioH;
                    right = (int) (bmpW * scaleX + .5f);
                    left = (right - viewWidth) >> 1;
                    right -= left;
                    mCanvasTranslateX = -left;
                } else {
                    scaleX = ratioW;
                    scaleY = ratioW;
                    bottom = (int) (bmpH * scaleY + .5f);
                    top = (bottom - viewHeight) >> 1;
                    bottom -= top;
                    mCanvasTranslateY = -top;
                }
            } else if(bmpW < viewWidth){
                scaleX = viewWidth * 1f / bmpW;
                scaleY = scaleX;
                bottom = (int) (bmpH * scaleY + .5f);
                top = (bottom - viewHeight) >> 1;
                bottom -= top;
                mCanvasTranslateY = -top;
            } else if(bmpH < viewHeight){
                scaleY = viewHeight * 1f / bmpH;
                scaleX = scaleY;
                right = (int) (bmpW * scaleX + .5f);
                left = (right - viewWidth) >> 1;
                right -= left;
                mCanvasTranslateX = -left;
            } else if(bmpW > viewWidth){
                left = (bmpW - viewWidth) >> 1;
                right = bmpW - left;
                mCanvasTranslateX = -left;
            } else if(bmpH > viewHeight){
                top = (bmpH - viewHeight) >> 1;
                bottom = bmpH - top;
                mCanvasTranslateY = -top;
            }
        } else if(scaleType == ScaleType.CENTER){
            if(bmpW <= viewWidth){
                mCanvasTranslateX = (viewWidth - bmpW) >> 1;
                left = 0;
                right = bmpW;
            } else {
                int offset = (bmpW - viewWidth) >> 1;
                mCanvasTranslateX = -offset;
                left = offset;
                right = bmpW - offset;
            }
            if(bmpH <= viewHeight){
                mCanvasTranslateY = (viewHeight - bmpH) >> 1;
                top = 0;
                bottom = bmpH;
            } else {
                int offset = (bmpH - viewHeight) >> 1;
                mCanvasTranslateY = -offset;
                top = offset;
                bottom = bmpH - offset;
            }
        } else if(scaleType == ScaleType.FIT_CENTER){
            left = 0;
            right = viewWidth;
            scaleX = viewWidth * 1f / bmpW;
            scaleY = scaleX;
            int scaledBmpH = (int) (bmpH * scaleY + .5f);
            if(scaledBmpH <= viewHeight){
                mCanvasTranslateY = (viewHeight - scaledBmpH) >> 1;
                bottom = scaledBmpH;
            } else {
                int offset = (scaledBmpH - viewHeight) >> 1;
                mCanvasTranslateY = -offset;
                top = offset;
                bottom = scaledBmpH - offset;
            }
        } else if (scaleType == ScaleType.FIT_START) {
            left = 0;
            right = viewWidth;
            scaleX = viewWidth * 1f / bmpW;
            scaleY = scaleX;
            int scaledBmpH = (int) (bmpH * scaleY + .5f);
            bottom = scaledBmpH < viewHeight ? scaledBmpH : viewHeight;
        } else if (scaleType == ScaleType.FIT_END) {
            left = 0;
            right = viewWidth;
            scaleX = viewWidth * 1f / bmpW;
            scaleY = scaleX;
            int scaledBmpH = (int) (bmpH * scaleY + .5f);
            if(scaledBmpH < viewHeight){
                mCanvasTranslateY = viewHeight - scaledBmpH;
                bottom = viewHeight - mCanvasTranslateY;
            } else if(scaledBmpH > viewHeight) {
                mCanvasTranslateY = viewHeight - scaledBmpH;
                top = -mCanvasTranslateY;
                bottom = scaledBmpH;
            }
        }
        RectF imgDrawRect = mImageDrawRectF;
        imgDrawRect.set(left, top, right, bottom);

        Matrix imgMtx = this.mImageMtx;
        imgMtx.reset();
        imgMtx.postScale(scaleX, scaleY);
    }

    public void setAnimLoad(boolean animLoad) {
        this.animLoad = animLoad;
    }

    public void setCornerRadius(int radius) {
        cornerRadius = radius;
        refreshClipPath();
        invalidateSelf();
    }

    public void setPlaceHolder(int resId){
        if(mPlaceHolderResId == resId){
            return;
        }
        mPlaceHolderResId = resId;
        mPlaceHolderBmp = BitmapFactory.decodeResource(getResources(), resId, mImgLoadOptions);
        initPlaceHolderRect(getMeasuredWidth(), getMeasuredHeight());
        invalidateSelf();
    }

    // ************************ setImage  ******************************
    @Override
    public void setImageResource(int resId) {
        mImageAlpha = animLoad ? 0 : 255;
        needCalDrawConfig = true;
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mImageAlpha = animLoad ? 0 : 255;
        needCalDrawConfig = true;
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mImageAlpha = animLoad ? 0 : 255;
        needCalDrawConfig = true;
        super.setImageBitmap(bm);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        needCalDrawConfig = true;
        super.setScaleType(scaleType);
    }

    // ************************ set background ******************************
    @Override
    public void setBackgroundColor(int color) {
//        super.setBackgroundColor(color);
        if (!changeBgEnable) {
            return;
        }
        if(mBgResId == color){
            return;
        }
        mBgResId = color;
        mBgDrawable = new ColorDrawable(color);
        invalidateSelf();
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
//        super.setBackgroundDrawable(background);
        if (!changeBgEnable) {
            return;
        }
        if(mBgDrawable == background){
            return;
        }
        mBgResId = 0;
        mBgDrawable = background;
        invalidateSelf();
    }

    @Override
    public void setBackground(Drawable background) {
//        super.setBackground(background);
        if (!changeBgEnable) {
            return;
        }
        if(mBgDrawable == background){
            return;
        }
        mBgResId = 0;
        mBgDrawable = background;
        invalidateSelf();
    }

    @Override
    public void setBackgroundResource(int resId) {
//        super.setBackgroundResource(resid);
        if (!changeBgEnable) {
            return;
        }
        if(mBgResId == resId){
            return;
        }
        mBgResId = resId;
        mBgDrawable = getResources().getDrawable(resId);
        invalidateSelf();
    }

    private void invalidateSelf() {
        invalidateSelf(10);
    }

    private void invalidateSelf(int delay) {
        removeCallbacks(mInvilidateRunnable);
        postDelayed(mInvilidateRunnable, animTimeInterval);
    }

}

