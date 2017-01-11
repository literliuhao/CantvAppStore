package com.can.appstore.index.interfaces;

import android.view.View;

import com.can.appstore.index.entity.FragmentEnum;

/**
 * Created by liuhao on 2016/09/09.
 */
public interface IAddFocusListener{
    void addFocusListener(View v, boolean hasFocus, FragmentEnum sourceEnum);
}