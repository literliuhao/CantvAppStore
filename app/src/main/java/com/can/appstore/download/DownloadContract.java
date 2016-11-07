package com.can.appstore.download;

import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;

import java.util.List;

import cn.can.downloadlib.DownloadTask;

/**
 * Created by laiforg on 2016/10/31.
 */

public interface DownloadContract {

    public static interface DownloadPresenter extends BasePresenter{

        void loadData();
        void onItemFocused(int focusedPos);
        void deleteAllTasks();
        void pauseAllTasks();
        void resumeAllTasks();
    }

    public static interface DownloadView extends BaseView<DownloadPresenter>{

        void onDataLoaded(List<DownloadTask> tasks);
        void refreshRowNumber(String formatRow);
        void showNoDataView();
        void hideNoDataView();

    }

}
