package com.can.appstore;

import android.app.Application;
import android.content.Context;

/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MyApp extends Application {
<<<<<<< HEAD
    public static Context mContext;
=======
    private static MyApp INSTANCE;

    public static MyApp getApplication() {
        return INSTANCE;
    }
>>>>>>> DEV_1.0

    @Override
    public void onCreate() {
        super.onCreate();
<<<<<<< HEAD
        this.mContext = this;
    }

    public Context getContext() {
        return mContext;
=======
        INSTANCE = this;
>>>>>>> DEV_1.0
    }

}
