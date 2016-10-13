package cn.can.tvlib.imageloader.module;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;

import cn.can.tvlib.R;


/***
 * Glide相关配置项<p/>
 *
 * 使用方式：<br/>
 *      使用此Module只需要在Manifest.xml中声明key-value分别为"[自定义Module全路径包名]" 和 "GlideModule" 的meta-data标签，
 * Glide会在初始化时自动加载此配置项<br/>
 * <br/>
 *
 * ps: 如果需要，可在Manifest中配置多个GlideModule
 */
public class CustomGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        ViewTarget.setTagId(R.id.glide_tag_id);

        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));

        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));

        final int cacheSize200MegaBytes = 209715200;
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, "Glidecache", cacheSize200MegaBytes));

//      builder.setDiskCache(new InternalCacheDiskCacheFactory(App.getContext(), 104857600));//100M
//		builder.setDiskCache(new DiskLruCacheFactory(new CacheDirectoryGetter() {
//			@Override
//			public File getCacheDirectory() {
//				return getMyCacheLocationBlockingIO();
//			}
//		}), cacheSize200MegaBytes);
//		builder.setDiskCache(new DiskCache.Factory() {
//		    @Override public DiskCache build() {
//		        File cacheLocation = getMyCacheLocationBlockingIO();
//		        cacheLocation.mkdirs();
//		        return DiskLruCacheWrapper.get(cacheLocation, cacheSize200MegaBytes);
//		    }
//		});
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }

}
