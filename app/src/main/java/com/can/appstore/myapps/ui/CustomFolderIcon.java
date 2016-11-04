package com.can.appstore.myapps.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.can.appstore.R;

/**
 * Created by wei on 2016/11/4.
 */

public class CustomFolderIcon extends ViewGroup {

    public CustomFolderIcon(Context context) {
        super(context);
    }

    public CustomFolderIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFolderIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomFolderIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 对子控件进行定位
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int cWidth = 0;
        int cHeight = 0;

        MarginLayoutParams cParams = null;

        //便利所有的子View，获取他们的宽和高以及margin来设置他们的位置

        for (int i = 0; i < cCount; i++) {
            View cView = getChildAt(i);
            cWidth = cView.getMeasuredWidth();
            cHeight = cView.getMeasuredHeight();
            cParams = (MarginLayoutParams) cView.getLayoutParams();

            int cl = 0,ct = 0,cr = 0,cb = 0;

            switch (i){
                case 0:
                    cl = cParams.leftMargin;
                    ct = cParams.topMargin;
                    break;
                case 1:
                    cl = cParams.leftMargin*2 + cWidth;
                    ct = cParams.topMargin;
                    break;
                case 2:
                    cl = cParams.leftMargin*3 + cWidth*2;
                    ct = cParams.topMargin;
                    break;
                case 3:
                    cl = cParams.leftMargin;
                    ct = cParams.topMargin*2 + cHeight;
                    break;
                case 4:
                    cl = cParams.leftMargin*2 + cWidth;
                    ct = cParams.topMargin*2 + cHeight;
                    break;
                case 5:
                    cl = cParams.leftMargin*3 + cWidth*2;
                    ct = cParams.topMargin*2 + cHeight;
                    break;
            }
            cr = cl + cWidth;
            cb = ct + cHeight;
            cView.layout(cl,ct,cr,cb);
        }


    }

    /**
     * 设置ViewGroup自身的宽高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(R.dimen.px224,R.dimen.px152);
    }

    /**
     * 设置该ViewGroup的布局方式
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
