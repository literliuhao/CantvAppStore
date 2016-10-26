package cn.can.tvlib.utils;

import android.content.ContentResolver;
import android.content.Context;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by zhangbingyuan on 2016/10/24.
 */

public class DateUtil {

    public static final String FORMAT_HOUR_MINUTE_SECOND = "HH:mm:ss";
    public static final String FORMAT_24HOURS  = "HH:mm";
    public static final String FORMAT_12HOURS  = "hh:mm";
    public static final String FORMAT_YEAR_MONTH_DAY = "yy-MM-dd";
    public static final String FORMAT_DATE_TIME = "yy-MM-dd HH:mm";

    public static final String PREFIX_AM = "上午  ";
    public static final String PREFIX_PM = "下午  ";

    /**
     * 格式化时间
     * @param timeInMillis
     * @param format
     * @return
     */
    public static String format(long timeInMillis, String format){
        return new SimpleDateFormat(format).format(timeInMillis);
    }

    /**
     * 获取当前系统时间
     * @param context
     * @param prefixIfNeed
     * @return
     */
    public static String getSystemTime(Context context, boolean prefixIfNeed){
        if(is24HoursInSystem(context)){
            return formatTo24Hours(System.currentTimeMillis());
        } else {
            return formatTo12Hours(System.currentTimeMillis(), prefixIfNeed);
        }
    }

    /**
     * 获取网络时间
     * @param context
     * @param prefixIfNeed
     * @return
     */
    public static String getNetworkTime(Context context, boolean prefixIfNeed){
        long timeInMillis = 0;
        try {
            URLConnection uc = new URL("http://www.baidu.com").openConnection();// 生成连接对象
            uc.connect();// 发出连接
            timeInMillis = uc.getDate();// 读取网站日期时间,精度是毫秒值
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(is24HoursInSystem(context.getApplicationContext())){
            return formatTo24Hours(timeInMillis);
        } else {
            return formatTo12Hours(timeInMillis, prefixIfNeed);
        }
    }

    /**
     * 格式化时间为24小时制
     * @param timeInMillis
     * @return
     */
    public static String formatTo24Hours(long timeInMillis){
        return new SimpleDateFormat(FORMAT_24HOURS).format(timeInMillis);
    }

    /**
     * 格式化时间为12小时制
     * @param timeInMillis
     * @param needPrefix  是否添加“上午”或“下午”前缀
     * @return
     */
    public static String formatTo12Hours(long timeInMillis, boolean needPrefix){
        String format = new SimpleDateFormat(FORMAT_12HOURS).format(timeInMillis);
        if(needPrefix){
            Calendar calendar = Calendar.getInstance();
            int apm = calendar.get(Calendar.AM_PM);
            if(apm == Calendar.AM){
                format = PREFIX_AM + format;
            } else {
                format = PREFIX_PM + format;
            }
            return format;
        } else {
            return format;
        }
    }

    /**
     * 判断系统当前是否为24小时制
     * @param context
     * @return
     */
    public static boolean is24HoursInSystem(Context context){
        boolean isSystemTime24 = false;
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat == null) {
            isSystemTime24 = android.text.format.DateFormat.is24HourFormat(context);
        } else if (strTimeFormat.equals("24")) {
            isSystemTime24 = true;
        }
        return isSystemTime24;
    }

}
