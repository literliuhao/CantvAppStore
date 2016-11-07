package com.can.appstore.appdetail;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.ShellUtils;
import cn.can.tvlib.utils.ShellUtils.CommandResult;

/**
 * Created by JasonF on 2016/10/13.
 */

public class AppUtils {
    private static long lastClickTime;

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
                continue;
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

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 转换文件大小
     *
     * @param size
     * @return
     */
    public static String FormetFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.availMem / 1024 / 1024;
    }

    /**
     * 获取SD卡总大小
     *
     * @return
     */
    public static long getSDTotalSize() {
        File sdcardDir = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(sdcardDir.getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalSize = statFs.getBlockCountLong();
        return blockSize * totalSize;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return 剩余多少M
     */
    public static long getSDAvaliableSize() {
        // 获取存储卡路径
        File path = Environment.getExternalStorageDirectory();
        // StatFs 看文件系统空间使用情况
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        return blockSize * availableBlocks;
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

    public static long getApkInstallDirSzie(Context context) {
        long usableSpace = context.getFilesDir().getAbsoluteFile().getUsableSpace();
        return usableSpace;
    }

    /**
     * 检测应用是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    /**
     * 按钮是否延时响应
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 静默安装
     */
    public static synchronized int installAPK(final String downloadPath) {
        Log.i("", "installAPK :  " + downloadPath);
        // new Thread() {
        // public void run() {
        // Process install = null;
        // try {
        // install = Runtime.getRuntime().exec("pm install -r " + downloadPath);
        // return install.waitFor();
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // if (install != null) {
        // install.destroy();
        // }
        // }
        // }
        // }.start();

        CommandResult res = ShellUtils.execCommand("pm install -r " + downloadPath, false);
        return res.result;
    }

    /**
     * 图片高斯模糊
     *
     * @param bitmap
     * @return
     */
    public static Drawable blurBitmap(Bitmap bitmap, Context ctx) {

        // Let's create an empty bitmap with the same size of the bitmap we want
        // to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(ctx);

        // Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // Create the Allocations (in/out) with the Renderscript and the in/out
        // bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        // Set the radius of the blur
        blurScript.setRadius(25.f);

        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        // recycle the original bitmap
        bitmap.recycle();

        // After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return new BitmapDrawable(ctx.getResources(), outBitmap);
    }

    /**
     * 获取当前页面的屏幕截图
     *
     * @param activity
     * @return
     */
    public static Bitmap getScreenShots(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    /**
     * dirName是输出的文件夹名称，filaName是输出的文件名，两者共同组成输出的路径，如“ /mnt/sdcard/pictures/shot.png”
     * @param bmp
     * @param dirName
     * @param fileName
     * @throws IOException
     */
    private void saveToSD(Bitmap bmp, String dirName,String fileName) throws IOException {
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(dirName);
            // 判断文件夹是否存在，不存在则创建
            if(!dir.exists()){
                dir.mkdir();
            }

            File file = new File(dirName + fileName);
            // 判断文件是否存在，不存在则创建
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                if (fos != null) {
                    // 第一参数是图片格式，第二个是图片质量，第三个是输出流
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    // 用完关闭
                    fos.flush();
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
