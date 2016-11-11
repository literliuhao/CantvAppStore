package com.can.appstore.myapps.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 2016/10/11.
 */

public class AppUtils {

    /**
     * 获取已安装的所有apk信息
     * @param context
     * @return
     */
    public static List<AppInfo> findAllInstallApkInfo(Context context) {
        // 创建集合
        List<AppInfo> list = new ArrayList<AppInfo>();
        // 包管理 所有已经安装的apk
        PackageManager pm = context.getPackageManager();
        // 所有已经安装的apk
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for (PackageInfo info : pList) {
            AppInfo bean = new AppInfo();
            bean.packageName = info.packageName;
            // ApplicationInfo application标签
            ApplicationInfo applicationInfo = info.applicationInfo;
            bean.installApkpath = applicationInfo.sourceDir;
            // 图标
            bean.appIcon = applicationInfo.loadIcon(pm);
            // 名称
            bean.appName = applicationInfo.loadLabel(pm).toString();
            // 版本名称
            bean.versionName = info.versionName;
            // 版本号
            bean.versionCode = info.versionCode;
            // 大小
            File apk = new File(applicationInfo.sourceDir);
            //初次安装时间
            bean.installTime = info.firstInstallTime;
            // apk包文件大小
            bean.size = apk.length();

            // 是否是系统权限
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                bean.isSystemApp = true;
            } else {
                bean.isSystemApp = false;
            }
            list.add(bean);

        }
        return list;
    }

    /**
     * 卸载apk
     */
    public static void uninstallpkg(Context context, String packageName) {
        try {
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:"
                    + packageName));
            context.startActivity(uninstallIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
