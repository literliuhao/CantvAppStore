package com.can.appstore.myapps.utils;

import java.util.Comparator;

import cn.can.tvlib.utils.PackageUtil;

/**
 * Created by wei on 2016/11/22.
 */

public class ComparatorAppInfo implements Comparator<PackageUtil.AppInfo> {
    @Override
    public int compare(PackageUtil.AppInfo o1, PackageUtil.AppInfo o2) {
        if (o1.installtime == o2.installtime)
            return 0;
        if (o1.installtime < o2.installtime)
            return 1;
        return -1;
    }
}
