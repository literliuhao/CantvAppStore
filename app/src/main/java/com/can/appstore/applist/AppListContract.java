package com.can.appstore.applist;

import android.text.SpannableStringBuilder;

import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.Topic;

import java.util.List;

/**
 * Created by syl on 2016/10/19.
 */

public interface AppListContract {
    interface Presenter{
        void startLoadData();
        void onMenuItemSelect(int position);
        void loadMoreData();
        // TODO: 2016/11/11
        void loadAppListData();
        void loadAppListData(String topicId);
        void onAppListItemSelectChanged(int position);
        void release();
        int getAppListTotalSize();
    }
    interface View{
        void setPresenter(Presenter presenter);
        void showLoadingDialog();
        void hideLoadingDialog();
        void refreshMenuList(List<Topic> menuData, int focusPosition);
        void refreshAppList(List<AppInfo> rightData, int InsertPosition);
        // TODO: 2016/11/11  rowNum
        void refreshLineText(SpannableStringBuilder spannable);
        void refreshTypeName(String typeName);
        void onLoadFail();
    }

}
