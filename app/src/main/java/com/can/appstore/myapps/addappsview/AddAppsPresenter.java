package com.can.appstore.myapps.addappsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.entity.SelectedAppInfo;
import com.can.appstore.myapps.utils.MyAppsListDataUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil;
import cn.can.tvlib.common.pm.PackageUtil.AppInfo;
import cn.can.tvlib.ui.widgets.LoadingDialog;

/**
 * Created by wei on 2016/11/3.
 */

public class AddAppsPresenter implements AddAppsContract.Presenter {
    public final static String TAG = "AddAppsPresenter";
    public final static int ONR_ROW_SHOW_COUNT = 4;
    private AddAppsContract.View mView;
    private Context mContext;
    //数据
    private MyAppsListDataUtil mMyAppListData;
    private List<PackageUtil.AppInfo> isShown;
    private List<PackageUtil.AppInfo> addShowList = new ArrayList<>();
    private List<PackageUtil.AppInfo> mAllAppList;

    private LoadingDialog mLoadingDialog;
    //当前被选择的应用
    public List<SelectedAppInfo> mSelectAppInfo = new ArrayList<>();

    private AddAppsPresenter.AppInstallReceiver mAppInstallReceiver;
    private ArrayList<SelectedAppInfo> mAddShowList;

    public AddAppsPresenter(AddAppsContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void startLoad() {
        new AsyncTask<Void, Void, Void>() {
            //加载数据之前
            @Override
            protected void onPreExecute() {
                mView.showLoadingDialog();
            }

            //加载数据
            @Override
            protected Void doInBackground(Void... params) {
                mMyAppListData = new MyAppsListDataUtil(mContext);
                if (addShowList != null) {
                    addShowList.clear();
                }
                isShown = mMyAppListData.getShowList(isShown, null);
                mAllAppList = PackageUtil.findAllThirdPartyAppsNoDelay(mContext, mAllAppList);
                for (PackageUtil.AppInfo app : mAllAppList) {
                    boolean inShown = false;
                    for (PackageUtil.AppInfo appInfo : isShown) {
                        if (app.packageName.equals(appInfo.packageName)) {
                            inShown = true;
                            break;
                        }
                    }
                    if (!inShown) {
                        addShowList.add(app);
                    }
                }

                mAddShowList = new ArrayList<>();
                for (PackageUtil.AppInfo appInfo : addShowList) {
                    mAddShowList.add(SelectedAppInfo.wrap(appInfo));
                }
                return null;
            }

            //加载完数据
            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d(TAG, "onPostExecute");
                mView.loadAddAppInfoSuccess(mAddShowList);
                mView.hideLoadingDialog();
            }
        }.execute();

    }

    @Override
    public void addListener() {
        registerInstallReceiver();
    }

    /**
     * 注册应用安装卸载的广播
     */
    private void registerInstallReceiver() {
        Log.d(TAG, "registerInstallReceiver");
        if (mAppInstallReceiver == null) {
            mAppInstallReceiver = new AddAppsPresenter.AppInstallReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_VIEW);
            filter.addDataScheme("package");
            mContext.registerReceiver(mAppInstallReceiver, filter);
        }
    }

    /**
     * 刷新之前已经选择的数据
     */
    public void refreshSelectDataPosition() {
        Log.d(TAG, "refreshSelectDataPosition");
        if (mSelectAppInfo != null && mSelectAppInfo.size() > 0) {
            int[] alreadySelect = new int[mSelectAppInfo.size()];
            for (int i = 0; i < mSelectAppInfo.size(); i++) {
                for (int j = 0; j < addShowList.size(); j++) {
                    if (mSelectAppInfo.get(i).packageName.equals(addShowList.get(j).packageName)) {
                        alreadySelect[i] = j;
                    }
                }
            }
            mSelectAppInfo.clear();
            mView.setAlreadySelectApp(alreadySelect);
        }
    }

    class AppInstallReceiver extends BroadcastReceiver {

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
        for (int j = 0; j < addShowList.size(); j++) {
            if (packageName.equals(addShowList.get(j).packageName)) {
                addShowList.remove(j);
                mView.loadAddAppInfoSuccess(mAddShowList);
            }
        }
    }

    @Override
    public void release() {
        if (mMyAppListData != null) {
            mMyAppListData = null;
        }
        if (isShown != null) {
            isShown.clear();
            isShown = null;
        }
        if (addShowList != null) {
            addShowList.clear();
            addShowList = null;
        }
        hideLoading();
    }

    public void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public void showLoading(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.px80));
            mLoadingDialog.setMessage(msg);
            mLoadingDialog.show();
        }
    }

    /**
     * 计算当前总行数
     *
     * @return
     */
    public int calculateCurTotalRows() {
        int totalCount = addShowList.size();
        int totalRows = totalCount / ONR_ROW_SHOW_COUNT;
        if (totalCount % ONR_ROW_SHOW_COUNT != 0) {
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
        return position / ONR_ROW_SHOW_COUNT + 1;
    }

    /**
     * 计算可添加的个数
     */
    public void canSelectCount() {
        int canSelect = 18 - isShown.size();
        int alreadyShown = isShown.size() - 2;
        mView.showCanSelectCount(canSelect, alreadyShown);
    }


    public void saveSelectlist(List<SelectedAppInfo> list) {
        isShown.addAll(list);
        mMyAppListData.saveShowList(isShown);
    }

    public void unRegiestr() {
        if (mAppInstallReceiver != null) {
            mContext.unregisterReceiver(mAppInstallReceiver);
            mAppInstallReceiver = null;
        }
    }

}
