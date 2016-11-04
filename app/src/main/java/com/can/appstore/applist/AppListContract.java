package com.can.appstore.applist;

import android.text.SpannableStringBuilder;

import java.util.List;

/**
 * Created by 4 on 2016/10/19.
 */

public interface AppListContract {
    interface Presenter{
        void startLoadData();
        void onMenuItemSelect(int position);
        void loadMoreData();
        void loadAppListData();
        void onAppListItemSelectChanged(int position);
        int getAppListTotalSize();
        void relese();
    }
    interface View{
        void setPresenter(Presenter presenter);
        void showLoadingDialog();
        void hideLoadingDialog();
        void refreshMenuList(List<AppListMenuInfo> menuData,int focusPosition);
        void refreshAppList(List<AppListInfo> rightData,int InsertPosition);
        void refreshLineText(SpannableStringBuilder spannable);
        void onLoadFail();
    }

}
