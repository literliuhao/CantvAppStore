package cn.can.downloadlib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by zhangbingyuan on 2016/10/26.
 */

public class SdcardUtils {

    /**
     * sdk版本号
     * @return
     */
    public static int getSdkVersion(){
        return Build.VERSION.SDK_INT;
    }

    /**
     * 内部存储空间总大小
     * @return 单位：Byte
     */
    public static long getInternalTotalSpace(){
        return new StatFs(Environment.getDataDirectory().getAbsolutePath()).getTotalBytes();
    }

    /**
     * 内部存储空间剩余大小
     * @return 单位：Byte
     */
    public static long getInternalAvailableSpace(){
        return new StatFs(Environment.getDataDirectory().getAbsolutePath()).getAvailableBytes();
    }

    /**
     * 内部存储空间信息
     * @return 长度为2的long型数组，分别为：内部存储空间总大小和剩余大小    单位：Byte
     */
    public static long[] getInternalSpaceInfo(){
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long[] spaceInfo = new long[2];
        spaceInfo[0] = statFs.getTotalBytes();
        spaceInfo[1] = statFs.getAvailableBytes();
        return spaceInfo;
    }

    /**
     * 是否有挂在sd卡
     * @return
     */
    public static boolean hasSDCard(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * SD卡存储空间总大小
     * @return 单位：Byte
     */
    public static long getSDCardTotalSpace(){
        return new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath()).getTotalBytes();
    }

    /**
     * SD卡存储空间剩余大小
     * @return 单位：Byte
     */
    public static long getSDCardAvailableSpace(){
        return new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath()).getAvailableBytes();
    }

    /**
     * SD卡存储空间信息
     * @return 长度为2的long型数组，分别为：内部存储空间总大小和剩余大小    单位：Byte
     */
    public static long[] getSDCardSpaceInfo(){
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long[] spaceInfo = new long[2];
        spaceInfo[0] = statFs.getTotalBytes();
        spaceInfo[1] = statFs.getAvailableBytes();
        return spaceInfo;
    }

    /**
     * 获取系统总内存
     * @return 单位：Byte
     */
    public static long getDeviceTotalMemory(){
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
     * 获取当前可用内存
     *
     * @param context
     * @return 单位：Byte
     */
    public static long getAvailableMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * 获取当前应用可被分配的最大内存
     *
     * @param context
     * @return 单位：Byte
     */
    public static long getTotalMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }














}
