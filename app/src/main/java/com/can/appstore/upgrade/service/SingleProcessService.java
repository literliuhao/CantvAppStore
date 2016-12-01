package com.can.appstore.upgrade.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.can.appstore.upgrade.InstallApkListener;

import cn.can.downloadlib.utils.SdcardUtils;
import cn.can.tvlib.utils.ShellUtils;

/**
 * Created by syl on 2016/11/29.
 */

public class SingleProcessService extends Service {
    private static final String TAG = "SingleProcessService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:SingleProcessService ");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: SingleProcessService");
        Toast.makeText(SingleProcessService.this, "onCreate", Toast.LENGTH_LONG).show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: SingleProcessService");
        Toast.makeText(SingleProcessService.this, "onStartCommand", Toast.LENGTH_LONG).show();
        //String a = intent.getStringExtra("1");
        //String b = intent.getStringExtra("2");
        //        installApk(this, a, 0, new InstallApkListener() {
        //            @Override
        //            public void onInstallSuccess() {
        //                Toast.makeText(SingleProcessService.this, "安装完成", Toast.LENGTH_LONG).show();
        //                ShellUtils.CommandResult res = ShellUtils.execCommand("pm shell am start -n com.can
        // .appstore/com.can" +
        //                        ".appstore.index.IndexActivity", false);
        //                //SingleProcessService.this.stopSelf();
        //            }
        //
        //            @Override
        //            public void onInstallFail(String reason) {
        //                Toast.makeText(SingleProcessService.this, "安装失败", Toast.LENGTH_LONG).show();
        //                //SingleProcessService.this.stopSelf();
        //            }
        //        });
        //return super.onStartCommand(intent, flags, startId);


        for (int i = 0; i < 1000; i++) {
            Log.d(TAG, "onStartCommand: " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
        //return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        Intent intent1 = new Intent("com.can.appstore.ACTION_INDEX");
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);

        ShellUtils.CommandResult res = ShellUtils.execCommand("pm shell am start -n com.can.appstore/com.can" +
                ".appstore.index.IndexActivity", false);

        super.onDestroy();
    }

    public void installApk(Context mContext, String path, long size, InstallApkListener onInstallApkListener) {
        Log.d(TAG, "installApk: SingleProcessService");
        Toast.makeText(SingleProcessService.this, "installApk", Toast.LENGTH_LONG).show();
        long space = SdcardUtils.getSDCardAvailableSpace();
        if (space < size) {
            onInstallApkListener.onInstallFail(mContext.getResources().getString(cn.can.downloadlib.R.string
                    .error_msg));
            return;
        }
        Log.d("", "path: " + path);
        ShellUtils.CommandResult res = ShellUtils.execCommand("pm install -r " + path, false);
        Log.d("", "inStallApk: " + res.result + "----" + res.errorMsg);
        if (res.result == 0) {
            Toast.makeText(SingleProcessService.this, "安装完成", Toast.LENGTH_LONG).show();
            onInstallApkListener.onInstallSuccess();
        } else {
            Toast.makeText(SingleProcessService.this, "安装失败", Toast.LENGTH_LONG).show();
            onInstallApkListener.onInstallFail(mContext.getResources().getString(cn.can.downloadlib.R.string
                    .error_install));
        }
    }


    public class BootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                String packageName = intent.getDataString().substring(8);
                System.out.println("---------------" + packageName);
                //                Intent newIntent = new Intent();
                //                newIntent.setClassName(packageName, packageName +.MainActivity ");
                //                newIntent.setAction("android.intent.action.MAIN");
                //                newIntent.addCategory("android.intent.category.LAUNCHER");
                //                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //                context.startActivity(newIntent);
            }
        }
    }

}
