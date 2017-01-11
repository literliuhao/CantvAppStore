package com.can.appstore.myapps.utils;

import android.content.Context;
import android.text.TextUtils;

import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.index.model.ShareData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil;
import cn.can.tvlib.common.pm.PackageUtil.AppInfo;
import cn.can.tvlib.common.storage.PreferencesUtils;


/**
 * Created by wei on 2016/10/25.
 */

public class MyAppsListDataUtil {
    private static final String MY_APPS_SHOW_LIST = "myappsshowlist";
    public static final int ATMOST_SHOW_THIRDAPP_COUNT = 16;
    //隐藏应用
    public static List<String> hideList = null;
    private ShareData mShareData;
    private final Context context;

    public MyAppsListDataUtil(Context context) {
        this.context = context;
    }

    /**
     * 主页：我的应用显示的列表，可编辑。所有第三方应用中的一部分
     * 除去黑名单
     * 列表数据已包名拼接字符串的形式存在SP文件中
     * 首次：SP存在，获取本地所有应用最多添加16个
     *
     * @return
     */
    public List<PackageUtil.AppInfo> getShowList(List<AppInfo> mShowList, List<AppInfo> allAppsList) {
        if (mShowList == null) {
            mShowList = new ArrayList<>();
        } else {
            mShowList.clear();
        }
        if (allAppsList == null) {
            allAppsList = PackageUtil.findAllThirdPartyApps(context, allAppsList);
        }
        if (!PreferencesUtils.getString(context, MY_APPS_SHOW_LIST, "0").equals("0")) {
            //存在，证明我在本地已写过过文件
            mShowList = getList(mShowList);
        } else {
            //文件不存在，初次
            if (allAppsList.size() <= ATMOST_SHOW_THIRDAPP_COUNT) {
                mShowList.addAll(allAppsList);
            } else {
                for (int i = 0; i < ATMOST_SHOW_THIRDAPP_COUNT; i++) {
                    mShowList.add(allAppsList.get(i));
                }
            }
            saveShowList(mShowList);
        }
        mShowList.add(0, new AppInfo(context.getString(R.string.all_apps), context.getResources().getDrawable(R.drawable.allapp)));
        mShowList.add(1, new AppInfo(context.getString(R.string.system_apps), context.getResources().getDrawable(R.drawable.ic_launcher)));
        return mShowList;
    }

    /**
     * 全部应用Activity显示的列表
     * 本地已安装的所有非系统应用 + 白名单中的系统应用
     *
     * @return
     */
    public List<AppInfo> getAllAppList(List<AppInfo> allAppslist) {
        if (allAppslist == null) {
            allAppslist = new ArrayList<>();
        } else {
            allAppslist.clear();
        }
        allAppslist = PackageUtil.findAllComplexAppsNoDelay(context, allAppslist, AppConstants.PRE_APPS);
        ComparatorAppInfo comparatorAppInfo = new ComparatorAppInfo();
        Collections.sort(allAppslist, comparatorAppInfo);

        return allAppslist;
    }

    /**
     * 主页我的应用页，编辑后保存到本地
     * 生成我的应用页面显示的所有item（除添加）
     *
     * @param list
     */
    public void saveShowList(List<AppInfo> list) {
        String string = "";
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.isEmpty(list.get(i).packageName)) {
                continue;
            }
            string += (list.get(i).packageName);
            if (i == list.size() - 1) {
                break;
            }
            string += ("&");
        }
        PreferencesUtils.putString(context, MY_APPS_SHOW_LIST, string);
    }

    //获取本地存的我的应用也显示的应用集合
    public List<AppInfo> getList(List<AppInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        String listString = PreferencesUtils.getString(context, MY_APPS_SHOW_LIST);
        String[] split = listString.split("&");

        for (int i = 0; i < split.length; i++) {
            AppInfo info = PackageUtil.getAppInfo(context, split[i]);
            if (info != null) {
                list.add(info);
            }
        }
        return list;
    }


    /**
     * 获取全部白名单应用,必须是APK存在的
     * 文件管理器
     * 微信相册等
     */
    public List<AppInfo> getSystemApp(List<AppInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        for (int i = 0; i < AppConstants.PRE_APPS.size(); i++) {
            AppInfo appInfo = PackageUtil.getAppInfo(context, AppConstants.PRE_APPS.get(i));
            //如果该应用已卸载，返回为空
            if (appInfo != null) {
                list.add(appInfo);
            }
        }
        return list;
    }

    /**
     * 从列表里删除隐藏应用
     */
    public List<AppInfo> removeHideApp(List<AppInfo> appList) {
        mShareData = ShareData.getInstance();
        hideList = mShareData.getHiddenApps(hideList);
        if (appList == null) {
            return null;
        }
        if (hideList == null || hideList.size() == 0) {
            return appList;
        }
        if (hideList.size() != 0) {
            for (int i = appList.size() - 1; i >= 0; i--) {
                AppInfo appInfo = appList.get(i);
                for (int j = hideList.size() - 1; j >= 0; j--) {
                    if (hideList.get(j).equals(appInfo.packageName)) {
                        //  hideList.remove(j);
                        appList.remove(i);
                        break;
                    }
                }
            }
        }
        return appList;
    }

}
