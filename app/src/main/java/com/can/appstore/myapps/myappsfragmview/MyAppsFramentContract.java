
package com.can.appstore.myapps.myappsfragmview;


import android.graphics.drawable.Drawable;

import java.util.List;

import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

/**
 * Created by wei on 2016/11/9.
 */

public interface MyAppsFramentContract {
    interface Presenter{
        void startLoad();

        void addListener();

        void release();

        void  saveShowList(List<PackageUtil.AppInfo>  list);
    }

    interface View{

        void loadAddAppInfoSuccess(List<AppInfo> showList, List<Drawable> mDraebleList);
    }

}
