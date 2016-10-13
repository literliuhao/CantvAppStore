package cn.can.tvlib.imageloader;

import android.content.Context;

public class ImageLoader {

    private static ImageLoader mInstance;
    private BaseImageLoader mLoader;
    private Context context;

    private ImageLoader(Context context) {
        mLoader = new GlideImageLoader(context);
        this.context = context.getApplicationContext();
    }

    public static ImageLoader getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(context);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * @param context Activity/Fragment 当UI不可见需要取消任务时，需要传入Activity/Fragment的context
     *                ApplicationContext 当不需要自动取消任务时，使用ApplicationContext
     * @param img
     */
    public void loadImage(Context context, ImageInfo img) {
        mLoader.loadImage(context, img);
    }

    public void clearImageAllCache() {
        mLoader.clearImageAllCache(context);
    }

    public void clearMemCache() {
        mLoader.clearMemCache(context);
    }

    public GlideImageLoader getLoader() {
        return (GlideImageLoader) mLoader;
    }
}