package cn.can.tvlib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import java.util.List;

/**
 * 进程相关Util
 */
public class ProcessUtil {
    /**
     * 判断是否为主进程
     *
     * @return 如果是主进程则返回 <code>true</code>
     */
    public static boolean isMainProcess(Context context) {
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int pid = Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
            if (runningAppProcess.pid == pid) {
                return packageName.equals(runningAppProcess.processName);
            }
        }
        return false;
    }
}
