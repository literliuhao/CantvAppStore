package cn.can.tvlib.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BitmapUtils {

    /**
     * 图片高斯模糊
     *
     * @param bitmap
     * @return
     */
    public static Drawable blurBitmap(Bitmap bitmap, Context ctx) {

        // Let's create an empty bitmap with the same size of the bitmap we want
        // to blur

//		DisplayMetrics dm = new DisplayMetrics();  
//		dm = ctx.getResources().getDisplayMetrics();  
//		int w = dm.widthPixels;
//		int h = dm.heightPixels;
//		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);

        // Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(ctx);

        // Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // Create the Allocations (in/out) with the Renderscript and the in/out
        // bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, compress(bitmap));
//		Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        Allocation allOut = allIn;

        // Set the radius of the blur
        blurScript.setRadius(15.0f);

        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        try {
            //TODO 这里有可能发生异常 android.renderscript.RSIllegalArgumentException
            // Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(bitmap);

            // recycle the original bitmap
            // bitmap.recycle();

            // After finishing everything, we destroy the Renderscript.
            rs.destroy();

            return new BitmapDrawable(ctx.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap compress(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 1280;
        float ww = 1920;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);
    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    private static Bitmap getScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(view.getDrawingCache());
        activity.getWindow().getDecorView().setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return bm;
    }

    /**
     *
     * @param ctx 请传入Activity的引用
     * @return
     */
    public static Drawable blurBitmap(Context ctx) {
        return blurBitmap(getScreenShot((Activity) ctx), ctx);
    }
}
