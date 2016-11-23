package com.can.appstore.search;

import java.util.List;

/**
 * Created by yibh on 2016/10/12 14:33 .
 */

public interface SearchContract {
    interface View {
        void startSearch();

        void delContent();

        void clearContent();

        void getAppList(List list);

        void getInitials(String con);   //首字母

        void getHotRecomAppList(List list); //热门推荐

        void getHotKeyList(List list); //大家都在搜

        void noNetWork();
    }

    interface Presenter {
        void getSearchList(String searCon, int pageIndex);

        void getDefaultList();
    }
}
