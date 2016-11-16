package com.can.appstore.appdetail;

import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;
import com.can.appstore.entity.AppInfo;

/**
 * Created by JasonF on 2016/10/25.
 */

public interface AppDetailContract {

    interface Presenter extends BasePresenter{
        void startLoad();

        void clickStartDownload(boolean isUpdateButton);

        void addBroadcastReceiverListener();

        void addDownlaodListener();
    }

    interface View extends BaseView<Presenter>{
        void loadDataFail();

        void onClickHomeKey();

        void loadAppInfoOnSuccess(AppInfo appInfo);

        void refreshDownloadButtonStatus(int status, float progress);

        void refreshUpdateButtonStatus(int status, float progress);

        void refreshUpdateButton(boolean isShow);
    }
}
