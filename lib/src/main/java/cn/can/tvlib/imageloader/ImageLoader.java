package cn.can.tvlib.imageloader;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：Glide封装
 * 修订历史：
 * ================================================
 */

/**
 * <br/>图片加载帮助类（采用单例模式，便于后边扩展 -.-！）<br/>
 * <p>
 * Created by zhangbingyuan on 2016/8/29.<p/>
 * <p>
 * 1. getInstance()  获取加载类实体<br/><br/>
 *
 * 2. load()  加载图片<br/>
 *      ps：如果想配置更多参数，请参见方法 buildTask()<br/><br/>
 *
 * 3. download()  下载图片<br/>
 *      ps：如果想配置更多参数，请参见方法 buildDownloadTask()<br/><br/>
 *
 * 4. pauseTask() 暂停图片加载任务（注：如果Context传Activity，则仅暂停该Activity为上下文的图片加载任务，
 *              如果Activity中嵌套了Fragment，则Fragment中图片加载任务不会停止）<br/><br/>
 *
 * 5. pauseAllTask() 暂停所有图片加载任务<br/><br/>
 *
 * 6. resumeTask() 恢复下载任务<br/><br/>
 *
 * 7. resumeAllTask() 恢复下载任务<br/><br/>
 *
 * 8. clearMemoryCache() 清除内存缓存（注：此方法必须在主线程调用）<br/><br/>
 *
 * 9. clearDiskCache() 清除本地文件缓存（注：此方法推荐在子线程中进行）<br/><br/>
 *
 * 10. downloadImgByUrl()  下载[图片]等文件到指定路径
 */
public class ImageLoader implements ImageLoaderI {

    private static final String TAG = "ImageLoader";

    // Glide已经是单例，这里设计单例方便后边扩展。。。
    private static volatile ImageLoader instance;

    private ImageLoader() {
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
           }
        }
        return instance;
    }

    /**
     * load network image to ImageView.<br/>
     * If u want to modify all kinds of Configs, u can invoke the @method{createTask} to obtain a loadTask, then start it after your config finish.
     *
     * @param context
     * @param view
     * @param url
     */
    @Override
    public void load(Context context, ImageView view, String url) {
        new GlideLoadTask.Builder()
                .view(view)
                .url(url)
                .build()
                .start(context);
    }

    /**
     * load network image to ImageView.<br/>
     * If u want to modify all kinds of Configs, u can invoke the @method{createTask} to obtain a loadTask, then start it after your config finish.
     *
     * @param context
     * @param view
     * @param url
     * @param successCallback
     * @param failCallback
     */
    @Override
    public void load( Context context, ImageView view, String url, GlideLoadTask.SuccessCallback successCallback,
                     GlideLoadTask.FailCallback failCallback) {
        new GlideLoadTask.Builder()
                .view(view)
                .url(url)
                .successCallback(successCallback)
                .failCallback(failCallback)
                .build()
                .start(context);
    }

    /**
     * load network image to ImageView.<br/>
     * If u want to modify all kinds of Configs, u can invoke the @method{createTask} to obtain a loadTask, then start it after your config finish.
     *
     * @param context
     * @param view
     * @param url
     * @param placeHolder
     * @param errorHolder
     * @param successCallback
     * @param failCallback
     */
    @Override
    public void load(Context context, ImageView view, String url, int placeHolder, int errorHolder,
                     GlideLoadTask.SuccessCallback successCallback, GlideLoadTask.FailCallback failCallback) {
        new GlideLoadTask.Builder()
                .view(view)
                .url(url)
                .placeholder(placeHolder)
                .errorHolder(errorHolder)
                .successCallback(successCallback)
                .failCallback(failCallback)
                .build()
                .start(context);
    }

    /**
     * load network image to ImageView.<br/>
     * If u want to modify all kinds of Configs, u can invoke the @method{createTask} to obtain a loadTask, then start it after your config finish.
     *
     * @param context
     * @param view
     * @param url
     * @param anim
     * @param placeHolder
     * @param errorHolder
     * @param successCallback
     * @param failCallback
     */
    @Override
    public void load(Context context, ImageView view, String url, int anim, int placeHolder, int errorHolder,
                     GlideLoadTask.SuccessCallback successCallback, GlideLoadTask.FailCallback failCallback) {
        new GlideLoadTask.Builder()
                .view(view)
                .url(url)
                .animResId(anim)
                .placeholder(placeHolder)
                .errorHolder(errorHolder)
                .successCallback(successCallback)
                .failCallback(failCallback)
                .build()
                .start(context);
    }

    /**
     * Create a Image download task. <br/>
     * The method is run in MainThread
     *
     * @param context
     * @param url
     * @param succCallback
     * @param failCallback
     */
    @Override
    public void download( Context context, String url, GlideLoadTask.DownloadSuccCallback succCallback,
                          GlideLoadTask.DownloadFailCallback failCallback) {
        new GlideLoadTask.DownloadTaskBuilder()
                .url(url)
                .successCallback(succCallback)
                .failCallback(failCallback)
                .build()
                .start(context);
    }

    @Override
    public GlideLoadTask.Builder buildTask(ImageView view, String url) {
        return new GlideLoadTask.Builder().view(view).url(url);
    }

    @Override
    public GlideLoadTask.DownloadTaskBuilder buildDownloadTask(String url) {
        return new GlideLoadTask.DownloadTaskBuilder().url(url);
    }

    @Override
    public void pauseTask(Context context) {
        if(hasDestroyed(context)){
            return;
        }
        Glide.with(context).pauseRequests();
    }

    @Override
    public void pauseAllTask(Context context) {
        if(hasDestroyed(context)){
            return;
        }
        Glide.with(context).pauseRequestsRecursive();
    }

    @Override
    public void resumeTask(Context context) {
        if(hasDestroyed(context)){
            return;
        }
        Glide.with(context).resumeRequests();
    }

    @Override
    public void resumeAllTask(Context context) {
        if(hasDestroyed(context)){
            return;
        }
        Glide.with(context).resumeRequestsRecursive();
    }

    private boolean hasDestroyed(Context context){
        if(context instanceof Activity){
            Activity activity = (Activity) context;
            if(activity.isFinishing() || activity.isDestroyed()){
                return true;
            }
        } else if(context instanceof FragmentActivity){
            FragmentActivity activity = (FragmentActivity) context;
            if(activity.isFinishing() || activity.isDestroyed()){
                return true;
            }
        }
        return false;
    }

    /**
     * Tips : The method must be invoked in main-thread.
     *
     * @param context
     */
    @Override
    public void clearMemoryCache(Context context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("The method 'clearMemoryCache' should be invoke in Main-Thread.");
        }
        Glide.get(context.getApplicationContext()).clearMemory();
    }

    /**
     * Tips : The method is recommended be invoked in subThread.
     *
     * @param context
     */
    @Override
    public void clearDiskCache(Context context) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "The method 'clearDiskCache' is recommended be invoke in the Thread except Main-Thread. ");
        }
        Glide.get(context.getApplicationContext()).clearDiskCache();
    }

    @Override
    public boolean downloadImgByUrl(String urlStr, String path) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            File imageFile = new File(path);
            if (!imageFile.exists()) {
                imageFile.getParentFile().mkdirs();
            } else {
                imageFile.deleteOnExit();
            }
            fos = new FileOutputStream(imageFile);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(true);
            conn.getInputStream();
            is = new BufferedInputStream(conn.getInputStream());
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf, 0, 1024)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
            return false;
        }
    }

}
