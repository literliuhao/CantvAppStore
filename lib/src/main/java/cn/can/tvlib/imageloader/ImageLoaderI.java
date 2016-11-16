package cn.can.tvlib.imageloader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by zhangbingyuan on 2016/8/31.
 */

public interface ImageLoaderI {

    void load(Context context, ImageView view, String url);

    void load(Context context, ImageView view, String url, GlideLoadTask.SuccessCallback successCallback,
              GlideLoadTask.FailCallback failCallback);
    void load(Context context, ImageView view, String url, int placeHolder, int errorHolder);

    void load(Context context, ImageView view, String url, int placeHolder, int errorHolder,
              GlideLoadTask.SuccessCallback successCallback, GlideLoadTask.FailCallback failCallback);

    void load(Context context, ImageView view, String url, int anim, int placeHolder, int errorHolder,
              GlideLoadTask.SuccessCallback successCallback, GlideLoadTask.FailCallback failCallback);

    void download(Context context, String url, GlideLoadTask.DownloadSuccCallback succCallback,
                  GlideLoadTask.DownloadFailCallback failCallback);

    GlideLoadTask.Builder buildTask(ImageView view, String url);

    GlideLoadTask.DownloadTaskBuilder buildDownloadTask(String url);

    void pauseTask(Context context);

    void pauseAllTask(Context context);

    void resumeTask(Context context);

    void resumeAllTask(Context context);

    void clearMemoryCache(Context context);

    void clearDiskCache(Context context);

    boolean downloadImgByUrl(String urlStr, String path);

}
