package cn.can.tvlib.utils;

import android.util.Log;
/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class L {
	public static boolean isDebug = true;
	public static String TAG = "TAG";

	// 下面四个是默认tag的函数
	public static void i(String msg) {
		if (isDebug) {
			Log.i(TAG, msg);
		}
	}

	public static void d(String msg) {
		if (isDebug) {
			Log.d(TAG, msg);
		}
	}

	public static void e(String msg) {
		if (isDebug) {
			Log.e(TAG, msg);
		}
	}

	public static void v(String msg) {
		if (isDebug) {
			Log.v(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}
}
