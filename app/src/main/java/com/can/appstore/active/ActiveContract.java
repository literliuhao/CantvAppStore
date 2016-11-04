package com.can.appstore.active;

/**
 * Created by Atangs on 2016/11/2.
 */

public class ActiveContract {
    interface TaskPresenter{
        //初始化下载任务，以及下载状态
        void initDownloadTask();

        void startDownload();
    }

    interface OperationView{
        void refreshProgressbarProgress(float progress);

        void refreshTextProgressbarTextStatus(String status);

        void showToast(String toastContent);
    }
}
