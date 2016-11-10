package com.can.appstore.update;

import com.can.appstore.update.model.AppInfoBean;

import java.util.List;

/**
 * Created by shenpx on 2016/11/9 0009.
 */

public interface UpdateContract {

    //控件接口
    interface View {
        /**
         * 显示Dialog
         */
        void showLoadingDialog();
        /**
         * 关闭Dialog
         */
        void hideLoadingDialog();
        //无数据
        void showNoData();

        void hideNoData();
        //开启自动更新
        void showStartAutoUpdate();

        void showSDProgressbar(int currentsize, int total, String sdinfo);
        //刷新单个条目
        void refreshItem(int position);
        //刷新集合
        void refreshAll();

        void showCurrentNum(int current, int total);

        void showInstallPkgList(List<AppInfoBean> mDatas);

    }

    //业务接口
    interface Presenter {
        //获取集合
        void getInstallPkgList(boolean isAutoUpdate);
        //刷新存储数据
        void getSDInfo();
        //刷新集合
        void refreshInstallPkgList();
        //获取当前集合大小
        void getListSize();
        //清空集合
        void clearList();
        //释放资源
        void release();
    }
}
