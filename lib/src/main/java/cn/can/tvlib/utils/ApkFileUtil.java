package cn.can.tvlib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

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
 * 作    者：zhangbingyuan
 * 版    本：v1.0
 * 创建日期：
 * 描    述：apk安装包文件解析工具类
 * 修订历史：
 * ================================================
 */
public class ApkFileUtil {

    public static final int STATE_INSTALLED = 1;
    public static final int STATE_UNINSTALL = 2;
    public static final int STATE_HAS_UPDATE = 3;

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
