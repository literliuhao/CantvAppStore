package com.can.appstore.upgrade.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.can.appstore.R;

/**
 * Created by syl on 2016/12/7.
 */

public class UpgradeFailActivity extends Activity{
    private TextView mTvFailContent;
    private TextView mTvUpgradeFailBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upgrade_fail);
        initView();
        initData();
    }
    private void initView() {
        mTvUpgradeFailBack = (TextView) findViewById(R.id.tv_upgrade_fail_back);
        mTvFailContent = (TextView) findViewById(R.id.tv_fail_content);
        mTvUpgradeFailBack.setBackgroundResource(R.mipmap.btn_focus);
        mTvUpgradeFailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initData() {
        mTvFailContent.setText(getIntent().getStringExtra(ProgressActivity.FAIL_REASON));
    }

}
