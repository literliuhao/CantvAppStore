package com.can.appstore.subject;

import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;
import com.can.appstore.subject.model.SubjectInfo;

import java.util.List;

/**
 * Created by laifrog on 2016/10/25.
 */

public interface SubjectContract {

    public static interface SubjectPresenter extends BasePresenter{
        void startLoad();
        void loadMore(int lastVisiablePos);
        void onItemFocused(int position);
    }

    public static interface SubjectView extends BaseView<SubjectPresenter>{
        void refreshData(List<SubjectInfo> datas);
        void refreshRowNum(String formatRow);
    }


}
