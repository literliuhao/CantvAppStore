package com.can.appstore.upgrade;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.can.appstore.PortalActivity;
import com.can.appstore.R;
import com.can.appstore.index.IndexActivity;
import com.can.appstore.upgrade.service.BuglyUpgradeService;
import com.can.appstore.upgrade.service.UpgradeService;
import com.can.appstore.upgrade.view.UpgradeInFoDialog;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;

/**
 * Created by syl on 2016/12/7.
 */

public class MyUpgradeListener implements UpgradeListener {
    private static final String TAG = "MyUpgradeListener";
    private Context mContext;
    private boolean mDownloadSelf;

    public MyUpgradeListener(Context context, boolean downloadSelf) {
        mContext = context.getApplicationContext();
        mDownloadSelf = downloadSelf;
    }

    @Override
    public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
        if (strategy != null) {
            Log.d(TAG, "onUpgrade: 更新");
            Intent intent;
            if (mDownloadSelf) {
                intent = new Intent(mContext, UpgradeService.class);
            } else {
                intent = new Intent(mContext, BuglyUpgradeService.class);
            }
            mContext.startService(intent);
        } else {
            Log.d(TAG, "onUpgrade: 没有更新");
        }
    }
}
