package com.can.appstore.base;

import android.content.Context;

/**
 * Created by laiforg on 2016/10/25.
 */

public interface BaseView <T>{

    void setPresenter(T presenter);

    void showToast(String msg);

    void showToast(int resId);

    void showLoadingDialog();

    void hideLoadingDialog();

    Context getContext();


}
