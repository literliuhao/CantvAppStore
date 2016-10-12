package cn.can.tvlib.imageloader;

import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.can.tvlib.R;

public class ImageInfo {
	private String url;
	boolean isSkipMemoryCache;
	boolean isGif;
	private int placeHolder;
	private ImageView imgView;
	private int cornerSizeDp;
	private float thumbnail;
	private int errorHolder;
	// private BitmapTransformation bmpTransfor;
	private DiskCacheStrategy diskCacheStrategy;
	private int width;
	private int height;
	private Priority priority;
	private OnLoadFinishListener loadFinishListener;

	private ImageInfo(Builder builder) {
		this.url = builder.url;
		this.placeHolder = builder.placeHolder;
		this.imgView = builder.imgView;
		this.isSkipMemoryCache = builder.isSkipMemoryCache;
		this.cornerSizeDp = builder.cornerSizeDp;
		this.thumbnail = builder.thumbnail;
		this.errorHolder = builder.errorHolder;
		// this.bmpTransfor = builder.bmpTransfor;
		this.diskCacheStrategy = builder.diskCacheStrategy;
		this.isGif = builder.isGif;
		this.width = builder.width;
		this.height = builder.height;
		this.priority = builder.priority;
		this.loadFinishListener = builder.loadFinishListener;
	}

	public String getUrl() {
		return url;
	}

	public int getPlaceHolder() {
		return placeHolder;
	}

	public ImageView getImgView() {
		return imgView;
	}

	public boolean isSkipMemoryCache() {
		return isSkipMemoryCache;
	}

	public int getCornerSizeDp() {
		return cornerSizeDp;
	}

	public float getThumbnail() {
		return thumbnail;
	}

	public int getErrorHolder() {
		return errorHolder;
	}

	// public BitmapTransformation getBmpTransfor() {
	// return bmpTransfor;
	// }

	public DiskCacheStrategy getDiskCacheStrategy() {
		return diskCacheStrategy;
	}

	public boolean isGif() {
		return isGif;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Priority getPriority() {
		return priority;
	}

	public OnLoadFinishListener getLoadFinishListener() {
		return loadFinishListener;
	}

	public void setLoadFinishListener(OnLoadFinishListener loadFinishListener) {
		this.loadFinishListener = loadFinishListener;
	}

	public static class Builder {
		private String url;
		private int placeHolder;
		private int errorHolder;
		private ImageView imgView;
		private boolean isSkipMemoryCache;
		boolean isGif;
		private int cornerSizeDp;
		private float thumbnail;
		// private BitmapTransformation bmpTransfor;
		private DiskCacheStrategy diskCacheStrategy;
		private int width;
		private int height;
		private Priority priority;

		private OnLoadFinishListener loadFinishListener;

		public Builder() {
			this.url = "";
			this.placeHolder = R.drawable.news_detail_bg;
			this.imgView = null;
			this.isSkipMemoryCache = false;
			this.cornerSizeDp = 0;
			this.thumbnail = 1;
			this.errorHolder = R.drawable.errorholder;
			this.isGif = false;
			this.priority = Priority.NORMAL;
			this.diskCacheStrategy = DiskCacheStrategy.SOURCE;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder placeHolder(int placeHolder) {
			this.placeHolder = placeHolder;
			return this;
		}

		public Builder imgView(ImageView imgView) {
			this.imgView = imgView;
			return this;
		}

		public Builder isSkipMemoryCache(boolean isSkipMemoryCache) {
			this.isSkipMemoryCache = isSkipMemoryCache;
			return this;
		}

		public Builder isGif(boolean isGif) {
			this.isGif = isGif;
			return this;
		}

		public Builder cornerSizeDp(int cornerSizeDp) {
			this.cornerSizeDp = cornerSizeDp;
			return this;
		}

		public Builder thumbnail(float thumbnail) {
			this.thumbnail = thumbnail;
			return this;
		}

		public Builder errorHolder(int errorHolder) {
			this.errorHolder = errorHolder;
			return this;
		}

		// public Builder transform(BitmapTransformation bmpTransfor) {
		// if (bmpTransfor != null) {
		// this.bmpTransfor = bmpTransfor;
		// }
		// return this;
		// }

		public Builder diskCacheStrategy(DiskCacheStrategy diskCacheStrategy) {
			if (diskCacheStrategy != null) {
				this.diskCacheStrategy = diskCacheStrategy;
			}
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder height(int height) {
			this.height = height;
			return this;
		}

		public Builder priority(Priority priority) {
			this.priority = priority;
			return this;
		}

		public Builder loadListener(OnLoadFinishListener listener) {
			this.loadFinishListener = listener;
			return this;
		}

		public ImageInfo build() {
			return new ImageInfo(this);
		}
	}

	public interface OnLoadFinishListener {
		public void onSuccess();

		public void onFail();
	}
}
