package cn.can.tvlib.utils;


import android.content.Context;
import android.text.format.Formatter;

/**
 * Created by zhangbingyuan on 2016/10/26.
 */

public class FormatUtil {

    public static String formatFileSize(Context context, long sizeInBytes){
        return Formatter.formatFileSize(context, sizeInBytes);
    }

    public static String formatShortFileSize(Context context, long sizeInBytes){
        return Formatter.formatShortFileSize(context, sizeInBytes);
    }



}
