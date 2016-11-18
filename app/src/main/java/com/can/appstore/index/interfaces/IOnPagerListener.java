package com.can.appstore.index.interfaces;

import android.view.View;

/**
 * Created by liuhao on 2016/09/09.
 */
public interface IOnPagerListener {
    void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onExtraPageSelected(int position, View view);

    void onExtraPageScrollStateChanged(int state, View view);
}