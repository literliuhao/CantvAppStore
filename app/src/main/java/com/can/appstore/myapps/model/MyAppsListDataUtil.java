package com.can.appstore.myapps.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.can.appstore.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cn.can.tvlib.utils.PreferencesUtils;


/**
 * Created by wei on 2016/10/25.
 */

public  class MyAppsListDataUtil {

    private final Context context;
    private Map<String,AppInfo> allapps = new ArrayMap<String,AppInfo>();

    public MyAppsListDataUtil(Context  context) {
        this.context = context;
    }

    /**
     * 主页：我的应用显示的列表，可编辑。列表数据已包名拼接字符串的形式存在SP文件中
     *      首次：SP存在，获取本地所有应用最多添加16个
     *
     * @return
     */
    public  List<AppInfo>  getShowList(List<AppInfo> mShowList){
        if(mShowList == null){
            mShowList = new ArrayList<AppInfo>(18);
        }else{
            mShowList.clear();
        }
        List<AppInfo>  allAppsList = getAllAppList(null);
      if( !PreferencesUtils.getString(context,"myappsshowlist","0").equals("0")){
          //存在，证明我在本地已写过过文件
          mShowList = getList(mShowList);
          if(mShowList.size() < 16 && allAppsList.size()>mShowList.size()){
              mShowList.add(new AppInfo("添加应用", context.getResources().getDrawable(R.drawable.addapp_icon)));
          }

      }else{
          //文件不存在，初次
          if(allAppsList.size()<=16){
              mShowList = allAppsList;
          }else{
              for (int i=0;i<15;i++){
                  mShowList.add(allAppsList.get(i));
              }
          }
          saveShowList(mShowList);
      }
        mShowList.add(0,new AppInfo("全部应用", context.getResources().getDrawable(R.drawable.allapp)));
        mShowList.add(1,new AppInfo("系统应用", context.getResources().getDrawable(R.drawable.ic_launcher)));
        return mShowList;
    }

    /**
     * 全部应用Activity显示的列表，本地已安装的所有非系统应用
     * 在内存维护List
     * @return
     */
    public  List<AppInfo> getAllAppList(List<AppInfo> allAppslist){
        List<AppInfo> mAppsList = AppUtils.findAllInstallApkInfo(context);
        if(allAppslist == null){
            allAppslist = new ArrayList<AppInfo>();
        }else{
            allAppslist.clear();
        }
        for (AppInfo  app : mAppsList){
            if(!app.isSystemApp){
                allAppslist.add(app);
                allapps.put(app.packageName,app);
            }
        }
        ComparatorAppInfo  comparatorAppInfo = new ComparatorAppInfo();
        Collections.sort(allAppslist,comparatorAppInfo);
        return allAppslist;
    }

    /**
     * 主页我的应用页，编辑后保存到本地
     * @param list
     */
    public void saveShowList(List<AppInfo>  list ){
            String string  = "";
            for(int i = 0; i<list.size();i++){
                if(TextUtils.isEmpty(list.get(i).packageName)){
                    continue;
                }
                string += (list.get(i).packageName);
                if(i==list.size()-1){
                    break;
                }
                string += ("&");
            }
        PreferencesUtils.putString(context,"myappsshowlist",string);
    }

    public List<AppInfo> getList(List<AppInfo>  list){
        if(list == null){
            list = new ArrayList<AppInfo>();
        }else{
            list.clear();
        }
        String listString = PreferencesUtils.getString(context,"myappsshowlist");
        String[] split = listString.split("&");

        for (int i = 0;i< split.length;i++){
            if(allapps.containsKey(split[i])){
                list.add(allapps.get(split[i]));
            }
        }
        return list;
    }


    /**
     * 全部应用排序
     */
    private class ComparatorAppInfo implements Comparator<AppInfo>{

        @Override
        public int compare(AppInfo o1, AppInfo o2) {
            if(o1.installTime == o2.installTime) return 0;
            if(o1.installTime < o2.installTime)return 1;
            return -1;
        }
    }


    /**
     * 获取全部系统应用
     * 文件管理器
     * 微信相册等
     */

    public List<AppInfo> getSystemApp(){
        List<AppInfo> list = new ArrayList<AppInfo>();

        return list;
    }

}
