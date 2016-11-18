package com.can.appstore.active;

/**
 * Created by Atangs on 2016/11/2.
 */

public class ActiveContract {
    interface TaskPresenter{
        void clickBtnDownload();

        void removeAllListener();

        void requestActiveData(String id);
    }

    interface OperationView{
        void refreshProgressbarProgress(float progress);

        void refreshTextProgressbarTextStatus(String status);

        void showToast(String toastContent);

        void loadwebview(String url);

        void setNativeLayout(String url);

        void showNetworkRetryView(boolean isRetry, boolean isWebView);
    }
}
