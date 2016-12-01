package com.can.appstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.can.appstore.active.ActiveActivity;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.applist.AppListActivity;
import com.can.appstore.download.DownloadActivity;
import com.can.appstore.index.IndexActivity;
import com.can.appstore.installpkg.InstallManagerActivity;
import com.can.appstore.message.MessageActivity;
import com.can.appstore.search.SearchActivity;
import com.can.appstore.specialdetail.SpecialDetailActivity;
import com.can.appstore.speciallist.SpecialActivity;
import com.can.appstore.uninstallmanager.UninstallManagerActivity;

//import com.can.appstore.myapps.ui.CustomFolderIconActivity;

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

        findViewById(R.id.bt_specail_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpecialDetailActivity.actionStart(MainActivity.this,"");
            }
        });
        findViewById(R.id.bt_install).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InstallManagerActivity.class));
            }
        });

        findViewById(R.id.bt_active).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ActiveActivity.class));
            }
        });


        //
        findViewById(R.id.bt_downloader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadActivity.actionStart(MainActivity.this);
            }
        });

        findViewById(R.id.bt_topics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpecialActivity.actionStart(MainActivity.this);
            }
        });

        findViewById(R.id.bt_apps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppListActivity.actionStart(MainActivity.this, AppListActivity.PAGE_TYPE_APP_LIST, "", "");
            }
        });

        findViewById(R.id.bt_rank_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppListActivity.actionStart(MainActivity.this, AppListActivity.PAGE_TYPE_RANKING, "", "");
            }
        });

        findViewById(R.id.bt_msgs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageActivity.actionStart(MainActivity.this);
            }
        });

        findViewById(R.id.bt_app_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailActivity.actionStart(MainActivity.this, "1", "");
            }
        });

        findViewById(R.id.bt_uninstall_manager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UninstallManagerActivity.actionStart(MainActivity.this);
            }
        });
    }
}