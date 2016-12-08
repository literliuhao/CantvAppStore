package com.can.appstore.upgrade.activity;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.upgrade.service.UpgradeService;
import com.can.appstore.upgrade.view.UpgradeInFoDialog;
import com.can.appstore.upgrade.widgets.UpgradeInfoNoticeCursor;
import com.can.appstore.upgrade.widgets.UpgradeInfoScrollView;

/**
 * Created by syl on 2016/12/7.
 */

public class UpgradeInfoActivity extends Activity {

    private String mVersionName;
    private String mNewFeature;
    private String mFileName;
    private String mFilePath;
    private long mUpgradeSize;

    private TextView mTvUpgradeTitle;
    private TextView mTvUpgradeInfoVersion;
    private TextView mTvUpgradeInfoContent;
    private TextView tv_upgrade_install;
    private UpgradeInfoScrollView mSvUpgradeInfo;
    private UpgradeInfoNoticeCursor mUserNoticeCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_upgrade_info_activity);
        initView();
        parseIntentData();
        initData();
    }

    private void initView() {
        mSvUpgradeInfo = (UpgradeInfoScrollView) findViewById(R.id.sv_upgrade_info);
        mUserNoticeCursor = (UpgradeInfoNoticeCursor) findViewById(R.id.user_notice_cursor);
        mSvUpgradeInfo.setCursor(mUserNoticeCursor);

        mTvUpgradeTitle = (TextView) findViewById(R.id.tv_upgrade_title);
        mTvUpgradeInfoVersion = (TextView) findViewById(R.id.tv_upgrade_info_version);
        mTvUpgradeInfoContent = (TextView) findViewById(R.id.tv_upgrade_info_content);
        tv_upgrade_install = (TextView) findViewById(R.id.tv_upgrade_install);
        tv_upgrade_install.setBackgroundResource(R.mipmap.btn_focus);

        mSvUpgradeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpgradeInfoActivity.this,ProgressActivity.class);
                intent.putExtra(UpgradeService.FILE_NAME,mFileName);
                intent.putExtra(UpgradeService.UPGRADE_SIZE,mUpgradeSize);
                intent.putExtra(UpgradeService.FILE_PATH,mFilePath);
                UpgradeInfoActivity.this.startActivity(intent);
                finish();
            }
        });
    }


    private void parseIntentData() {
        Intent intent = getIntent();
        mVersionName = intent.getStringExtra(UpgradeService.VERSION_NAME);
        mNewFeature = intent.getStringExtra(UpgradeService.NEW_FEATURE);
        mFileName = intent.getStringExtra(UpgradeService.FILE_NAME);
        mUpgradeSize = intent.getLongExtra(UpgradeService.UPGRADE_SIZE,0);
        mFilePath = intent.getStringExtra(UpgradeService.FILE_PATH);
    }


    private void initData() {
        mTvUpgradeTitle.setText(getResources().getString(R.string.app_upgrade));
        mTvUpgradeInfoVersion.setText(getResources().getString(R.string.new_version) + mVersionName);
        mTvUpgradeInfoContent.setText(mNewFeature);
        tv_upgrade_install.setText(getResources().getString(R.string.install));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
