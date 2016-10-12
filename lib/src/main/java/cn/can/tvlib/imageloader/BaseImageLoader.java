package cn.can.tvlib.imageloader;

import android.content.Context;

/**
 * 
 */
public interface BaseImageLoader {
   void loadImage(Context ctx, ImageInfo img);
   void clearImageAllCache(Context ctx);
   void clearMemCache(Context ctx);
}
