package com.can.appstore.myapps.allappsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.myapps.model.AppInfo;
import com.can.appstore.myapps.model.AppUtils;
import com.can.appstore.myapps.model.MyAppsListDataUtil;

import java.util.List;

import cn.can.tvlib.ui.widgets.LoadingDialog;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by wei on 2016/11/8.
 */

public class AllAppsPresenter implements AllAppsContract.Presenter {

    LoadingDialog mLoadingDialog;
    AllAppsContract.View mView;
    MyAppsListDataUtil mMyAppsListDataUtil;
    List<AppInfo> allAppsList;
    Context mContext;

    AppInstallReceiver mAppInstallReceiver;
    private BroadcastReceiver mHomeReceivcer;


    public AllAppsPresenter(AllAppsContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }


    @Override
    public void startLoad() {
        new AsyncTask<Void, Void, Void>() {
            //加载数据之前
            @Override
            protected void onPreExecute() {
                mView.showLoading();
            }

            //加载数据
            @Override
            protected Void doInBackground(Void... params) {
                mMyAppsListDataUtil = new MyAppsListDataUtil(mContext);
                allAppsList = mMyAppsListDataUtil.getAllAppList(allAppsList);
                return null;
            }

            //加载完数据
            @Override
            protected void onPostExecute(Void aVoid) {
                mView.loadAllAppInfoSuccess(allAppsList);
                mView.hideLoading();
            }
        }.execute();

    }

    @Override
    public void addListener() {
        registHomeBoradCast();
        registerInstallReceiver();
    }

    @Override
    public void release() {
        if (mMyAppsListDataUtil != null) {
            mMyAppsListDataUtil = null;
        }
        if (allAppsList != null) {
            allAppsList.clear();
            allAppsList = null;
        }
        hideLoading();
    }

    public void showLoading(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.px80));
            mLoadingDialog.setMessage(msg);
            mLoadingDialog.show();
        }
    }

    public void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 注册按主页键的广播
     */
    private void registHomeBoradCast() {
        mHomeReceivcer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    mView.onClickHomeKey();
                    return;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.registerReceiver(mHomeReceivcer, filter);
    }

    /**
     * 注册应用安装卸载的广播
     */
    private void registerInstallReceiver() {
        if (mAppInstallReceiver == null) {
            mAppInstallReceiver = new AppInstallReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_VIEW);
            filter.addDataScheme("package");
            mContext.registerReceiver(mAppInstallReceiver, filter);
        }
    }

    class AppInstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
                Log.d(TAG, "install packageName : " + packageName);
                startLoad();
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.d(TAG, "uninstall packageName : " + packageName);
                refreshItemInListPosition(packageName);
            }
        }
    }

    private void refreshItemInListPosition(String packageName) {
        for (int j = 0; j < allAppsList.size(); j++) {
            if (packageName.equals(allAppsList.get(j).packageName)) {
                allAppsList.remove(j);
                mView.loadAllAppInfoSuccess(allAppsList);
            }
        }
    }

    /**
     * 计算当前总行数
     *
     * @return
     */
    public int calculateCurTotalRows() {
        int totalCount = allAppsList.size();
        int totalRows = totalCount / 5;
        if (totalCount % 5 != 0) {
            totalRows = totalRows + 1;
        }
        return totalRows;
    }

    /**
     * 计算当前行数
     *
     * @param position
     * @return
     */
    public int calculateCurRows(int position) {
        return position / 5 + 1;
    }

    public void startApp(int position) {
        //TODO
    }

    public void uninstallApp(int position) {
        String packageName = allAppsList.get(position).packageName;
        AppUtils.uninstallpkg(mContext, packageName);
    }

    public void unRegiestr() {
        if (mAppInstallReceiver != null) {
            mContext.unregisterReceiver(mAppInstallReceiver);
            mAppInstallReceiver = null;
        }
        if (mHomeReceivcer != null) {
            mContext.unregisterReceiver(mHomeReceivcer);
            mHomeReceivcer = null;
        }
    }
}
