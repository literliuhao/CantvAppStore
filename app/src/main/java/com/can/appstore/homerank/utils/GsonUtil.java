package com.can.appstore.homerank.utils;

import com.google.gson.Gson;

/**
 * Created by yibh on 2016/10/17 16:55 .
 */

public class GsonUtil {
    public static <T> T jsonToBean(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }
}
