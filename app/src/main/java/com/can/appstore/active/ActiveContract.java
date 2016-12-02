package com.can.appstore.active;

/**
 * Created by Fuwen on 2016/11/2.
 */

public class ActiveContract {
    interface TaskPresenter{
        void clickBtnDownload();

        void requestActiveData(String id);

        void release();

    }

    interface OperationView{
        void refreshProgressbarProgress(float progress);

        void refreshTextProgressbarTextStatus(int status);

        void showActiveToast(int toastStrId);

        void loadwebview(String url);

        void showBackground(String url);

        void showProgreessbar();

        void loadDataFail(int toastId);

    }
}
