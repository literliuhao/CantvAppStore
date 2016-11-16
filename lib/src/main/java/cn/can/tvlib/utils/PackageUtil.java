package cn.can.tvlib.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：v1.0
 * 创建日期：
 * 描    述：安装包管理和相关信息查询工具类
 * 修订历史：
 * ================================================
 */
public class PackageUtil {

    // 应用安装状态
    public static final int STATE_INSTALLED = 1;//已安装
    public static final int STATE_UNINSTALL = 2;//未安装
    public static final int STATE_HAS_UPDATE = 3;//有更新

    // -----------------------------------------------------  应用安装卸载、操作相关  -------------------------------
    /**
     * 调用系统安装接口，安装一个apk文件
     * @param context
     * @param apkFile
     */
    public static void install(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 调用系统安装接口，安装一个apk文件
     * @param context
     * @param apkFilePath
     */
    public static void install(Context context, String apkFilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.parse("file://" + apkFilePath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 调用系统安装接口，卸载一个app
     * @param context
     * @param packageName
     */
    public static void unInstall(Context context, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        context.startActivity(intent);
    }

    public static boolean isInstalled(Context context, String packageName) {
        try {
            context.getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName
     */
    public static void openApp(Context context, String packageName){
        openApp(context, packageName, null);
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName
     */
    public static void openApp(Context context, String packageName, Bundle params) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return;
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            if(params != null){
                intent.putExtras(params);
            }
            context.startActivity(intent);
        } else {
            Log.v(packageName, "指定包名的程序并未安装...");
        }
    }

    /***
     * 根据浏览器打开URL(下载也可以用这个)
     */
    public static void openWebURL(Context context, String webUri) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }



    // -----------------------------------------------------  应用信息相关  -------------------------------
    /**
     * 获取当前应用的版本号
     */
    public static String getMyVersionName(Context context) {
        try {
            return context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取当前应用的版本号
     */
    public static int getMyVersionCode(Context context) {
        try {
            return context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 判断该应用的安装情况
     *
     * @param context
     * @param packageName 要判断应用的包名
     * @param versionCode 要判断应用的版本号
     */
    private int queryInstallState(Context context, String packageName, int versionCode) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo pi : pakageinfos) {
            String pkgName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if (packageName.endsWith(pkgName)) {
                if (versionCode == pi_versionCode) {
                    Log.i("", "已经安装，不用更新，可以卸载该应用");
                    return STATE_INSTALLED;
                } else if (versionCode > pi_versionCode) {
                    Log.i("", "已经安装，有更新");
                    return STATE_HAS_UPDATE;
                }
            }
        }
        Log.i("", "未安装该应用，可以安装");
        return STATE_UNINSTALL;
    }

    /**
     * 根据包名获取某个应用的相关信息
     * @param context
     * @param packageName
     * @return
     */
    public static AppInfo getAppInfo(Context context, String packageName){
        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for (PackageInfo info : pList) {
            if(!packageName.equals(info.packageName)){
                continue;
            }
            final AppInfo app = new AppInfo();
            app.packageName = info.packageName;
            app.versionName = info.versionName;
            app.versionCode = info.versionCode;
            app.installtime = info.firstInstallTime;
            ApplicationInfo applicationInfo = info.applicationInfo;
            app.installPath = applicationInfo.sourceDir;
            app.appIcon = applicationInfo.loadIcon(pm);
            app.appName = applicationInfo.loadLabel(pm).toString();
            // 是否是系统权限
            app.isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            // 应用占用空间大小
            try {
                Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                method.setAccessible(true);
                method.invoke(pm, app.packageName, new IPackageStatsObserver.Stub(){
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        app.size = pStats.codeSize + pStats.dataSize + pStats.cacheSize;
                    }
                });
            } catch (Exception e) {
                File apk = new File(applicationInfo.sourceDir);
                app.size = apk.length();// apk包文件大小
            }
            return app;
        }
        return null;
    }

    /**
     * 获取已安装的所有应用信息
     * @param context
     * @return
     */
    public static List<AppInfo> findAllApps(Context context, List<AppInfo> appList) {
        if(appList == null){
            appList = new ArrayList<>();
        } else {
            appList.clear();
        }
        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for (PackageInfo info : pList) {
            final AppInfo app = new AppInfo();
            app.packageName = info.packageName;
            app.versionName = info.versionName;
            app.versionCode = info.versionCode;
            app.installtime = info.firstInstallTime;
            ApplicationInfo applicationInfo = info.applicationInfo;
            app.installPath = applicationInfo.sourceDir;
            app.appIcon = applicationInfo.loadIcon(pm);
            app.appName = applicationInfo.loadLabel(pm).toString();
            // 是否是系统权限
            app.isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            // 应用占用空间大小
            try {
                Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                method.setAccessible(true);
                method.invoke(pm, app.packageName, new IPackageStatsObserver.Stub(){
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        app.size = pStats.codeSize + pStats.dataSize + pStats.cacheSize;
                    }
                });
            } catch (Exception e) {
                File apk = new File(applicationInfo.sourceDir);
                app.size = apk.length();// apk包文件大小
            }
            appList.add(app);
        }
        return appList;
    }

    /**
     * 获取所有系统应用信息
     * @param context
     * @return
     */
    public static List<AppInfo> findAllSystemApps(Context context, List<AppInfo> appList) {
        if(appList == null){
            appList = new ArrayList<>();
        } else {
            appList.clear();
        }
        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for (PackageInfo info : pList) {
            ApplicationInfo applicationInfo = info.applicationInfo;
            // 是否是系统权限
            boolean isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            if(!isSystemApp){
                continue;
            }
            final AppInfo app = new AppInfo();
            app.isSystemApp = isSystemApp;
            app.packageName = info.packageName;
            app.versionName = info.versionName;
            app.versionCode = info.versionCode;
            app.installtime = info.firstInstallTime;
            app.installPath = applicationInfo.sourceDir;
            app.appIcon = applicationInfo.loadIcon(pm);
            app.appName = applicationInfo.loadLabel(pm).toString();
            // 应用占用空间大小
            try {
                Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                method.setAccessible(true);
                method.invoke(pm, app.packageName, new IPackageStatsObserver.Stub(){
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        app.size = pStats.codeSize + pStats.dataSize + pStats.cacheSize;
                    }
                });
            } catch (Exception e) {
                File apk = new File(applicationInfo.sourceDir);
                app.size = apk.length();// apk包文件大小
            }
            appList.add(app);
        }
        return appList;
    }

    /**
     * 获取已安装的第三方应用信息
     * @param context
     * @return
     */
    public static List<AppInfo> findAllThirdPartyApps(Context context, List<AppInfo> appList) {
        if(appList == null){
            appList = new ArrayList<>();
        } else {
            appList.clear();
        }
        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for (PackageInfo info : pList) {
            ApplicationInfo applicationInfo = info.applicationInfo;
            // 是否是系统权限
            boolean isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            if(isSystemApp){
                continue;
            }
            final AppInfo app = new AppInfo();
            app.isSystemApp = isSystemApp;
            app.packageName = info.packageName;
            app.versionName = info.versionName;
            app.versionCode = info.versionCode;
            app.installtime = info.firstInstallTime;
            app.installPath = applicationInfo.sourceDir;
            app.appIcon = applicationInfo.loadIcon(pm);
            app.appName = applicationInfo.loadLabel(pm).toString();
            // 应用占用空间大小
            try {
                Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                method.setAccessible(true);
                method.invoke(pm, app.packageName, new IPackageStatsObserver.Stub(){
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        app.size = pStats.codeSize + pStats.dataSize + pStats.cacheSize;
                    }
                });
            } catch (Exception e) {
                File apk = new File(applicationInfo.sourceDir);
                app.size = apk.length();// apk包文件大小
            }
            appList.add(app);
        }
        return appList;
    }

    /**
     * 获取处于白名单中的系统应用 + 第三方应用
     * @param context
     * @return
     */
    public static List<AppInfo> findAllComplexApps(Context context, List<AppInfo> appList, List<String> appWhiteList) {
        if(appList == null){
            appList = new ArrayList<>();
        } else {
            appList.clear();
        }
        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        for (PackageInfo info : pList) {
            ApplicationInfo applicationInfo = info.applicationInfo;
            boolean isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            // 排除不在白名单中的系统应用
            if(isSystemApp && !appWhiteList.contains(info.packageName)){
                continue;
            }
            final AppInfo app = new AppInfo();
            app.isSystemApp = isSystemApp;
            app.packageName = info.packageName;
            app.versionName = info.versionName;
            app.versionCode = info.versionCode;
            app.installtime = info.firstInstallTime;
            app.installPath = applicationInfo.sourceDir;
            app.appIcon = applicationInfo.loadIcon(pm);
            app.appName = applicationInfo.loadLabel(pm).toString();

            // 应用占用空间大小
            try {
                Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                method.setAccessible(true);
                method.invoke(pm, app.packageName, new IPackageStatsObserver.Stub(){
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        app.size = pStats.codeSize + pStats.dataSize + pStats.cacheSize;
                    }
                });
            } catch (Exception e) {
                File apk = new File(applicationInfo.sourceDir);
                app.size = apk.length();// apk包文件大小
            }
            appList.add(app);
        }
        return appList;
    }

