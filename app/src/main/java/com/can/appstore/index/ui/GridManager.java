package com.can.appstore.index.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class GridManager extends GridView {

    public GridManager(Context context) {
        super(context);
        init(context, null);
    }

    public GridManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    WidgetTvViewBring mWidgetTvViewBring;

    private void init(Context context, AttributeSet attrs) {
        this.setChildrenDrawingOrderEnabled(true);
        mWidgetTvViewBring = new WidgetTvViewBring(this);
    }

    @Override
    public void bringChildToFront(View child) {
        mWidgetTvViewBring.bringChildToFront(this, child);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return mWidgetTvViewBring.getChildDrawingOrder(childCount, i);
    }

}