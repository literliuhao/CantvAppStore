package com.can.appstore.myapps.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.can.appstore.R;

import java.util.ArrayList;

/**
 * Created by wei on 2016/11/4.
 */

public class CustomFolderIconActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myapps_list_item_custom);
        CustomFolderIcon customFolderIcon = (CustomFolderIcon) findViewById(R.id.my_icons);
        Drawable d1 = getResources().getDrawable(R.drawable.bj_02);
        Drawable d2 = getResources().getDrawable(R.drawable.icon_loading);
        Drawable d3 = getResources().getDrawable(R.drawable.bj_03);
        Drawable d4= getResources().getDrawable(R.drawable.bj_04);
        Drawable d5= getResources().getDrawable(R.drawable.bj_05);




        ArrayList<Drawable> list = new ArrayList<Drawable>();
        list.add(d1);
        list.add(d2);
//        list.add(d3);
//        list.add(d4);
//        list.add(d5);
        customFolderIcon.addMyIcon(list);

    }
}
