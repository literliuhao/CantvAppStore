package com.can.appstore.update.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;

import com.can.appstore.MyApp;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.update.model.AppInfoBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 更新包，安装包管理通用工具类
 * Created by shenpx on 2016/10/20 0020.
 */

public class UpdateUtils {

    private static final String TAG = "UpdateUtils";

    /**
     * 获取SD卡总大小
     *
     * @return
     */
    public static int getSDTotalSize() {
        File sdcardDir = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(sdcardDir.getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalSize = statFs.getBlockCountLong();
        int sdSize = (int) (blockSize * totalSize / 1024 / 1024);
        if (sdSize > 1024) {
            sdSize = sdSize / 1024;
        }
        return sdSize;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return 剩余多少(String)
     */
    public static String getSDAvaliableSize() {
        // 获取存储卡路径
        File path = Environment.getExternalStorageDirectory();
        // StatFs 看文件系统空间使用情况
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        long roomSize = blockSize * availableBlocks / 1024 / 1024;
        String size = "";
        if (roomSize > 1024) {
            size = roomSize / 1024 + "G";
        } else {
            size = roomSize + "M";
        }
        return size;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return 剩余多少(int)
     */
    public static int getSDSurplusSize() {
        // 获取存储卡路径
        File path = Environment.getExternalStorageDirectory();
        // StatFs 看文件系统空间使用情况
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        int roomSize = (int) (blockSize * availableBlocks / 1024 / 1024);
        if (roomSize > 1024) {
            roomSize = roomSize / 1024;
        }
        return roomSize;
    }

    /**
     * 获取系统总内存
     *
     * @param context 可传入应用程序上下文。
     * @return 总内存大单位为B。
     */
    public static long getTotalMemorySize(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取非系统应用信息列表
     */
    public static List<AppInfo> getAppList() {
        ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
        PackageManager pm = MyApp.mContext.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                AppInfo info = new AppInfo();
                info.setPackageName(packageInfo.packageName);
                int versionCode = packageInfo.versionCode;
                info.setVersionCode(versionCode);
                appList.add(info);
            } else {
                //　　　　　　　　
            }

        }
        return appList;
    }


    /**
     * 由包名获取应用名
     *
     * @param packname
     * @return
     */
    public static String getAppName(Context context, String packname) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadLabel(pm).toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return packname;
    }

    /**
     * 由包名获取版本号
     *
     * @param packname
     * @return
     */
    public static int getVersonCode(Context context, String packname) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = pm.getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        int versionCode = packInfo.versionCode;
        return versionCode;
    }

    /**
     * 获取非系统应用信息列表
     */
    public static List<AppInfoBean> getAppInfoBeanList() {
        ArrayList<AppInfoBean> appList = new ArrayList<AppInfoBean>();
        int i = 30;
        PackageManager pm = MyApp.mContext.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {

                ++i;
                AppInfoBean info = new AppInfoBean();
                info.setAppName(packageInfo.applicationInfo.loadLabel(pm)
                        .toString());
                //info.setPackageName(packageInfo.packageName);
                int versionCode = packageInfo.versionCode;
                //info.setVersionCode(String.valueOf(versionCode));
                String versionName = packageInfo.versionName;
                info.setVersionName(versionName);
                info.setAppSize(i + "M");
                Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
                info.setIcon(drawable);
                if (i == 31) {
                    info.setDownloadUrl("http://app.znds.com/down/20160909/dsj2.0-2.9.1-dangbei.apk");
                    info.setPackageName("com.elinkway.tvlive2");
                    info.setVersionCode(String.valueOf(101));
                } else if (i == 32) {
                    info.setDownloadUrl("http://app.znds.com/update/dangbeimarket_3.9.5_znds.apk");
                    info.setPackageName("com.dangbeimarket");
                    info.setVersionCode(String.valueOf(101));
                }else if (i == 33) {
                    info.setDownloadUrl("http://app.znds.com/down/20161118/dbzm_2.1.4.2_dangbei.apk");
                    info.setPackageName("com.dangbei.tvlauncher");
                    info.setVersionCode(String.valueOf(47));
                }else if (i == 34) {
                    info.setDownloadUrl("http://app.znds.com/down/20161117/douyu_1.1.6_dangbei.apk");
                    info.setPackageName("com.douyu.xl.douyutv");
                    info.setVersionCode(String.valueOf(110006));
                }else if (i == 35) {
                    info.setDownloadUrl("http://app.znds.com/down/20161111/qqyy_1.8.0.5_dangbei.apk");
                }else{
                    info.setDownloadUrl("http://");
                }
                // 获取该应用安装包的Intent，用于启动该应用
                //info.appIntent = pm.getLaunchIntentForPackage(packageInfo.packageName);
                appList.add(info);
            } else {
                //　　　　　　　　
            }

        }
        return appList;
    }

//    "http://app.znds.com/down/20160909/dsj2.0-2.9.1-dangbei.apk" 电视家
//    http://app.znds.com/update/dangbeimarket_3.9.5_znds.apk   当贝市场
//    http://app.znds.com/down/20161118/dbzm_2.1.4.2_dangbei.apk  当贝桌面
//    http://app.znds.com/down/20161117/douyu_1.1.6_dangbei.apk   斗鱼
//    http://app.znds.com/down/20161111/qqyy_1.8.0.5_dangbei.apk   qq音乐

}
