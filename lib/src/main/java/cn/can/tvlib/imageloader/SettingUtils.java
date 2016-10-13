package cn.can.tvlib.imageloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;


public class SettingUtils {

    /**
     * 清理缓存
     */
    public static void countDirSizeTask(final Context context,final CountDirSizeListener listener) {
        new Thread() {
            public void run() {
                final long result = getDirSize(context.getCacheDir());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult(result);
                    }
                });
            }
        }.start();
    }

    public static void clearAppCache(final Context context,final ClearCacheListener listener) {
        new Thread() {
            public void run() {
                clearCache(context.getCacheDir());
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult();
                    }
                });
            }
        }.start();
    }

    public interface ClearCacheListener {
        void onResult();
    }

    public interface CountDirSizeListener {
        void onResult(long result);
    }

    private static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }

        if (fileSizeString.startsWith(".")) {
            return "0B";
        }
        return fileSizeString;
    }

    private static void clearCache(File file) {
        clearFile(file);
    }

    private static void clearFile(File file) {
        if(file == null || !file.exists()){
            return;
        }
        if(file.isDirectory()) {
            for (File child : file.listFiles()) {
                clearFile(child);
            }
        } else {
            file.delete();
        }
    }

    private static int clearFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
        }
        return deletedFiles;
    }
}
