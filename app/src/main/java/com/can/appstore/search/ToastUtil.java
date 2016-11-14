package com.can.appstore.search;

import android.widget.Toast;

import com.can.appstore.MyApp;

/**
 * author: yibh
 * Date: 2016/10/12  18:41 .
*/
public class ToastUtil {
    public static void toastShort(String msg) {
        Toast.makeText(MyApp.getApplication(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String msg) {
        Toast.makeText(MyApp.getApplication(), msg, Toast.LENGTH_LONG).show();
    }

}
