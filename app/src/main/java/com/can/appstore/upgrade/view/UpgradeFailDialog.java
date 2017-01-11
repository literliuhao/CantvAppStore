package com.can.appstore.upgrade.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.can.appstore.R;

/**
 * Created by syl on 2016/11/1.
 */

public class UpgradeFailDialog extends Dialog{
    private Context mContext;
    private TextView mTvFailContent;
    private TextView mTvUpgradeFailBack;

    public UpgradeFailDialog(Context context,String failInfo) {
        super(context, R.style.upgradeDialogStyle);
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        mContext = context;
        initView();
        initData(failInfo);
    }
    private void initView() {
        setContentView(R.layout.layout_upgrade_fail);
        mTvUpgradeFailBack = (TextView) findViewById(R.id.tv_upgrade_fail_back);
        mTvFailContent = (TextView) findViewById(R.id.tv_fail_content);
        mTvUpgradeFailBack.setBackgroundResource(R.mipmap.btn_focus);
        mTvUpgradeFailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private void initData(String failInfo) {
        mTvFailContent.setText(failInfo);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
