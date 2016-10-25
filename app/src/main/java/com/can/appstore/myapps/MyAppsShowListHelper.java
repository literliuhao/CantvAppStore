package com.can.appstore.myapps;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.can.appstore.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by wei on 2016/10/25.
 */

public  class MyAppsShowListHelper {

    private final Context context;
    private Map<String,AppInfo> allapps = new ArrayMap<String,AppInfo>();

    public MyAppsShowListHelper(Context  context) {
        this.context = context;
    }

    public  List<AppInfo>  getShowList(){
        List<AppInfo> mShowList = new ArrayList<AppInfo>(18);
        File file = new File("/data/data/com.can.appstore/files/myappsshowlist.txt");
        List<AppInfo>  allAppsList = getAllAppList();
      if(file.exists()){
          //存在，证明我在本地已写过过文件
          mShowList = getList();
      }else{
          //文件不存在，初次
          for (int i=0;i<allAppsList.size();i++){
              if(mShowList.size()<=16){
                  mShowList.add(allAppsList.get(i));
              }else{
                  break;
              }
          }
          saveShowList(mShowList);
      }
        mShowList.add(0,new AppInfo("全部应用", context.getResources().getDrawable(R.drawable.ic_launcher, null)));
        mShowList.add(1,new AppInfo("系统应用", context.getResources().getDrawable(R.drawable.ic_launcher,null)));

        if(mShowList.size()<18){
            mShowList.add(new AppInfo("添加应用", context.getResources().getDrawable(R.drawable.ic_launcher,null)));
        }


        return mShowList;
    }

    public  List<AppInfo> getAllAppList(){
        List<AppInfo> mAppsList = AppUtils.findAllInstallApkInfo(context);
        List<AppInfo> allAppslist = new ArrayList<AppInfo>();
        for (AppInfo  app : mAppsList){
            if(!app.isSystemApp){
                allAppslist.add(app);
                allapps.put(app.packageName,app);
            }
        }
        return allAppslist;
    }

    public void saveShowList(List<AppInfo>  list ){
        try {
            FileOutputStream outputStream =context.openFileOutput("myappsshowlist.txt",Context.MODE_PRIVATE);
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i<list.size();i++){
                if(TextUtils.isEmpty(list.get(i).packageName)){
                    continue;
                }
                builder.append(list.get(i).packageName);
                if(i==list.size()-1){
                    break;
                }
                builder.append("&");
            }
            outputStream.write(builder.toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<AppInfo> getList(){
        String listString = "";
        List<AppInfo> list = new ArrayList<AppInfo>();
        try {
            FileInputStream inputStream = context.openFileInput("myappsshowlist.txt");
            int lenght = inputStream.available();
            byte[] buffer = new byte[lenght];
            inputStream.read(buffer);
            listString = new String(buffer, "GB2312");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] split = listString.split("&");

        for (int i = 0;i< split.length;i++){
            list.add(allapps.get(split[i]));
        }
        return list;
    }




}
