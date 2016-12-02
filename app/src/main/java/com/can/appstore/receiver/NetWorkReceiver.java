package com.can.appstore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.can.appstore.http.HttpManager;

import cn.can.tvlib.utils.NetworkUtils;

public class NetWorkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean networkConnected = NetworkUtils.isNetworkConnected(context);
        if (networkConnected) {
            HttpManager.init(context);
        }
    }
}
