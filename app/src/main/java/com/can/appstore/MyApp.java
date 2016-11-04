package com.can.appstore;

import android.app.Application;

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
    private static MyApp INSTANCE;

    public static MyApp getApplication() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
