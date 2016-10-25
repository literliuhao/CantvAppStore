package cn.can.tvlib.ui.view;

import android.content.Context;
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
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;


/**
 * ================================================
 * 作    者：zhangbingyaun
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：将图片处理为圆角矩形，
 *          注意：ScaleType只支持FIT_XY、CENTER_INSIDE两种模式，其他模式无效
 *          一般情况下：FIT_XY模式用于加载资源图片，CENTER_INSIDE模式用于显示图片加载过程中的占位图logo
 * 修订历史：
 *
 * ================================================
 */
public class RoundCornerImageView extends ImageView{

    protected float cornerRadius;
    private Paint mPaint;
    private Paint mMaskPaint;
    private RectF mRect;
    private int mMaskColor;
    private boolean showMask;

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mMaskPaint = new Paint(mPaint);
        mMaskPaint.setStyle(Paint.Style.FILL);

        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 8,
                context.getResources().getDisplayMetrics());

        mRect = new RectF();
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
            if(bmp != null){
                int bmpW = bmp.getWidth();
                int bmpH = bmp.getHeight();
                int width = getWidth();
                int height = getHeight();
                ScaleType scaleType = getScaleType();
                if(scaleType == ScaleType.FIT_XY) {
                    mRect.right = width;
                    mRect.bottom = height;
                    if (bmpW != width || bmpH != height) {
                        try {
                            bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
                        } catch (OutOfMemoryError e) {
                            System.gc();
                            try {
                                bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
                            } catch (Exception e1) {
                            }
                        }
                    }
                    mPaint.setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                    canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, mPaint);
                }else if(scaleType == ScaleType.CENTER_INSIDE){
                    mRect.right = bmpW;
                    mRect.bottom = bmpH;
                    canvas.save();
                    canvas.translate((width - bmpW) / 2, (height - bmpH) / 2);
                    canvas.drawBitmap(bmp, new Rect(0, 0, bmpW, bmpH), mRect, new Paint());
                    canvas.restore();
                }

                if(showMask && mMaskColor != 0){
                    mMaskPaint.setColor(mMaskColor);
                    mRect.right = width;
                    mRect.bottom = height;
                    canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, mMaskPaint);
                }
            }
        }
    }

    public void setMaskColor(int maskColor, boolean showImmidate) {
        showMask = showImmidate;
        mMaskColor = maskColor;
        if(showMask){
            postInvalidate();
        }
    }

    public void showMask(){
        if(!showMask && mMaskColor != 0){
            showMask = true;
            postInvalidate();
        }
    }

    public void hideMask(){
        if(showMask && mMaskColor != 0){
            showMask = false;
            postInvalidate();
        }
    }
}
