package cn.can.tvlib.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import cn.can.tvlib.R;
import cn.can.tvlib.ui.widgets.LoadingDialog;

public class PromptUtils {
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static TextView mTextView;
    private static Object synObj = new Object();

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
     * show toast, which is filled by custom view and show a little time.
     *
     * @param context
     * @param msg
     */
    public static void toast(Context context, String msg) {
        toast(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * show toast, which is filled by custom view and show a long time.
     *
     * @param context
     * @param msg
     */
    public static void toastLong(Context context, String msg) {
        toast(context, msg, Toast.LENGTH_LONG);
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
    public static void toast(final Context context, final String msg, final int duration) {
        {
            if (mToast != null) {
                mToast.cancel();
            }
            new Thread(new Runnable() {
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (synObj) {
                                if (mToast == null) {
                                    mToast = new Toast(context.getApplicationContext());
                                }
                                if (mTextView == null) {
                                    mTextView = new TextView(context.getApplicationContext());
                                    mTextView.setBackgroundResource(R.drawable.shape_toast);
                                    mTextView.setTextColor(Color.WHITE);
                                    mTextView.setPadding(50, 30, 50, 30);
                                    mTextView.setTextSize(30);
                                }
                                mTextView.setText(msg);
                                mToast.setDuration(duration);
                                mToast.setView(mTextView);
                                mToast.show();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    public static void toastShort(Context context, String msg) {
        toast(context,msg,Toast.LENGTH_SHORT);
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
