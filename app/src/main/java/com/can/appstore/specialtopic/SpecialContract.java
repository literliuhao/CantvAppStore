package com.can.appstore.specialtopic;

import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;
import com.can.appstore.entity.SpecialTopic;

import java.util.List;

/**
 * Created by laifrog on 2016/10/25.
 */

public interface SpecialContract {

    public static interface SpecialPresenter extends BasePresenter{
        void startLoad();
        void onItemFocused(int position);
        void remindNoData();
        void loadMore(int lastVisiablePos);
    }

    public static interface SubjectView extends BaseView<SpecialPresenter>{
        void refreshData(List<SpecialTopic> datas);
        void refreshRowNum(CharSequence formatRow);
        void showNoDataView();
        void showRetryView();
        void hideRetryView();
        void onLoadMore(int startInsertPos,int endInsertPos);
    }


}
