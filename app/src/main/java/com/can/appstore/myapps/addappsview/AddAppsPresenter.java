package com.can.appstore.myapps.addappsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;

import com.can.appstore.R;
import com.can.appstore.myapps.model.MyAppsListDataUtil;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.widgets.LoadingDialog;
import cn.can.tvlib.utils.PackageUtil;
import cn.can.tvlib.utils.PackageUtil.AppInfo;

/**
 * Created by wei on 2016/11/3.
 */

public class AddAppsPresenter implements AddAppsContract.Presenter {
    private AddAppsContract.View mView;
    private Context mContext;
    private BroadcastReceiver mHomeReceivcer;
    //数据
    private MyAppsListDataUtil mMyAppListData;
    private List<AppInfo> isShown;
    private List<AppInfo> addShowList = new ArrayList<AppInfo>();
    private List<AppInfo> mAllAppList;

    private LoadingDialog mLoadingDialog;

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
                mView.showLoading();
            }

            //加载数据
            @Override
            protected Void doInBackground(Void... params) {
                mMyAppListData = new MyAppsListDataUtil(mContext);
                isShown = mMyAppListData.getShowList(isShown);
                mAllAppList = PackageUtil.findAllThirdPartyApps(mContext, mAllAppList);
                for (AppInfo app : mAllAppList) {
                    boolean inShown = false;
                    for (AppInfo appInfo : isShown) {
                        if (app.packageName.equals(appInfo.packageName)) {
                            inShown = true;
                            break;
                        }
                    }
                    if (!inShown) {
                        addShowList.add(app);
                    }
                }
                return null;
            }
            //加载完数据
            @Override
            protected void onPostExecute(Void aVoid) {
                mView.loadAddAppInfoSuccess(addShowList);
                mView.hideLoading();
            }
        }.execute();

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
     * @return
     */
    public int calculateCurTotalRows() {
        int totalCount = addShowList.size();
        int totalRows = totalCount / 4;
        if (totalCount % 4 != 0) {
            totalRows = totalRows + 1;
        }
        return totalRows;
    }

    /**
     * 计算当前行数
     * @param position
     * @return
     */
    public int calculateCurRows(int position) {
        return position / 4 + 1;
    }

    /**
     * 计算可添加的个数
     */
    public void canSelectCount() {
        int canSelect = 18 - isShown.size();
        int alreadyShown = isShown.size() - 2;
        mView.showCanSelectCount(canSelect, alreadyShown);
    }



    public void saveSelectlist(List<AppInfo> list) {
        boolean b = isShown.addAll(list);
        if (b) {
            ToastUtil.toastShort("添加成功");
        } else {
            ToastUtil.toastShort("添加失败");
        }
        mMyAppListData.saveShowList(isShown);
    }

}
