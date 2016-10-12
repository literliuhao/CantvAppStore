package cn.can.tvlib.imageloader;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;


/***
 * 有需要的时候在配置
 *
 * @author JasonF
 */
public class GlideManager implements GlideModule {

    public GlideManager() {
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
        // builder.setDiskCache(new
        // InternalCacheDiskCacheFactory(App.getContext(), 104857600));//100M

        final int cacheSize200MegaBytes = 209715200;
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, "Glidecache", cacheSize200MegaBytes));
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
