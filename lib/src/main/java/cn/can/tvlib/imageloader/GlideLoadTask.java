package cn.can.tvlib.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import cn.can.tvlib.imageloader.transformation.GlideCircleTransform;
import cn.can.tvlib.imageloader.transformation.GlideRoundTransform;

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：Glide加载任务配置
 * 修订历史：
 * ================================================
 */

/**
 * 图片加载任务,用于配置各种图片加载参数<br/>
 * <p>
 * Created by zhangbingyuan on 2016/8/29.<p/>
 */

public class GlideLoadTask {
    // 图片加载任务优先级
    public static final int PRIORITY_IMMEDIATE = 0;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_NORMAL = 2;
    public static final int PRIORITY_LOW = 3;
    // 图片下载完成后缓存策略
    public static final int CACHE_NONE_IN_DISK = 0; //不缓存
    public static final int CACHE_ALL_IN_DISK = 1; //缓存原图和处理后图片到本地
    public static final int CACHE_SRC_IN_DISK = 2; //仅缓存原图到本地
    public static final int CACHE_RESULT_IN_DISK = 3; //仅缓存处理后图片到本地
    // 图片下载成功后反馈结果类型
    public static final int RESULT_TYPE_NORMAL = 0;
    public static final int RESULT_TYPE_BITMAP = 1;
    public static final int RESULT_TYPE_GIF = 2;
    // 默认提供的图片加载后的简单处理效果
    public static final int SHAPE_RECTANGLE = 0; // default
    public static final int SHAPE_CIRCLE = 1; // 圆形
    public static final int SHAPE_ROUND_CORNER = 2; // 圆角矩形

    public interface SuccessCallback {
        boolean onSuccess(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource);
    }

    public interface FailCallback {
        boolean onFail(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource);
    }

    public interface DownloadSuccCallback {
        void onSuccess(String url, File file);
    }

    public interface DownloadFailCallback {
        void onFail(String url, Exception e);
    }

    private ImageView imgView;
    private String url;
    private int width;
    private int height;
    private int errorPlaceholder;
    private int placeholder;
    private float thumbnail;
    private boolean cacheInMemory = true;
    private int priority = PRIORITY_NORMAL;
    private int diskCacheStagety = CACHE_ALL_IN_DISK;
    private int resultType;
    private int animResId;
    private boolean crossFade;
    private int shape;
    private ViewPropertyAnimation.Animator animator;
    private BitmapTransformation transformation;
    private SuccessCallback loadSuccessCallback;
    private FailCallback loadFailCallback;

    private boolean onlyDownload;
    private int timeout;
    private TimeUnit timeUnit;
    private DownloadSuccCallback downloadSuccCallback;
    private DownloadFailCallback downloadFailCallback;

    public static class Builder {

        private ImageView imgView;
        private String url;
        private int width;
        private int height;
        private int errorPlaceholder;
        private int placeholder;
        private float thumbnail;
        private boolean cacheInMemory;
        private int priority = PRIORITY_NORMAL;
        private int diskCacheStagety = CACHE_ALL_IN_DISK;
        private int resultType;
        private int animResId;
        private boolean crossFade = true;
        private int shape;
        private ViewPropertyAnimation.Animator animator;
        private BitmapTransformation transformation;
        private SuccessCallback loadSuccessCallback;
        private FailCallback loadFailCallback;

        public void start(Context context){
            build().start(context);
        }

        public GlideLoadTask build() {
            GlideLoadTask task = new GlideLoadTask();
            task.imgView = this.imgView;
            task.url = this.url;
            task.width = this.width;
            task.height = this.height;
            task.errorPlaceholder = this.errorPlaceholder;
            task.placeholder = this.placeholder;
            task.thumbnail = this.thumbnail;
            task.cacheInMemory = this.cacheInMemory;
            task.priority = this.priority;
            task.diskCacheStagety = this.diskCacheStagety;
            task.resultType = this.resultType;
            task.crossFade = this.crossFade;
            task.animResId = this.animResId;
            task.shape = this.shape;
            task.animator = this.animator;
            task.transformation = this.transformation;
            task.loadSuccessCallback = this.loadSuccessCallback;
            task.loadFailCallback = this.loadFailCallback;
            return task;
        }

