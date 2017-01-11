package com.can.appstore.installpkg;

import com.can.appstore.update.model.AppInfoBean;

import java.util.List;

/**
 * Created by shenpx on 2016/11/9 0009.
 */

public interface InstallContract {

    //控件接口
    interface View {
        void showLoading();

        void hideLoading();

        void showNoData();

        void hideNoData();

        void showSDProgressbar(int currentsize,String sdinfo);

        void refreshItem(int position);

        void removeItem(int position);

        void refreshAll();

        void showCurrentNum(int current, int total);

        void showInstallPkgList(List<AppInfoBean> mDatas);

        void deleteLastItem(int position);

    }

    //业务接口
    interface Presenter {
        void getInstallPkgList();

        void getSDInfo();

        void deleteAll();

        void deleteInstall();

        void deleteOne(int positionn);

        void refreshInstallPkgList();

        void release();
    }
}
