package cn.can.downloadlib.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static android.content.pm.PackageManager.GET_ACTIVITIES;

/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ApkUtils {

    public static final int STATE_INSTALLED = 1;
    public static final int STATE_UNINSTALL = 2;
    public static final int STATE_HAS_UPDATE = 3;

    /**
     * 调用系统安装接口，安装一个apk文件
     *
     * @param context
     * @param uriFile
     */
    public static void install(Context context, File uriFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(uriFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 调用系统安装接口，卸载一个app
     *
     * @param context
     * @param packageName
     */
    public static void uninstall(Context context, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvailable(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param file
     * @return
     */
    public static boolean isAvailable(Context context, File file) {
        return isAvailable(context, getPackageName(context, file.getAbsolutePath()));
    }

    /**
     * 根据文件路径获取包名
     *
     * @param context
     * @param filePath
     * @return
     */
    public static String getPackageName(Context context, String filePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return appInfo.packageName;  //得到安装包名称
        }
        return null;
    }

    /**
     * 从apk中获取版本信息
     *
     * @param context
     * @param channelPrefix
     * @return
     */
    public static String getChannelFromApk(Context context, String channelPrefix) {
        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelPrefix;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split(channelPrefix);
        String channel = "";
        if (split.length >= 2) {
            channel = ret.substring(key.length());
        }
        return channel;
    }

    /**
     * 获取未安装APK文件的包名
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static String getPkgNameFromApkFile(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return "";
        }
        PackageManager pm = context.getApplicationContext().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return appInfo.packageName;
        }
        return "";
    }

    /**
     * 获取已安装的所有apk信息
     *
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
            bean.size = apk.length();// apk包文件大小

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
     * 解析指定目录apk文件信息
     *
     * @param context
     * @param apkFile
     * @return
     */
    public static ApkFile parseFromFile(Context context, File apkFile) {
        return parseFromPath(context, apkFile.getAbsolutePath());
    }

    /**
     * 解析指定目录apk文件信息
     *
     * @param context
     * @param apkFilePath
     * @return
     */
    public static ApkFile parseFromPath(Context context, String apkFilePath) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFilePath, GET_ACTIVITIES);
        ApkFile apkFile = null;
        if (packageInfo != null) {
            apkFile = new ApkFile();
            apkFile.filePath = apkFilePath;
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            appInfo.sourceDir = apkFilePath;
            appInfo.publicSourceDir = apkFilePath;
            apkFile.apkIcon = appInfo.loadIcon(packageManager);
            apkFile.packageName = packageInfo.packageName;
            apkFile.versionName = packageInfo.versionName;
            apkFile.versionCode = packageInfo.versionCode;
        }
        return apkFile;
    }

    /**
     * 根据安装包路径判断该应用是否已安装
     *
     * @param context
     * @param apkFilePath
     * @return
     */
    public static boolean isInstalled(Context context, String apkFilePath) {
        ApkFile apkFile = parseFromPath(context, apkFilePath);
        return apkFile != null;
    }

    /**
     * 根据安装包路径、提供的版本号 判断该应用的安装状态<br/>
     * STATE_INSTALLED 、 STATE_UNINSTALL 、 STATE_HAS_UPDATE
     *
     * @param context
     * @param apkFilePath
     * @param newVersionCode
     * @return
     */
    public static int parseInstallState(Context context, String apkFilePath, int newVersionCode) {
        ApkFile apkFile = parseFromPath(context, apkFilePath);
        if (apkFile == null) {
            return STATE_UNINSTALL;
        }
        if (apkFile.versionCode < newVersionCode) {
            return STATE_HAS_UPDATE;
        }
        return STATE_INSTALLED;
    }

    /**
     * 从指定目录扫描安装包
     *
     * @param context
     * @param dir
     * @param recursive //是否递归查找
     * @return
     */
    public static List<ApkFile> searchFiles(Context context, File dir, boolean recursive) {
        if (dir.isFile()) {
            throw new IllegalArgumentException("The file \"" + dir.getAbsolutePath() + "\" isn't a directory.");
        }
        List<ApkFile> apkFiles = new ArrayList<>();
        searchFiles(context.getApplicationContext(), dir, apkFiles, recursive);
        return apkFiles;
    }

    private static void searchFiles(Context context, File fileDir, List<ApkFile> apkFiles, boolean recursive) {
        if (fileDir.isFile()) {
            if (fileDir.getName().toLowerCase().endsWith(".apk")) {
                ApkFile apkFile = parseFromFile(context, fileDir);
                if (apkFile != null) {
                    apkFiles.add(apkFile);
                }
            }
            return;
        }

        File[] files = fileDir.listFiles();
        for (int i = 0, fileCount = files.length; i < fileCount; i++) {
            File file = files[i];
            if (recursive) {
                searchFiles(context, file, apkFiles, recursive);
            } else {
                if (file.getName().toLowerCase().endsWith(".apk")) {
                    ApkFile apkFile = parseFromFile(context, fileDir);
                    if (apkFile != null) {
                        apkFiles.add(apkFile);
                    }
                }
            }
        }
    }

    /**
     * 从apk中获取渠道信息
     *
     * @param context
     * @param channelPrefix
     * @return
     */
    public static String parseChannel(Context context, String channelPrefix) {
        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelPrefix;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split(channelPrefix);
        String channel = "";
        if (split.length >= 2) {
            channel = ret.substring(key.length());
        }
        return channel;
    }

    /**
     * 判断apk安装控件是否充足
     *
     * @param apkSize apk大小 ，单位byte
     * @return
     */
    public static boolean isEnoughSpaceSize(long apkSize) {

        File dataPath = Environment.getDataDirectory();
        float per = dataPath.getUsableSpace() / dataPath.getTotalSpace();
        float space = dataPath.getUsableSpace() - apkSize;
        if (space > 0.1 && apkSize < dataPath.getUsableSpace() && space > 50) {
            return true;
        }
        return false;
    }


    public static class ApkFile {
        Drawable apkIcon;
        String packageName;
        String filePath;
        String versionName;
        int versionCode;

        public Drawable getApkIcon() {
            return apkIcon;
        }

        public void setApkIcon(Drawable apkIcon) {
            this.apkIcon = apkIcon;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }
    }
}
