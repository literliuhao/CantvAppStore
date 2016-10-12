package com.can.appstore.search;

/**
 * Created by yibh on 2016/10/12 14:33 .
 */

public interface SearchContract {
    interface View {
        void startSearch();

        void delContent();

        void clearContent();
    }

    interface Presenter {
        void getSearchList();
    }
}
