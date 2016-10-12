package cn.can.tvlib.imageloader;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.math.BigDecimal;

/**
 *
 */
public class GlideImageLoader implements BaseImageLoader {

    // App.getContext().getCacheDir() + "/image_cache"
    private String ImageExternalCatchDir = Environment.getDownloadCacheDirectory() + "/image_cache";

    public GlideImageLoader(Context context) {
        ImageExternalCatchDir = context.getExternalCacheDir() + "/image_cache";
        // initGlide();
    }

    // private void initGlide() {
    // Glide.get(this).register(GlideUrl.class, InputStream.class, new
    // VolleyUrlLoader.Factory(yourRequestQueue));

    // Glide.get(this).register(GlideUrl.class, InputStream.class, new
    // OkHttpUrlLoader.Factory(new OkHttpClient()));
    // }

    @Override
    public void loadImage(Context ctx, ImageInfo img) {
        loadByNet(ctx, img);
        // if (NetUtils._checkNet(ctx)) {
        // loadByNet(ctx, img);
        // } else {
        // loadByCache(ctx, img);
        // }
    }

    @Override
    public void clearImageAllCache(final Context context) {
        clearImageDiskCache(context);
        clearImageMemoryCache(context);
        deleteFolderFile(ImageExternalCatchDir, true);
    }

    @Override
    public void clearMemCache(final Context context) {
        clearImageMemoryCache(context);
    }

    // /**
    // * load cache image with Glide
    // */
    // private void loadByCache(Context ctx, ImageInfo img) {
    // Glide.with(ctx).using(new StreamModelLoader<String>() {
    // @Override
    // public DataFetcher<InputStream> getResourceFetcher(final String model, int i, int i1) {
    // return new DataFetcher<InputStream>() {
    // @Override
    // public InputStream loadData(Priority priority) throws Exception {
    // throw new IOException();
    // }
    //
    // @Override
    // public void cleanup() {
    //
    // }
    //
    // @Override
    // public String getId() {
    // return model;
    // }
    //
    // @Override
    // public void cancel() {
    //
    // }
    // };
    // }
    // }).load(img.getUrl()).placeholder(img.getPlaceHolder()).diskCacheStrategy(DiskCacheStrategy.ALL)
    // .into(img.getImgView());
    // }

    /**
     * 加载图片
     *
     * @param context
     * @param img     图片信息
     */
    public void loadByNet(Context context, final ImageInfo img) {
        if (img.isGif) {
            Glide.with(context).load(img.getUrl()).asGif().diskCacheStrategy(img.getDiskCacheStrategy())
                    .into(img.getImgView());
        } else {
            if (img.getLoadFinishListener() == null) {
                Glide.with(context).load(img.getUrl()).dontAnimate().skipMemoryCache(img.isSkipMemoryCache)
                        .diskCacheStrategy(img.getDiskCacheStrategy()).error(img.getErrorHolder())
                        .placeholder(img.getPlaceHolder()).priority(img.getPriority())
                        .into(img.getImgView());
            } else {
                Glide.with(context).load(img.getUrl()).dontAnimate().skipMemoryCache(img.isSkipMemoryCache)
                        .diskCacheStrategy(img.getDiskCacheStrategy()).error(img.getErrorHolder())
                        .placeholder(img.getPlaceHolder()).priority(img.getPriority()).listener(new RequestListener<String, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception arg0, String arg1, Target<GlideDrawable> arg2, boolean arg3) {
                        img.getImgView().setBackgroundResource(img.getErrorHolder());
                        img.getLoadFinishListener().onFail();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable arg0, String arg1, Target<GlideDrawable> arg2,
                                                   boolean arg3, boolean arg4) {
                        if (arg0 != null) {
                            img.getImgView().setImageDrawable(arg0);
                        } else {
                            img.getImgView().setImageDrawable(null);
                            img.getImgView().setBackgroundResource(img.getPlaceHolder());
                        }
                        img.getLoadFinishListener().onSuccess();
                        return false;
                    }
                })
                        .into(img.getImgView());
            }
        }
    }

    public void loadImgByCircle(Context context, ImageInfo img) {
        Glide.with(context).load(img.getUrl()).error(img.getErrorHolder()).transform(new GlideCircleTransform(context))
                .placeholder(img.getPlaceHolder()).into(img.getImgView());
    }

    public void loadImgByRound(Context context, ImageInfo img) {
        Glide.with(context).load(img.getUrl()).dontAnimate().skipMemoryCache(img.isSkipMemoryCache)
                .diskCacheStrategy(img.getDiskCacheStrategy()).error(img.getErrorHolder())
                .transform(new CenterCrop(context), new GlideRoundTransform(context, img.getCornerSizeDp()))
                .placeholder(img.getPlaceHolder()).priority(img.getPriority()).into(img.getImgView());
    }

    public void loadImgByFitCenter(Context context, ImageInfo img) {
        Glide.with(context).load(img.getUrl()).error(img.getErrorHolder()).placeholder(img.getPlaceHolder())
                .into(img.getImgView());
    }

    public void loadImgByThumbnail(Context context, ImageInfo img) {
        Glide.with(context).load(img.getUrl()).dontAnimate().skipMemoryCache(img.isSkipMemoryCache)
                .diskCacheStrategy(img.getDiskCacheStrategy()).override(img.getWidth(), img.getHeight())
                .error(img.getErrorHolder()).placeholder(img.getPlaceHolder()).priority(img.getPriority())
                .thumbnail(img.getThumbnail()).into(img.getImgView());
    }

    // public void loadImgForByte(Context context, ImageInfo img) {
    // Glide.with(context).load(img.getUrl()).asBitmap().toBytes().centerCrop()
    // .into(new SimpleTarget<byte[]>(img.getWidth(), img.getHeight()) {
    // @Override
    // public void onResourceReady(byte[] data, GlideAnimation anim) {
    // }
    // });
    // }

    // public void load(Context context, ImageInfo img) {
    // Glide.with(context).load(img.getUrl()).into(new ViewTarget<YourViewClass,
    // GlideDrawable>(yourViewObject) {
    // @Override
    // public void onResourceReady(GlideDrawable resource, GlideAnimation anim)
    // {
    // YourViewClass myView = this.view;
    // // Set your resource on myView and/or start your animation here.
    // }
    // });
    // }

    /**
     * 清除图片磁盘缓存
     */
    public void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context).clearDiskCache();
                    }
                });
            } else {
                Glide.get(context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    public void clearImageMemoryCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { // 只能在主线程执行
                Glide.get(context).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片所有缓存
     */
    public void clearAllCache(final Context context) {
        clearImageDiskCache(context);
        clearImageMemoryCache(context);
        deleteFolderFile(ImageExternalCatchDir, true);
    }

    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    public String getCacheSize() {
        try {
            return getFormatSize(getFolderSize(new File(ImageExternalCatchDir)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取指定文件夹内所有文件大小的和 *
     *
     * @param file
     * @return size
     * @throws Exception
     */
    public long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    public void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size size
     * @return String
     */
    public static String getFormatSize(double size) {

        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}
