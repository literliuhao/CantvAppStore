package com.can.appstore.applist;

import android.text.SpannableStringBuilder;

import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Topic;

import java.util.HashMap;
import java.util.List;

/**
 * Created by syl on 2016/10/19.
 */

public interface AppListContract {

    interface Presenter extends BasePresenter {
        void startLoadData();
        void loadMoreData();
        void loadAppListData();
        void onMenuItemSelect(int position);
        void onAppListItemSelectChanged(int position);
        HashMap getIds(int position);
    }

    interface View extends BaseView<Presenter> {
        void showSearchView();
        void showAppList();
        void showFailUI();
        void hideAppList();
        void hideFailUI();
        void refreshMenuList(List<Topic> menuData, int focusPosition);
        void refreshAppList(List<AppInfo> appListData);
        void refreshAppList(List<AppInfo> appListData, int InsertPosition);
        void refreshRowNumber(SpannableStringBuilder spannable);
        void refreshTypeName(String typeName);
        void finish();
    }

}
