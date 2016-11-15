package com.can.appstore.download;

import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;

import java.util.List;

import cn.can.downloadlib.DownloadTask;

/**
 * Created by laiforg on 2016/10/31.
 */

public interface DownloadContract {

    interface DownloadPresenter extends BasePresenter{

        void loadData();
        void calculateRowNum(int focusedPos);
        void deleteAllTasks();
        boolean pauseAllTasks();
        boolean resumeAllTasks();
    }

    interface DownloadView extends BaseView<DownloadPresenter>{

        void onDataLoaded(List<DownloadTask> tasks);
        void refreshRowNumber(String formatRow);
        void showNoDataView();
        void hideNoDataView();

    }

}
