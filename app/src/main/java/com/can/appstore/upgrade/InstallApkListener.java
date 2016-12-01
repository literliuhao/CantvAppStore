package com.can.appstore.upgrade;

/**
 * Created by syl on 2016/11/16.
 */

public interface InstallApkListener {
    void onInstallSuccess();
    void onInstallFail(String reason);
}
