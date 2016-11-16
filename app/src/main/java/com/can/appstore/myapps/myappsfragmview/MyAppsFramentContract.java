
package com.can.appstore.myapps.myappsfragmview;

import com.can.appstore.myapps.model.AppInfo;

import java.util.List;

/**
 * Created by wei on 2016/11/9.
 */

public interface MyAppsFramentContract {
    interface Presenter{
        void startLoad();

        void addListener();

        void release();

        void  saveShowList(List<AppInfo>  list);
    }

    interface View{

        void loadAddAppInfoSuccess(List<AppInfo> infoList);

    }

}
