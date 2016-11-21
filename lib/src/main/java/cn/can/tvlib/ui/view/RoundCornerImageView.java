package cn.can.tvlib.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
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
    protected float cornerRadius;
    private int mMaskColor;
    private int mMaskSize;

    private Paint mPaint;
    private RectF mRect;
    private Paint mMaskPaint;
    private RectF mMaskRect;
    private boolean maskParamsLegal;
    private boolean showMask;

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerView);
        cornerRadius = typedArray.getDimensionPixelSize(R.styleable.RoundCornerView_cornerSize, 12);
        mMaskColor = typedArray.getColor(R.styleable.RoundCornerView_maskColor, 0);
        mMaskSize = typedArray.getDimensionPixelSize(R.styleable.RoundCornerView_maskSize, 0);
        typedArray.recycle();

        maskParamsLegal = mMaskSize != 0 && mMaskColor != 0;
        showMask = maskParamsLegal;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mMaskPaint = new Paint(mPaint);
        mMaskPaint.setStyle(Paint.Style.FILL);
        mMaskPaint.setColor(mMaskColor);

        mRect = new RectF();
        mMaskRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (maskParamsLegal) {
            updateMaskRect(w, h);
        }
    }

    private void updateMaskRect(int w, int h) {
        int maskTop = h - mMaskSize;
        if (maskTop < 0) {
            maskTop = 0;
        }
        mMaskRect.set(0, maskTop, w, h);
    }

    private static final String TAG = "RoundCornerImageView";
    @Override
    protected void onDraw(Canvas canvas) {
        ScaleType scaleType = getScaleType();
        if (scaleType == ScaleType.CENTER_CROP || scaleType == ScaleType.CENTER || scaleType == ScaleType.FIT_END
                || scaleType == ScaleType.FIT_START || scaleType == ScaleType.FIT_CENTER || scaleType == ScaleType.MATRIX) {
            throw new IllegalStateException("Unsupported scaleType of RoundCornerImageView, please user 'fit_xy' or 'center_inside'.");
        }

        Drawable bg = getBackground();
        if (bg != null) {
            bg.setBounds(0, 0, getWidth(), getHeight());
            bg.draw(canvas);
        }
        if (getDrawable() != null) {
            Drawable drawable = getDrawable().getCurrent();
            Bitmap bmp = null;
            if(drawable instanceof BitmapDrawable){
                bmp = ((BitmapDrawable)drawable).getBitmap();
            } else if(drawable instanceof GlideBitmapDrawable){
                bmp = ((GlideBitmapDrawable)drawable).getBitmap();
            }
            if (bmp != null) {
                int viewWidth = getMeasuredWidth();
                int viewHeight = getMeasuredHeight();

                int bmpWidth = bmp.getWidth();
                int bmpHeight = bmp.getHeight();

                if (scaleType == ScaleType.FIT_XY) {
                    mRect.right = viewWidth;
                    mRect.bottom = viewHeight;

                    bmp = createFitXYBitmap(bmp);
                    if (bmp == null) {
                        return;
                    }
                    mPaint.setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                    canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, mPaint);

                } else if (scaleType == ScaleType.CENTER_INSIDE) {
                    mRect.right = bmpWidth;
                    mRect.bottom = bmpHeight;

                    canvas.save();
                    canvas.translate((viewWidth - bmpWidth) / 2, (viewHeight - bmpHeight) / 2);
                    canvas.drawBitmap(bmp, null, mRect, new Paint());
                    canvas.restore();
                }
            }
        } else {
            Log.i(TAG, "onDraw: drawable null");
        }
    }

    private Bitmap createFitXYBitmap(Bitmap bmp) {
        Bitmap finalBmp = null;

        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();

        if (viewWidth == 0 || viewHeight == 0) {
            return null;
        }

        if (bmp.getWidth() != viewWidth || bmp.getHeight() != viewHeight) {
            try {
                finalBmp = Bitmap.createScaledBitmap(bmp, viewWidth, viewHeight, true);
            } catch (OutOfMemoryError e) {
                System.gc();
                try {
                    finalBmp = Bitmap.createScaledBitmap(bmp, viewWidth, viewHeight, true);
                } catch (Exception e1) {
                }
            }
        } else {
            Log.i(TAG, "onDraw: drawable null");
        }

        if (finalBmp == null) {
            return null;
        }

        if (showMask) {
            Canvas canvas = new Canvas(finalBmp);
            canvas.drawRect(mMaskRect.left, mMaskRect.top, mMaskRect.right, mMaskRect.bottom, mMaskPaint);
        }
        return finalBmp;
    }

    public void setMaskColor(int maskColor, int maskSize, boolean showImmidate) {
        mMaskColor = maskColor;
        mMaskSize = maskSize;
        maskParamsLegal = mMaskColor > 0 && mMaskSize > 0;
        if (maskParamsLegal) {
            updateMaskRect(getWidth(), getHeight());
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
}
