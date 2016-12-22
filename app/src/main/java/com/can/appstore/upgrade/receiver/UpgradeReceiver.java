package com.can.appstore.upgrade.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.can.appstore.index.IndexActivity;

/**
 * Created by zhl on 2016/11/30.
 */

public class UpgradeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            Intent i = new Intent(context, IndexActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
