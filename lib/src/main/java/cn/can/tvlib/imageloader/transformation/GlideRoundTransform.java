package cn.can.tvlib.imageloader.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;


/**
 * 将图片处理为圆角（在加载图片时设置此Transorm即可）
 *
 * @author zhangbingyuan
 */
public class GlideRoundTransform extends BitmapTransformation {

	private static float radius = 0f;

	public GlideRoundTransform(Context context) {
		this(context, 7);
	}

	public GlideRoundTransform(Context context, float cornerRadius) {
		super(context);
		radius = cornerRadius;
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		return roundCrop(pool, toTransform);
	}

	private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
		if (source == null)
			return null;

		Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);
		RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rectF, radius, radius, paint);
		return result;
	}

	@Override
	public String getId() {
		return getClass().getName() + Math.round(radius);
	}
}
