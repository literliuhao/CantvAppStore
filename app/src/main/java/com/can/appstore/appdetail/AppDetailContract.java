package com.can.appstore.appdetail;

import com.can.appstore.entity.AppInfo;

/**
 * Created by JasonF on 2016/10/25.
 */

public class AppDetailContract {

    interface Presenter {
        void startLoad();

        void clickStartDownload(boolean isUpdateButton);

        void addBroadcastReceiverListener();

        void addDownlaodListener();

        void release();
    }

    interface View {
        void showLoading();

        void hideLoading();

        void loadDataFail();

        void onClickHomeKey();

        void loadAppInfoOnSuccess(AppInfo appInfo);

        void refreshDownloadButtonStatus(int status, float progress);

        void refreshUpdateButtonStatus(int status, float progress);

        void refreshUpdateButton(boolean isShow);
    }
}
