package com.can.appstore.upgrade.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import com.can.appstore.R;
import com.can.appstore.upgrade.widgets.UpgradeProgressBar;

/**
 * Created by syl on 2016/11/1.
 */

public class UpgradeProgressBarDialog extends Dialog{

    private UpgradeProgressBar mProgressBar;
    private int mProgress;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mHandler.sendEmptyMessageDelayed(1,10);
            mProgressBar.setProgress(mProgress);
            mProgress++;
            if(mProgress > 200){
                mProgress = 0;
            }
        }
    };

    public UpgradeProgressBarDialog(Context context) {
        super(context, R.style.upgradeDialogStyle);
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        initView(context);
    }

    private void initView(Context context) {
        setContentView(R.layout.layout_upgrade_progress_bar);
        mProgressBar = (UpgradeProgressBar) findViewById(R.id.pb_upgrade);
        mProgressBar.setMax(200);
        mHandler.sendEmptyMessageDelayed(1,100);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//          return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

}
