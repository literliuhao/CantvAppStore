package cn.can.tvlib.utils;

import java.text.SimpleDateFormat;

/**
 * Created by zhangbingyuan on 2016/10/24.
 */

public class DateUtil {

    public static String formatTime(long timeInMillis, String format){
        return new SimpleDateFormat(format).format(timeInMillis);
    }

    public static String formatTime2Custom(long timeInMillis){
        return formatTime(timeInMillis, "HH:mm");
    }

    public static String getCurrentCustomTime(){
        return formatTime(System.currentTimeMillis(), "HH:mm");
    }


}
