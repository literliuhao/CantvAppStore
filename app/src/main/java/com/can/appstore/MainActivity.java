package com.can.appstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.can.appstore.search.SearchActivity;

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
        findViewById(R.id.bt_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.startAc(MainActivity.this);
            }
        });
    }
}