    public static class AppInfo {
        public String appName = "";
        public String packageName = "";
        public String versionName = "";
        public int versionCode = 0;
        public Drawable appIcon = null;
        public boolean isSystemApp = false;
        public long size = 0;
        public String installPath = "";   //已经安装的apk文件的路径  在data/app下有   .apk文件
        public String apkPath = "";  // 所有的apk文件的路径
        public long installtime = 0;

        @Override
        public String toString() {
            return "AppInfo{" +
                    "appName='" + appName + '\'' +
                    ", packageName='" + packageName + '\'' +
                    ", versionName='" + versionName + '\'' +
                    ", versionCode=" + versionCode +
                    ", appIcon=" + appIcon +
                    ", isSystemApp=" + isSystemApp +
                    ", size=" + size +
                    ", installPath='" + installPath + '\'' +
                    ", apkPath='" + apkPath + '\'' +
                    ", installtime=" + installtime +
                    '}';
        }


        public AppInfo(String appName, Drawable appIcon) {
            this.appName = appName;
            this.appIcon = appIcon;
        }

        public AppInfo() {   }



        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            AppInfo appInfo = (AppInfo) o;

            if (versionCode != appInfo.versionCode)
                return false;
            if (isSystemApp != appInfo.isSystemApp)
                return false;
            if (size != appInfo.size)
                return false;
            if (installtime != appInfo.installtime)
                return false;
            if (appName != null ? !appName.equals(appInfo.appName) : appInfo.appName != null)
                return false;
            if (packageName != null ? !packageName.equals(appInfo.packageName) : appInfo.packageName != null)
                return false;
            if (versionName != null ? !versionName.equals(appInfo.versionName) : appInfo.versionName != null)
                return false;
            if (installPath != null ? !installPath.equals(appInfo.installPath) : appInfo.installPath != null)
                return false;
            return apkPath != null ? apkPath.equals(appInfo.apkPath) : appInfo.apkPath == null;

        }

        @Override
        public int hashCode() {
            int result = appName != null ? appName.hashCode() : 0;
            result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
            result = 31 * result + (versionName != null ? versionName.hashCode() : 0);
            result = 31 * result + versionCode;
            result = 31 * result + (isSystemApp ? 1 : 0);
            result = 31 * result + (int) (size ^ (size >>> 32));
            result = 31 * result + (installPath != null ? installPath.hashCode() : 0);
            result = 31 * result + (apkPath != null ? apkPath.hashCode() : 0);
            result = 31 * result + (int) (installtime ^ (installtime >>> 32));
            return result;
        }
    }
}
