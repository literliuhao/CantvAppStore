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

        /**
         * 无数据
         */
        void showNoData();

        void hideNoData();

        /**
         * 开启自动更新
         */
        void showStartAutoUpdate();

        void showSDProgressbar(int currentsize, int total, String sdinfo);

        /**
         * 刷新单个条目
         */
        void refreshItem(int position);

        /**
         * 刷新集合
         */
        void refreshAll();

        void showCurrentNum(int current, int total);

        void showInstallPkgList(List<AppInfoBean> mDatas);

        /**
         * 刷新更新状态按钮
         */
//        void refreshUpdateButton(String status);

        /**
         * 刷新更新进度
         */
//        void refreshUpdateProgress(int progress, boolean visible);

    }

    //业务接口
    interface Presenter {
        /**
         * 获取集合
         */
        void getInstallPkgList(boolean isAutoUpdate);

        /**
         * 刷新存储信息数据
         */
        void getSDInfo();

        /**
         * 刷新集合
         */
        void refreshInstallPkgList();

        /**
         * 获取当前集合大小
         */
        void getListSize();

        /**
         * 清空集合
         */
        void clearList();

        /**
         * 释放资源
         */
        void release();
    }

    //更新进度view
    interface UpdateView {
        /**
         * 刷新更新状态按钮
         */
        void refreshUpdateButton(String status,boolean visible);

        /**
         * 刷新更新进度
         */
        void refreshUpdateProgress(int progress, boolean visible);
        /**
         * 刷新更新标
         */
        void refreshUpdateIcon();
        /**
         * 完成时刻
         */
        void showCompleted(String text);

    }
}
