package com.can.appstore.active;

/**
 * Created by Atangs on 2016/11/2.
 */

public class ActiveContract {
    interface TaskPresenter{
        void clickBtnDownload();
    }

    interface OperationView{
        void refreshProgressbarProgress(float progress);

        void refreshTextProgressbarTextStatus(String status);

        void showToast(String toastContent);

        void loadwebview(String url);

        void setLayoutBackground(String url);
    }
}
