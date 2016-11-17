package com.can.appstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.can.appstore.active.ActiveActivity;
import com.can.appstore.homerank.HomeRankActivity;
import com.can.appstore.index.IndexActivity;
import com.can.appstore.installpkg.InstallManagerActivity;
import com.can.appstore.myapps.ui.CustomFolderIconActivity;
import com.can.appstore.search.SearchActivity;
import com.can.appstore.specialdetail.SpecialDetailActivity;

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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, IndexActivity.class));
            }
        });
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));

            }
        });
        findViewById(R.id.btn_homerank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HomeRankActivity.class));

            }
        });

        findViewById(R.id.bt_specail_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpecialDetailActivity.startAc(MainActivity.this);
            }
        });
        findViewById(R.id.bt_install).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InstallManagerActivity.class));
            }
        });

        findViewById(R.id.bt_custom_viewgroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CustomFolderIconActivity.class));
            }
        });
        findViewById(R.id.bt_active).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ActiveActivity.class));
            }
        });
    }
}