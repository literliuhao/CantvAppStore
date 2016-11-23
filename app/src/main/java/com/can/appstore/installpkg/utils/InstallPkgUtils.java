package com.can.appstore.installpkg.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.utils.UpdateUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import cn.can.downloadlib.utils.SdcardUtils;
import cn.can.downloadlib.utils.ShellUtils;
import cn.can.downloadlib.utils.ToastUtils;

import static android.content.pm.PackageManager.GET_ACTIVITIES;

/**
 * ================================================
 * 作    者：zhl
 * 版    本：1.0
 * 创建日期：2016/08/31 14:17
 * 描    述：获取手机上apk文件信息类，主要是判断是否安装再手机上了，安装的版本比较现有apk版本信息
 * 修订历史：
 * ================================================
 */
public class InstallPkgUtils {
    private static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
    private static int UNINSTALLED = 1; // 表示未安装
    private static int INSTALLED_UPDATE = 2; // 表示已经安装，版本比现在这个版本要低
    public static List<AppInfoBean> myFiles = new ArrayList<AppInfoBean>();//安装包集合

    /**
     * 递归获取指定目录下每个目录中的apk文件
     *
     * @param path 路径
     */
    public static List FindAllAPKFile(String path) {

        File[] files = new File(path).listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.endsWith(".apk")) {  //判断扩展名
                    AppInfoBean bean = new AppInfoBean();
                    String apk_path = null;
                    apk_path = file.getAbsolutePath();
                    PackageManager pm = MyApp.mContext.getPackageManager();
                    PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, GET_ACTIVITIES);
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    /**获取apk的图标 */
                    appInfo.sourceDir = apk_path;
                    appInfo.publicSourceDir = apk_path;
                    Drawable apk_icon = appInfo.loadIcon(pm);
                    bean.setIcon(apk_icon);
                    /** 得到包名 */
                    String packageName = packageInfo.packageName;
                    bean.setPackageName(packageName);
                    /** apk的绝对路劲 */
                    bean.setFliePath(file.getAbsolutePath());
                    bean.setAppSize(new File(file.getAbsolutePath()).length() / 1024 / 1024 + "M");
                    /**获取文件名*/
                    bean.setAppName(getFileNameNoEx(new File(file.getAbsolutePath()).getName()));
                    /**由包名获取应用名*/
                    /*String appName = UpdateUtils.getAppName(MyApp.mContext, packageName);
                    bean.setAppName(appName);*/
                    /** apk的版本名称 */
                    String versionName = packageInfo.versionName;
                    bean.setVersionName(versionName);
                    /** apk的版本号码 */
                    int versionCode = packageInfo.versionCode;
                    bean.setVersionCode(String.valueOf(versionCode));
                    /**安装处理类型*/
                    int type = doType(pm, packageName, versionCode);
                    if (INSTALLED == type) {
                        bean.setInstall(true);
                    } else if (UNINSTALLED == type) {
                        bean.setInstall(false);
                    }
                    myFiles.add(bean);
                }
            } else if (file.isDirectory() && file.getPath().indexOf("/.") == -1) {  //忽略点文件（隐藏文件/文件夹）
                FindAllAPKFile(file.getPath());
            }
        }
        return myFiles;
    }

    /**
     * 判断该应用的安装情况
     *
     * @param pm          PackageManager
     * @param packageName 要判断应用的包名
     * @param versionCode 要判断应用的版本号
     */

    private static int doType(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo pi : pakageinfos) {
            String pkgName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if (packageName.endsWith(pkgName)) {
                if (versionCode == pi_versionCode) {
                    //Log.i("", "已经安装，不用更新，可以卸载该应用");
                    return INSTALLED;
                } else if (versionCode > pi_versionCode) {
                    //Log.i("", "已经安装，有更新");
                    return INSTALLED_UPDATE;
                }
            }
        }
        //Log.i("", "未安装该应用，可以安装");
        return UNINSTALLED;
    }

    /**
     * 删除安装包
     */
    public static void deleteApkPkg(String path) {
        File deleteFile = new File(path);
        if (deleteFile != null && deleteFile.exists()) {
            deleteFile.delete();
        }
    }

    /**
     * 安装应用from url
     */
    public static void installApkFromU(Context context, String uri, boolean install, String packageName) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.parse(uri),
                "application/vnd.android.package-archive");

        context.startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
        openApp(context, packageName);
    }

    /**
     * 安装应用from file
     */
    public static void installApkFromF(Context context, File file, boolean install, String packageName) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        //android.os.Process.killProcess(android.os.Process.myPid());
//        openApp(context,packageName);
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
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
            ComponentName cn = new ComponentName(ri.activityInfo.packageName,
                    className);

            intent.setComponent(cn);
            context.startActivity(intent);
        } else {
            Log.i(packageName, "指定包名的程序并未安装...");
        }
    }

    /**
     * 更新管理静默安装1
     */
    public static int installApp(String path) {

        long space = SdcardUtils.getSDCardAvailableSpace() / 1014 / 1024;
        if (space < 50) {
            ToastUtils.showMessageLong(MyApp.mContext, cn.can.downloadlib.R.string.error_msg);
            return 50;
        }
        ShellUtils.CommandResult res = ShellUtils.execCommand("pm install -r" + path, false);
        Log.i("shen", "installApp: " + res.result);
        //成功
        if (res.result == 0) {
            return 0;
        } else {
            return res.result;
        }
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 安装包管理静默安装
     */
    //final String path = Environment.getExternalStorageDirectory() + File.separator + "baidu"+File.separator + "360MobileSa
    public static int installApp2(String path) {

        long space = SdcardUtils.getSDCardAvailableSpace() / 1014 / 1024;
        if (space < 50) {
            ToastUtils.showMessageLong(MyApp.mContext, cn.can.downloadlib.R.string.error_msg);
            return 50;
        }

        String result = execCommand("pm","install","-r",path);
        Toast.makeText(MyApp.mContext, "安装结果:"+result, Toast.LENGTH_LONG).show();
        //ShellUtils.CommandResult res = ShellUtils.execCommand("pm install -r" + path, false);
        //Log.i("shen", "installApp: " + result);
        //成功
        if (result.contains("Success")) {
            return 0;
        } else {
            return 1;
        }
    }
    /**
     *静默安装方法
     */
    public static String execCommand(String... command) {
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        String result = "";

        try {
            process = new ProcessBuilder().command(command).start();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            result = new String(baos.toByteArray());
            if (inIs != null)
                inIs.close();
            if (errIs != null)
                errIs.close();
            process.destroy();
        } catch (IOException e) {
            result = e.getMessage();
        }
        return result;
    }
}
