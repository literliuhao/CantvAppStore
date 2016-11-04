package com.can.appstore.myapps.addappsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import com.can.appstore.R;
import com.can.appstore.myapps.model.AppInfo;
import com.can.appstore.myapps.model.MyAppsListDataUtil;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.widgets.LoadingDialog;

/**
 * Created by wei on 2016/11/3.
 */

public class AddAppsPresenter implements AddAppsContract.Presenter{
    AddAppsContract.View  mView;
    Context mContext;

    private LoadingDialog mLoadingDialog;


    MyAppsListDataUtil mMyAppListData;
    List<AppInfo> isShown;
    List<AppInfo> addShowList = new ArrayList<AppInfo>();
    private List<AppInfo> mAllAppList;

    private BroadcastReceiver mHomeReceivcer;

    public AddAppsPresenter(AddAppsContract.View  view, Context context){
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void startLoad() {
        new AsyncTask<Void,Void,Void>(){
            //加载数据之前
            @Override
            protected void onPreExecute() {
                mView.showLoading();
            }

            //加载数据
            @Override
            protected Void doInBackground(Void... params) {
                mMyAppListData = new MyAppsListDataUtil(mContext);
                isShown = mMyAppListData.getShowList();
                mAllAppList = mMyAppListData.getAllAppList();
                return null;
            }

            //加载完数据
            @Override
            protected void onPostExecute(Void aVoid) {
                for (AppInfo  app:mAllAppList) {
                    if(!isShown.contains(app)){
                        addShowList.add(app);
                    }
                }
                mView.loadAllAppInfoSuccess(addShowList);
                mView.hideLoading();
            }
        }.execute();

    }

    @Override
    public void addListener() {
        registHomeBoradCast();
    }

    @Override
    public void release() {
        if(mMyAppListData!=null){
            mMyAppListData = null;
        }
        if(mAllAppList!= null){
            mAllAppList.clear();
            mAllAppList = null;
        }
        if(isShown!= null){
            isShown.clear();
            isShown = null;
        }
        if(addShowList != null){
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
        int totalRows = totalCount / 4;
        if (totalCount % 4 != 0) {
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
        return position / 4 + 1;
    }
    /**
     * 计算可添加的个数
     */
    public void  canSelectCount(){
        int canSelect = 18 - isShown.size()+1;
        int alreadyShown= isShown.size()-3;
        mView.showCanSelectCount(canSelect,alreadyShown);
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

    public void unRegiestr() {
        if (mHomeReceivcer != null) {
            mContext.unregisterReceiver(mHomeReceivcer);
            mHomeReceivcer = null;
        }
    }

    public void saveSelectlist(List<AppInfo> list) {
        boolean b = isShown.addAll(list);
        if(b){
            ToastUtil.toastShort("添加成功");
        }else{
            ToastUtil.toastShort("添加失败");
        }
        mMyAppListData.saveShowList(isShown);
    }
}
