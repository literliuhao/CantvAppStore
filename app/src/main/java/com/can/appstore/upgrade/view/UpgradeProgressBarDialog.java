package com.can.appstore.upgrade.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.can.appstore.R;
import com.can.appstore.upgrade.widgets.UpgradeProgressBar;

/**
 * Created by syl on 2016/11/1.
 */

public class UpgradeProgressBarDialog extends Dialog{
    private static final String TAG = "UpgradeProgress";
    private UpgradeProgressBar mProgressBar;
    private int mProgress;
    private long mTime;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressBar.setProgress(mProgress);
            mProgress++;
            if(mProgress <= 70){
                mHandler.sendEmptyMessageDelayed(1,10);
            }else{
                Log.d(TAG, "handleMessage: "+ (System.currentTimeMillis()-mTime));
            }
        }
    };

    public UpgradeProgressBarDialog(Context context) {
        super(context, R.style.upgradeDialogStyle);
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        mTime = System.currentTimeMillis();
        initView();
    }

    private void initView() {
        setContentView(R.layout.layout_upgrade_progress_bar);
        mProgressBar = (UpgradeProgressBar) findViewById(R.id.pb_upgrade);
        mProgressBar.setMax(70);
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
          return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
