package cn.can.tvlib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
public class ApkScanSDUtils {
    private static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
    private static int UNINSTALLED = 1; // 表示未安装
    private static int INSTALLED_UPDATE = 2; // 表示已经安装，版本比现在这个版本要低

    private Context context;
    private List<ApkFile> myFiles = new ArrayList<ApkFile>();

    public ApkScanSDUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<ApkFile> getMyFiles() {
        return myFiles;
    }

    public void setMyFiles(List<ApkFile> myFiles) {
        this.myFiles = myFiles;
    }

    /**
     * @param file 递归去找每个目录下面的apk文件
     */
    public void FindAllAPKFile(File file) {
        if (file.isFile()) {
            String fileName = file.getName();
            ApkFile apkFile = new ApkFile();
            String apk_path = null;
            if (fileName.toLowerCase().endsWith(".apk")) {
                apk_path = file.getAbsolutePath();
                PackageManager pm = context.getPackageManager();
                PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, GET_ACTIVITIES);
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                /**获取apk的图标 */
                appInfo.sourceDir = apk_path;
                appInfo.publicSourceDir = apk_path;
                Drawable apk_icon = appInfo.loadIcon(pm);
                apkFile.setApk_icon(apk_icon);
                /** 得到包名 */
                String packageName = packageInfo.packageName;
                apkFile.setPackageName(packageName);
                /** apk的绝对路劲 */
                apkFile.setFilePath(file.getAbsolutePath());
                /** apk的版本名称 */
                String versionName = packageInfo.versionName;
                apkFile.setVersionName(versionName);
                /** apk的版本号码 */
                int versionCode = packageInfo.versionCode;
                apkFile.setVersionCode(versionCode);
                /**安装处理类型*/
                int type = doType(pm, packageName, versionCode);
                apkFile.setInstalled(type);
                myFiles.add(apkFile);
            }
        } else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file_str : files) {
                    FindAllAPKFile(file_str);
                }
            }
        }
    }

    /**
     * 判断该应用的安装情况
     *
     * @param pm          PackageManager
     * @param packageName 要判断应用的包名
     * @param versionCode 要判断应用的版本号
     */
    private int doType(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo pi : pakageinfos) {
            String pkgName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if (packageName.endsWith(pkgName)) {
                if (versionCode == pi_versionCode) {
                    Log.i("", "已经安装，不用更新，可以卸载该应用");
                    return INSTALLED;
                } else if (versionCode > pi_versionCode) {
                    Log.i("", "已经安装，有更新");
                    return INSTALLED_UPDATE;
                }
            }
        }
        Log.i("", "未安装该应用，可以安装");
        return UNINSTALLED;
    }

    class ApkFile {
        Drawable apk_icon;
        String packageName;
        String filePath;
        String VersionName;
        int VersionCode;
        int Installed;

        public Drawable getApk_icon() {
            return apk_icon;
        }

        public void setApk_icon(Drawable apk_icon) {
            this.apk_icon = apk_icon;
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
            return VersionName;
        }

        public void setVersionName(String versionName) {
            VersionName = versionName;
        }

        public int getVersionCode() {
            return VersionCode;
        }

        public void setVersionCode(int versionCode) {
            VersionCode = versionCode;
        }

        public int getInstalled() {
            return Installed;
        }

        public void setInstalled(int installed) {
            Installed = installed;
        }
    }
}
