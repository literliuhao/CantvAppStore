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
        void onMenuItemSelect(int position);
        void loadMoreData();
        // TODO: 2016/11/11
        void loadAppListData();
        void loadAppListData(String topicId);
        void onAppListItemSelectChanged(int position);
        HashMap getIds(int position);
    }

    interface View extends BaseView<Presenter> {
        void hideAppInfoLoadingDialog();
        void showSearchView();
        void refreshMenuList(List<Topic> menuData, int focusPosition);
        void refreshAppList(List<AppInfo> rightData, int InsertPosition,long delayTime);
        void refreshRowNumber(SpannableStringBuilder spannable);
        void changeAppInfoUiToFail();
        void refreshTypeName(String typeName);
        void finish();
    }

}
