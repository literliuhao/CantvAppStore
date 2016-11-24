package com.can.appstore.search;

import android.widget.Toast;

import com.can.appstore.MyApp;

/**
 * author: yibh
 * Date: 2016/10/12  18:41 .
 */
public class ToastUtil {
    public static void toastShort(String msg) {

    }

    public static void toastLong(String msg) {
        Toast.makeText(MyApp.mContext, msg, Toast.LENGTH_LONG).show();
    }

    static long time;
    static long limitime;
    static String message = "";

    /**
     * 避免同一内容连续响应显示
     *
     * @param msg       提示消息
     * @param timeLimit 多条消息之间的间隔,可忽略(默认3秒)
     */
    public static void toastShortTimeLimit(String msg, long... timeLimit) {
        if ((null != timeLimit) && (timeLimit.length > 0)) {
            limitime = timeLimit[0];
        } else {
            limitime = 3000;
        }
        if (System.currentTimeMillis() - time > limitime) {
            time = System.currentTimeMillis();
            message = msg;
            Toast.makeText(MyApp.mContext, msg, Toast.LENGTH_SHORT).show();
        } else {
            if (!message.equals(msg)) {
                message = msg;
                Toast.makeText(MyApp.mContext, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
