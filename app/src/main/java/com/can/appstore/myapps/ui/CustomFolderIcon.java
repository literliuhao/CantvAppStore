package com.can.appstore.myapps.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.can.appstore.R;

import java.util.List;

/**
 * Created by wei on 2016/11/4.
 */

public class CustomFolderIcon extends RelativeLayout {
    Context context;

    public CustomFolderIcon(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CustomFolderIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomFolderIcon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initView();
    }

    public CustomFolderIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private ImageView iv_01;
    private ImageView iv_02;
    private ImageView iv_03;
    private ImageView iv_04;
    private ImageView iv_05;
    private ImageView iv_06;

    private void initView() {
        View.inflate(context, R.layout.custom_folder_icon_item, CustomFolderIcon.this);
        iv_01 = (ImageView) findViewById(R.id.myapps_sys_icon1);
        iv_02 = (ImageView) findViewById(R.id.myapps_sys_icon2);
        iv_03 = (ImageView) findViewById(R.id.myapps_sys_icon3);
        iv_04 = (ImageView) findViewById(R.id.myapps_sys_icon4);
        iv_05 = (ImageView) findViewById(R.id.myapps_sys_icon5);
        iv_06 = (ImageView) findViewById(R.id.myapps_sys_icon6);
    }

    public void addMyIcon(List<Drawable> list) {
        switch (list.size()) {
            case 1:
                addOneIcon(list);
                break;
            case 2:
                addTwoIcon(list);
                break;
            case 3:
                addThreeIcon(list);
                break;
            case 4:
                addForeIcon(list);
                break;
            case 5:
                addFiveIcon(list);
                break;
            case 6:
                addSixIcon(list);
                break;
            default:
                break;
        }
    }

    public void addOneIcon(List<Drawable> list) {
        iv_01.setVisibility(VISIBLE);
        iv_01.setImageDrawable(list.get(0));
    }

    private void addTwoIcon(List<Drawable> list) {
        addOneIcon(list);
        iv_02.setVisibility(VISIBLE);
        iv_02.setImageDrawable(list.get(1));
    }

    private void addThreeIcon(List<Drawable> list) {
        addTwoIcon(list);
        iv_03.setVisibility(VISIBLE);
        iv_03.setImageDrawable(list.get(2));
    }

    private void addForeIcon(List<Drawable> list) {
        addThreeIcon(list);
        iv_04.setVisibility(VISIBLE);
        iv_04.setImageDrawable(list.get(3));
    }

    private void addFiveIcon(List<Drawable> list) {
        addForeIcon(list);
        iv_05.setVisibility(VISIBLE);
        iv_05.setImageDrawable(list.get(4));
    }

    private void addSixIcon(List<Drawable> list) {
        addFiveIcon(list);
        iv_06.setVisibility(VISIBLE);
        iv_06.setImageDrawable(list.get(5));
    }

}
