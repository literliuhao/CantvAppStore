package cn.can.tvlib.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import cn.can.tvlib.R;
import cn.can.tvlib.ui.widgets.LoadingDialog;

public class PromptUtils {

    /**
     * show system toast
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void sToast(Context context, String msg, int duration) {
        Toast.makeText(context, msg, duration).show();
    }

    /**
     * show system toast
     *
     * @param context
     * @param msg
     */
    public static void sToast(Context context, String msg) {
        sToast(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * show system toast
     *
     * @param context
     * @param msg
     */
    public static void sToastLong(Context context, String msg) {
        sToast(context, msg, Toast.LENGTH_LONG);
    }

    /**
     * show toast, which is filled by custom view
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void toast(Context context, String msg, int duration) {
        toast(context, msg, duration, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics()));
    }

    /**
     * show toast, which is filled by custom view and show a little time.
     *
     * @param context
     * @param msg
     */
    public static void toast(Context context, String msg) {
        toast(context, msg, Toast.LENGTH_SHORT, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics()));
    }

    /**
     * show toast, which is filled by custom view and show a long time.
     *
     * @param context
     * @param msg
     */
    public static void toastLong(Context context, String msg) {
        toast(context, msg, Toast.LENGTH_LONG, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics()));
    }

    private static Toast mToast = null;

    /**
     * show toast, which is filled by custom view.
     *
     * @param context
     * @param msg
     * @param duration
     */
    @SuppressWarnings("deprecation")
    public static void toast(Context context, String msg, int duration, int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            ((TextView) (mToast.getView().findViewById(R.id.toast_text_id))).setText(msg);
            mToast.setDuration(duration);
            mToast.show();
            return;
        }

        TextView tv = new TextView(context);
        tv.setId(R.id.toast_text_id);
        int paddingVertPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18,
                context.getResources().getDisplayMetrics());
        int paddingHoriPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                context.getResources().getDisplayMetrics());
        tv.setPadding(paddingHoriPx, paddingVertPx, paddingHoriPx, paddingVertPx);
        int cornerRadiusPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                context.getResources().getDisplayMetrics());
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(cornerRadiusPx);
        gd.setColor(Color.parseColor("#79000000"));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            tv.setBackground(gd);
        } else {
            tv.setBackgroundDrawable(gd);
        }
        tv.setTextColor(-1);
        int textSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                context.getResources().getDisplayMetrics());
        tv.setTextSize(textSizePx);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        tv.setEllipsize(TruncateAt.MARQUEE);
        tv.setMarqueeRepeatLimit(-1);
        tv.setSelected(true);
        tv.setText(msg);

        mToast = new Toast(context);
        mToast.setGravity(gravity, xOffset, yOffset);
        mToast.setView(tv);
        mToast.setDuration(duration);
        mToast.show();
    }

    public static void toastShort(Context context, String msg) {
        if (mToast != null) {
            mToast.setText(msg);
            mToast.show();
            return;
        }
        mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static Dialog showSysLoadingDialog(Context context, String msg) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage(msg);
        pd.show();
        return pd;
    }

    public static Dialog showLoadingDialog(Context context, String msg) {
        LoadingDialog pd = new LoadingDialog(context);
        pd.setMessage(msg);
        pd.show();
        return pd;
    }

    public static Dialog showLoadingDialog(Context context) {
        LoadingDialog pd = new LoadingDialog(context);
        pd.show();
        return pd;
    }

    public static Dialog showLoadingDialog(Context context, int loadingSize) {
        LoadingDialog pd = new LoadingDialog(context, loadingSize);
        pd.show();
        return pd;
    }

    public static Dialog showLoadingDialog(Context context, int loadingSize,int offsetX ) {
        LoadingDialog pd = new LoadingDialog(context, loadingSize, offsetX );
        pd.show();
        return pd;
    }

    public static Dialog showLoadingDialog(Context context, int loadingSize,int offsetX , String msg, int textSize, int textColor , int spaceInPixels) {
        LoadingDialog pd = new LoadingDialog(context, loadingSize, offsetX , msg , textSize , textColor , spaceInPixels);
        pd.show();
        return pd;
    }

    public static Dialog showLoadingDialog(Context context, int loadingSize , String msg, int textSize, int textColor , int spaceInPixels) {
        LoadingDialog pd = new LoadingDialog(context, loadingSize , msg , textSize , textColor , spaceInPixels);
        pd.show();
        return pd;
    }
}
