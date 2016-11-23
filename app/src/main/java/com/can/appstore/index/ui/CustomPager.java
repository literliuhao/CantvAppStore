package com.can.appstore.index.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by liuhao on 2016/11/21.
 */

public class CustomPager extends ViewPager {

    public CustomPager(Context context) {
        super(context);
    }

    public CustomPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.i("CustomPager", "gainFocus " + gainFocus + " direction " + " previouslyFocusedRect " + previouslyFocusedRect);
    }
}
