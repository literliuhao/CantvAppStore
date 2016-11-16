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

    private static Context mContext;
    private static MyApp INSTANCE;

    public static MyApp getApplication() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

}
