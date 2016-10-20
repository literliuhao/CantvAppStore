package com.can.appstore;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.can.appstore.myapps.MyApps;

/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FragmentManager fragmentManager = getFragmentManager();
        MyApps viewById = (MyApps)fragmentManager.findFragmentById(R.id.myapps_fragment);
        if(viewById.dispatchKeyEvent(event)){
            return true;
        }

        return super.dispatchKeyEvent(event);
    }
}
