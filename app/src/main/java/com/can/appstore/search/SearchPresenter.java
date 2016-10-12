package com.can.appstore.search;

/**
 * Created by yibh on 2016/10/12 14:39 .
 */

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View mView;
    public SearchPresenter(SearchActivity searchActivity) {
        mView=searchActivity;
    }

    @Override
    public void getSearchList() {
        mView.startSearch();
    }
}
