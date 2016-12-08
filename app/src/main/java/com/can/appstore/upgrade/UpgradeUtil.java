package com.can.appstore.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import cn.can.downloadlib.utils.SdcardUtils;
import cn.can.tvlib.utils.ShellUtils;
import cn.can.tvlib.utils.StringUtils;

/**
 * Created by syl on 2016/11/3.
 */

public class UpgradeUtil {
    public static String getFileMD5(String file) {
        if (file == null) {
            return "";
        }
        File f = new File(file);
        if (!f.exists()) {
            return "";
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        // String mdval = bigInt.toString(16);
        String mdval = String.format("%1$032x", bigInt);
        return mdval;
    }

    public static String getFileMD5(File file) {
        if (!file.exists()) {
            return "";
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        // String mdval = bigInt.toString(16);
        String mdval = String.format("%1$032x", bigInt);
        return mdval;
    }

    // 删除指定文件夹下所有文件
    // param path 文件夹完整绝对路径
    public static boolean delAllDateFile(String path) {
        boolean flag = false;
        String allPath = path;
        File file = new File(allPath);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            file.delete();
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (allPath.endsWith(File.separator)) {
                temp = new File(allPath + tempList[i]);
            } else {
                temp = new File(allPath + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllDateFile(allPath + "/" + tempList[i]);// 先删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     * 判断给定的路径是一个文件
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 创建目录
     *
     * @param dirName
     */
    public static File creatDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdirs();
        return dir;
    }


    /**
     * 静默安装
     */
    public static void installApk(Context mContext, String path, long size, InstallApkListener onInstallApkListener) {
        long space = SdcardUtils.getSDCardAvailableSpace();
        if (space < size) {
            onInstallApkListener.onInstallFail(mContext.getResources().getString(cn.can.downloadlib.R.string
                    .error_msg));
            return;
        }
        Log.d("", "path: " + path);
        ShellUtils.CommandResult res = ShellUtils.execCommand("pm install -r " + path, false);
        Log.d("", "inStallApk: " + res.result + "----" + res.errorMsg);
        onInstallApkListener.onInstallFail(mContext.getResources().getString(cn.can.downloadlib.R.string
                .error_install));
    }

    /**
     * 系统安装apk
     */
    public static void install(Context mContext, String path, long size, InstallApkListener onInstallApkListener) {
        long space = SdcardUtils.getSDCardAvailableSpace();
        if (space < size) {
            onInstallApkListener.onInstallFail(mContext.getResources().getString(cn.can.downloadlib.R.string
                    .error_msg));
            return;
        }
        //创建URI
        Uri uri = Uri.fromFile(new File(path));
        //创建Intent意图
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//启动新的activity
        //设置Uri和类型
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //执行安装
        mContext.startActivity(intent);

    }

}
