package com.can.appstore.homerank;

import java.util.List;

/**
 * Created by yibh on 2016/10/17 10:39 .
 */

public interface HomeRankContract {
    interface View {
        void startLoading();

        void getData(List list);
    }

    interface Presenter {
        void loadingData();
    }
}
