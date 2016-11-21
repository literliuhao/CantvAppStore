package com.can.appstore.myapps.model;

import android.content.Context;
import android.text.TextUtils;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.entity.ListResult;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtil.AppInfo;
import cn.can.tvlib.utils.PreferencesUtils;
import retrofit2.Response;


/**
 * Created by wei on 2016/10/25.
 */

public class MyAppsListDataUtil {

    private final Context context;

    public MyAppsListDataUtil(Context context) {
        this.context = context;
    }

    /**
     * 主页：我的应用显示的列表，可编辑。所有第三方应用中的一部分
     * 列表数据已包名拼接字符串的形式存在SP文件中
     * 首次：SP存在，获取本地所有应用最多添加16个
     *
     * @return
     */
    public List<PackageUtil.AppInfo> getShowList(List<AppInfo> mShowList) {
        if (mShowList == null) {
            mShowList = new ArrayList<AppInfo>(18);
        } else {
            mShowList.clear();
        }
        List<AppInfo> allAppsList = new ArrayList<>();
        allAppsList = PackageUtil.findAllThirdPartyApps(context, allAppsList);
        if (!PreferencesUtils.getString(context, "myappsshowlist", "0").equals("0")) {
            //存在，证明我在本地已写过过文件
            mShowList = getList(mShowList);
        } else {
            //文件不存在，初次
            if (allAppsList.size() <= 16) {
                mShowList = allAppsList;
            } else {
                for (int i = 0; i < 15; i++) {
                    mShowList.add(allAppsList.get(i));
                }
            }
            saveShowList(mShowList);
        }
        mShowList.add(0, new AppInfo("全部应用", context.getResources().getDrawable(R.drawable.allapp)));
        mShowList.add(1, new AppInfo("系统应用", context.getResources().getDrawable(R.drawable.ic_launcher)));
        return mShowList;
    }

    /**
     * 全部应用Activity显示的列表
     * 本地已安装的所有非系统应用和在白名单中的系统应用
     *
     * @return
     */
    public List<AppInfo> getAllAppList(List<AppInfo> allAppslist) {
        if (allAppslist == null) {
            allAppslist = new ArrayList<AppInfo>();
        } else {
            allAppslist.clear();
        }
        allAppslist = PackageUtil.findAllComplexApps(context, allAppslist, MyApp.PRE_APPS);
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
        PreferencesUtils.putString(context, "myappsshowlist", string);
    }

    //获取本地存的我的应用也显示的应用集合
    public List<AppInfo> getList(List<AppInfo> list) {
        if (list == null) {
            list = new ArrayList<AppInfo>();
        } else {
            list.clear();
        }
        String listString = PreferencesUtils.getString(context, "myappsshowlist");
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
     * 全部应用排序
     */
    private class ComparatorAppInfo implements Comparator<AppInfo> {
        @Override
        public int compare(AppInfo o1, AppInfo o2) {
            if (o1.installtime == o2.installtime)
                return 0;
            if (o1.installtime < o2.installtime)
                return 1;
            return -1;
        }
    }


    /**
     * 获取全部系统应用,必须是APK存在的
     * 文件管理器
     * 微信相册等
     */
    public List<AppInfo> getSystemApp(List<AppInfo> list) {
        if (list == null) {
            list = new ArrayList<AppInfo>();
        } else {
            list.clear();
        }
        for (int i = 0; i < MyApp.PRE_APPS.size(); i++) {
            AppInfo appInfo = PackageUtil.getAppInfo(context, MyApp.PRE_APPS.get(i));
            if (appInfo != null) {
                list.add(appInfo);
            }
        }
        return list;
    }

    /**
     * 获取可添加到桌面的应用
     * 在全部第三方应用中，不在我的应用桌面
     */
    public List<AppInfo> getAddApp(List<AppInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }


        return list;
    }

    /**
     * 获取 隐藏应用
     */
    public static List<String> hideList = new ArrayList<>();

    public void getHideApps() {
        CanCall<ListResult<String>> hiddenApps = HttpManager.getApiService().getHiddenApps();
        hiddenApps.enqueue(new CanCallback<ListResult<String>>() {
            @Override
            public void onResponse(CanCall<ListResult<String>> call, Response<ListResult<String>> response) throws Exception {
                ListResult<String> body = response.body();
                hideList = (List<String>) body;
            }

            @Override
            public void onFailure(CanCall<ListResult<String>> call, CanErrorWrapper errorWrapper) {

            }
        });
    }

    /**
     * 从列表里删除隐藏应用
     */
    public List<AppInfo> removeHideApp(List<AppInfo> appList) {
        if (appList == null) {
            return null;
        }
        if (hideList == null || hideList.size() == 0) {
            return appList;
        }
        List<AppInfo> hideAppList = new ArrayList<>();
        for (AppInfo appInfo : appList) {
            boolean isHide = false;
            for (String s : hideList) {
                if (appInfo.packageName.equals(s)) {
                    isHide = true;
                }
            }
            if (isHide) {
                hideAppList.add(appInfo);
            }
        }
        appList.removeAll(hideAppList);
        return appList;
    }

}
