package cn.can.tvlib.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * Created by zhangbingyuan on 2016/8/31.
 */

public class SharedPreferencesUtils {

    public static String SF_NAME = "appConfig";

    public static boolean hasKey(Context context, String key){
        return context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE).contains(key);
    }

    public static boolean putString(Context context, String key, String value){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.edit().putString(key, value).commit();
    }

    public static boolean putInt(Context context, String key, int value){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.edit().putInt(key, value).commit();
    }

    public static boolean putLong(Context context, String key, long value){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.edit().putLong(key, value).commit();
    }

    public static boolean putBoolean(Context context, String key, boolean value){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.edit().putBoolean(key, value).commit();
    }

    public static boolean putFloat(Context context, String key, float value){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.edit().putFloat(key, value).commit();
    }

    public static boolean putStringSet(Context context, String key, Set<String> value){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.edit().putStringSet(key, value).commit();
    }

    public static String getString(Context context, String key, String defValue){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.getString(key, defValue);
    }

    public static int getInt(Context context, String key, int defValue){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.getInt(key, defValue);
    }

    public static long getLong(Context context, String key, long defValue){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.getLong(key, defValue);
    }

    public static float getFloat(Context context, String key, float defValue){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.getFloat(key, defValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.getBoolean(key, defValue);
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defValue){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.getStringSet(key, defValue);
    }

    public static Map<String, ?> getAll(Context context, String key){
        SharedPreferences sf = context.getApplicationContext().getSharedPreferences(SF_NAME, Context.MODE_PRIVATE);
        return sf.getAll();
    }

}
