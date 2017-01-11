package cn.can.tvlib.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.ImageLoader;


/**
 * Created by zhangbingyuan on 2016/11/24.
 *
 * 封装Glide + RoundCornerImageView
 */

public class GlideRoundCornerImageView extends RoundCornerImageView {

    public GlideRoundCornerImageView(Context context) {
        super(context);
    }

    public GlideRoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void load(String url) {
        load(url, mBgResId, mPlaceHolderResId, mPlaceHolderResId, true);
    }

    /**
     * @param url
     * @param animLoad 图片加载时是否使用渐现动画
     */
    public void load(String url, boolean animLoad) {
        load(url, mBgResId, mPlaceHolderResId, mPlaceHolderResId, animLoad);
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
        setPlaceHolder(loadingResId);
        setImageResource(0);
        setAnimLoad(animLoad);

        GlideLoadTask.Builder builder = ImageLoader.getInstance().buildTask(this, url)
                .successCallback(new GlideLoadTask.SuccessCallback() {
                    @Override
                    public boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                             boolean isFromMemoryCache, boolean isFirstResource) {
                        setImageDrawable(resource);
                        return true;
                    }
                });

        if (errResId != 0 && errResId != loadingResId) {
            builder.failCallback(new GlideLoadTask.FailCallback() {
                @Override
                public boolean onFail(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    setPlaceHolder(errResId);
                    return false;
                }
            });
        }

        builder.start(getContext());
    }

}
