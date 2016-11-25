package cn.can.tvlib.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;

import cn.can.tvlib.R;
import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;

/**
 * Created by zhangbingyuan on 2016/11/24.
 */

public class GlideRoundCornerImageView extends FrameLayout {

    private RoundCornerImageView mBackgroundView;
    private RoundCornerImageView mImageView;

    private int mBgResId;
    private int mLoadingResId;
    private int mImageResId;

    public GlideRoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        int bgResId = 0;
        int loadingResId = 0;
        int imageResId = 0;
        int cornerRadius = RoundCornerImageView.DEF_CORNER_RADIUS;
        int maskColor = 0;
        int maskSize = 0;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GlideRoundCornerImageView);
            bgResId = typedArray.getResourceId(R.styleable.GlideRoundCornerImageView_background_res, 0);
            loadingResId = typedArray.getResourceId(R.styleable.GlideRoundCornerImageView_loading_res, 0);
            imageResId = typedArray.getResourceId(R.styleable.GlideRoundCornerImageView_src, 0);
            typedArray.recycle();

            TypedArray typedArray2 = context.obtainStyledAttributes(attrs, cn.can.tvlib.R.styleable.RoundCornerView);
            cornerRadius = typedArray2.getDimensionPixelSize(cn.can.tvlib.R.styleable.RoundCornerView_cornerSize, cornerRadius);
            maskColor = typedArray2.getColor(cn.can.tvlib.R.styleable.RoundCornerView_maskColor, 0);
            maskSize = typedArray2.getDimensionPixelSize(cn.can.tvlib.R.styleable.RoundCornerView_maskSize, 0);
            typedArray2.recycle();
        }

        mBackgroundView = new RoundCornerImageView(context);
        mBackgroundView.setLayoutParams(new LayoutParams(-1, -1));
        mBackgroundView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mBackgroundView.setCornerRadius(cornerRadius);
        addView(mBackgroundView);
        setBackgroundResource(bgResId);
        setLoadingResource(loadingResId);

        mImageView = new RoundCornerImageView(context, attrs);
        mImageView.setLayoutParams(new LayoutParams(-1, -1));
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setCornerRadius(cornerRadius);
        mImageView.setMaskColor(maskColor, maskSize, maskSize > 0);
        setImageResource(imageResId);
        addView(mImageView);
    }

    /**
     * 设置加载时占位图背景
     *
     * @param bgResId
     */
    @Override
    public void setBackgroundResource(int bgResId) {
        if (mBgResId != bgResId) {
            mBackgroundView.setBackgroundResource(bgResId);
            mBgResId = bgResId;
        }
    }

    /**
     * 设置加载时占位图背景
     *
     * @param background
     */
    @Override
    public void setBackground(Drawable background) {
        mBackgroundView.setBackground(background);
        mBgResId = 0;
    }

    /**
     * 设置加载时占位图背景
     *
     * @param color
     */
    @Override
    public void setBackgroundColor(int color) {
        mBackgroundView.setBackgroundColor(color);
        mBgResId = 0;
    }

    /**
     * 设置加载时占位图背景
     *
     * @param background
     */
    @Override
    public void setBackgroundDrawable(Drawable background) {
        mBackgroundView.setBackground(background);
        mBgResId = 0;
    }

    /**
     * 设置加载时占位图icon
     *
     * @param loadingResId
     */
    public void setLoadingResource(int loadingResId) {
        if (mLoadingResId != loadingResId) {
            mBackgroundView.setImageResource(loadingResId);
            mLoadingResId = loadingResId;
        }
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        mImageView.setScaleType(scaleType);
    }

    public void setImageResource(int resId) {
        if (mImageResId != resId) {
            mImageView.setImageResource(resId);
            mImageResId = resId;
        }
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
        mImageResId = 0;
    }

    public void setImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
        mImageResId = 0;
    }

    public void load(String url) {
        load(url, mBgResId, mLoadingResId, mLoadingResId, true);
    }

    /**
     * @param url
     * @param animLoad 图片加载时是否使用渐现动画
     */
    public void load(String url, boolean animLoad) {
        load(url, mBgResId, mLoadingResId, mLoadingResId, animLoad);
    }


    /**
     * @param url
     * @param bgResId      加载时背景图
     * @param loadingResId 加载时占位图icon
     */
    public void load(String url, int bgResId, int loadingResId) {
        load(url, bgResId, loadingResId, loadingResId, true);
    }

    /**
     * @param url
     * @param bgResId      加载时背景图
     * @param loadingResId 加载时占位图icon
     * @param animLoad     加载时是否使用渐现动画
     */
    public void load(String url, int bgResId, int loadingResId, boolean animLoad) {
        load(url, bgResId, loadingResId, loadingResId, animLoad);
    }

    /**
     * @param url
     * @param bgResId      加载时背景图
     * @param loadingResId 加载时占位图icon
     * @param errResId     加载失败时占位图icon
     * @param animLoad     加载时是否使用渐现动画
     */
    public void load(String url, int bgResId, int loadingResId, final int errResId, final boolean animLoad) {

        setBackgroundResource(bgResId);
        setLoadingResource(loadingResId);
        setImageResource(0);

        GlideLoadTask.Builder builder = ImageLoader.getInstance().buildTask(mImageView, url);

        if (animLoad) {
            builder.animResId(R.anim.fade_in);
        }

        if (errResId != 0 && errResId != loadingResId) {
            builder.failCallback(new GlideLoadTask.FailCallback() {
                @Override
                public boolean onFail(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    setLoadingResource(errResId);
                    return false;
                }
            });
        }

        builder.successCallback(new GlideLoadTask.SuccessCallback() {
            @Override
            public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (isFromMemoryCache) {
                    setImageDrawable(resource);
                    return true;
                }
                return false;
            }
        });

        builder.build().start(getContext());
    }

    // 包装RoundCornerImageView的方法
    public void setMaskColor(int maskColor, int maskSize, boolean showImmidate) {
        mImageView.setMaskColor(maskColor, maskSize, showImmidate);
    }

    public void showMask() {
        mImageView.showMask();
    }

    public void hideMask() {
        mImageView.hideMask();
    }

    public void setCornerRadius(int radius) {
        mImageView.setCornerRadius(radius);
    }

}