        public Builder view(ImageView imgView) {
            this.imgView = imgView;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder errorHolder(int errorPlaceholder) {
            this.errorPlaceholder = errorPlaceholder;
            return this;
        }

        public Builder placeholder(int placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder thumbnail(float thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder cacheInMemory(boolean cacheInMemory) {
            this.cacheInMemory = cacheInMemory;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder diskCacheStagety(int diskCacheStagety) {
            this.diskCacheStagety = diskCacheStagety;
            return this;
        }

        public Builder resultType(int resultType) {
            this.resultType = resultType;
            return this;
        }

        public Builder animResId(int animResId) {
            this.animResId = animResId;
            return this;
        }

        public Builder crossFade(boolean crossFade) {
            this.crossFade = crossFade;
            return this;
        }

        public Builder animator(ViewPropertyAnimation.Animator animator) {
            this.animator = animator;
            return this;
        }

        public Builder shape(int shape) {
            this.shape = shape;
            return this;
        }

        public Builder bitmapTransformation(BitmapTransformation transformation) {
            this.transformation = transformation;
            return this;
        }

        public Builder successCallback(SuccessCallback successCallback) {
            this.loadSuccessCallback = successCallback;
            return this;
        }

        public Builder failCallback(FailCallback failCallback) {
            this.loadFailCallback = failCallback;
            return this;
        }
    }

    public static class DownloadTaskBuilder {

        private String url;
        private int width;
        private int height;
        private int priority;
        private DownloadSuccCallback successCallback;
        private DownloadFailCallback failCallback;
        private int timeout;
        private TimeUnit timeUnit;

        public GlideLoadTask build() {
            GlideLoadTask task = new GlideLoadTask();
            task.onlyDownload = true;
            task.url = this.url;
            task.width = this.width;
            task.height = this.height;
            task.priority = this.priority;
            task.downloadSuccCallback = this.successCallback;
            task.downloadFailCallback = this.failCallback;
            task.timeout = this.timeout;
            task.timeUnit = this.timeUnit;
            return task;
        }

        public DownloadTaskBuilder url(String url) {
            this.url = url;
            return this;
        }

        public DownloadTaskBuilder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public DownloadTaskBuilder successCallback(DownloadSuccCallback successCallback) {
            this.successCallback = successCallback;
            return this;
        }

        public DownloadTaskBuilder failCallback(DownloadFailCallback failCallback) {
            this.failCallback = failCallback;
            return this;
        }

        public DownloadTaskBuilder timeout(int timeout, TimeUnit timeUnit) {
            this.timeout = timeout;
            this.timeUnit = timeUnit;
            return this;
        }
    }

    public void start(Context context) {
        DrawableTypeRequest<String> load = Glide.with(context).load(url);
        if (onlyDownload) {
            try {
                File file;
                if (width == 0 && height == 0) {
                    file = load.downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                } else {
                    file = load.downloadOnly(width, height).get();
                }
                if (file != null && file.exists() && file.length() > 0 && downloadSuccCallback != null) {
                    downloadSuccCallback.onSuccess(url, file);
                } else {
                    downloadFailCallback.onFail(url, new NullPointerException("The download file is empty."));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                downloadFailCallback.onFail(url, e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                downloadFailCallback.onFail(url, e);
            } catch (CancellationException e) {
                e.printStackTrace();
                downloadFailCallback.onFail(url, e);
            }
        } else {
            if (errorPlaceholder > 0) {
                load.error(errorPlaceholder);
            }
            if (placeholder > 0) {
                load.placeholder(placeholder);
            }
            if (thumbnail != 0) {
                load.thumbnail(thumbnail);
            }
            if (!cacheInMemory) {
                load.skipMemoryCache(!cacheInMemory);
            }
            if (priority == PRIORITY_HIGH) {
                load.priority(Priority.HIGH);
            } else if (priority == PRIORITY_IMMEDIATE) {
                load.priority(Priority.IMMEDIATE);
            } else if (priority == PRIORITY_LOW) {
                load.priority(Priority.LOW);
            }
            if (diskCacheStagety == CACHE_ALL_IN_DISK) {
                load.diskCacheStrategy(DiskCacheStrategy.ALL);
            } else if (diskCacheStagety == CACHE_NONE_IN_DISK) {
                load.diskCacheStrategy(DiskCacheStrategy.NONE);
            } else if (diskCacheStagety == CACHE_SRC_IN_DISK) {
                load.diskCacheStrategy(DiskCacheStrategy.SOURCE);
            } else if (diskCacheStagety == CACHE_RESULT_IN_DISK) {
                load.diskCacheStrategy(DiskCacheStrategy.RESULT);
            }
            if (resultType == RESULT_TYPE_GIF) {
                load.asGif();
            } else if (resultType == RESULT_TYPE_BITMAP) {
                load.asBitmap();
            }
            if (animator != null) {
                load.animate(animator);
            } else if (animResId > 0) {
                load.animate(animResId);
            } else if (crossFade) {
                load.crossFade();
            }
            if (transformation != null) {
                load.bitmapTransform(transformation);
            } else if (shape == SHAPE_CIRCLE) {
                load.bitmapTransform(new GlideCircleTransform(context));
            } else if (shape == SHAPE_ROUND_CORNER) {
                load.bitmapTransform(new GlideRoundTransform(context));
            }
            if (loadSuccessCallback != null || loadFailCallback != null) {
                load.listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (loadFailCallback != null) {
                            return loadFailCallback.onFail(e, model, target, isFirstResource);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        if (loadSuccessCallback != null) {
                            return loadSuccessCallback.onSuccess(resource, model, target, isFromMemoryCache, isFirstResource);
                        }
                        return false;
                    }
                });
            }
            if (width > 0 && height > 0) {
                load.override(width, height);
            }
            load.into(imgView);
        }
    }

}
