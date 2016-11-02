package com.can.appstore.myapps.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.can.appstore.R;

import java.util.ArrayList;
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
    public  List<AppInfo>  getShowList(){
        List<AppInfo> mShowList = new ArrayList<AppInfo>(18);
//        File file = new File("/data/data/com.can.appstore/files/myappsshowlist.txt");
        List<AppInfo>  allAppsList = getAllAppList();
      if( !PreferencesUtils.getString(context,"myappsshowlist","0").equals("0")){
          //存在，证明我在本地已写过过文件
          mShowList = getList();
          if(mShowList.size() < 16){
              mShowList.add(new AppInfo("添加应用", context.getResources().getDrawable(R.drawable.ic_launcher)));
          }
      }else{
          //文件不存在，初次
          for (int i=0;i<allAppsList.size();i++){
              if(mShowList.size()<=16){
                  mShowList.add(allAppsList.get(i));
              }else{
                  break;
              }
          }
          if(mShowList.size()<allAppsList.size()){
              mShowList.add(new AppInfo("添加应用", context.getResources().getDrawable(R.drawable.ic_launcher)));
          }
          saveShowList(mShowList);
      }
        mShowList.add(0,new AppInfo("全部应用", context.getResources().getDrawable(R.drawable.ic_launcher)));
        mShowList.add(1,new AppInfo("系统应用", context.getResources().getDrawable(R.drawable.ic_launcher)));
        return mShowList;
    }

    /**
     * 全部应用Activity显示的列表，本地已安装的所有非系统应用
     * 在内存维护List
     * @return
     */
    public  List<AppInfo> getAllAppList(){
        List<AppInfo> mAppsList = AppUtils.findAllInstallApkInfo(context);
        List<AppInfo> allAppslist = new ArrayList<AppInfo>();
//        TreeMap<long,AppInfo> map = new TreeMap<>();
        for (AppInfo  app : mAppsList){
            if(!app.isSystemApp){
                allAppslist.add(app);
                allapps.put(app.packageName,app);
            }
        }
//        for (AppInfo  app :allAppslist) {
//            map.put(app.installTime,app);
//        }
//        for (int i = 0; i<map.size();i++){
//            allAppslist.add(map.);
//        }
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

    public List<AppInfo> getList(){
        String listString = PreferencesUtils.getString(context,"myappsshowlist");
        List<AppInfo> list = new ArrayList<AppInfo>();
        String[] split = listString.split("&");

        for (int i = 0;i< split.length;i++){
            if(allapps.containsKey(split[i])){
                list.add(allapps.get(split[i]));
            }
        }
        return list;
    }

    public List<AppInfo>  getAddActivityList(){

        return null;
    }



}
