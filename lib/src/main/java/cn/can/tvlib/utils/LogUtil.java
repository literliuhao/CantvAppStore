package cn.can.tvlib.utils;

import android.util.Log;

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：Log日志输出控制工具类
 * 修订历史：
 * ================================================
 */
public class LogUtil {

	public static String TAG = "LogUtil";

	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARNING = 4;
	public static final int ERROR = 5;

	public static int logLevel = VERBOSE;
	public static boolean isDebug = true;

	public static void setLogLevel(int logLevel) {
		LogUtil.logLevel = logLevel;
	}

	public static void v(String msg) {
		v(TAG, msg, null);
	}

	public static void d(String msg) {
		d(TAG, msg, null);
	}

	public static void i(String msg) {
		i(TAG, msg, null);
	}

	public static void w(String msg) {
		w(TAG, msg, null);
	}

	public static void e(String msg) {
		e(TAG, msg, null);
	}

	public static void v(String tag, String msg) {
		v(tag, msg, null);
	}

	public static void d(String tag, String msg) {
		d(tag, msg, null);
	}

	public static void i(String tag, String msg) {
		i(tag, msg, null);
	}

	public static void w(String tag, String msg) {
		w(tag, msg, null);
	}

	public static void e(String tag, String msg) {
		e(tag, msg, null);
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (isDebug && VERBOSE >= logLevel) {
			Log.v(tag, generateLogMsg(msg), tr);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (isDebug && DEBUG >= logLevel) {
			Log.d(tag, generateLogMsg(msg), tr);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (isDebug && INFO >= logLevel) {
			Log.i(tag, generateLogMsg(msg), tr);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (isDebug && WARNING >= logLevel) {
			Log.w(tag, generateLogMsg(msg), tr);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (isDebug && ERROR >= logLevel) {
			Log.e(tag, generateLogMsg(msg), tr);
		}
	}
	
	private static String generateLogMsg(String msg){
		return msg;
	}
}
