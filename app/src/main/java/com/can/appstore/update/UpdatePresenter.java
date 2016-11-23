package com.can.appstore.update;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.utils.UpdateUtils;

import java.util.ArrayList;
import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;

/**
 * Created by shenpx on 2016/11/10 0010.
 */

public class UpdatePresenter implements UpdateContract.Presenter {

    private static final String TAG = "updatePresenter";
    private UpdateContract.View mView;
    private Context mContext;
    private List<AppInfoBean> mDatas;//已安装应用
    private List<AppInfo> date;

    public UpdatePresenter(UpdateContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
        mDatas = new ArrayList<AppInfoBean>();
        date = new ArrayList<AppInfo>();
    }

    @Override
    public void getInstallPkgList(boolean isAutoUpdate) {
        //mDatas.clear();
        date.clear();
        mView.showInstallPkgList(mDatas);
        if (isAutoUpdate) {
            mView.hideNoData();
            mView.showStartAutoUpdate();
            return;
        }
        mView.showLoadingDialog();
        final List appList = UpdateUtils.getAppList();
        //mDatas.clear();
        UpdateAppList.list.clear();
        //进行网络请求获取更新包信息
        /*AppInfo appInfo1 = new AppInfo();
        appInfo1.setPackageName("cn.cibntv.ott");
        appInfo1.setVersionCode(4);
        AppInfo appInfo2 = new AppInfo();
        appInfo2.setPackageName("打怪");
        appInfo2.setVersionCode(4);
        date.add(appInfo1);
        date.add(appInfo2);
        CanCall<ListResult<AppInfo>> listResultCanCall = HttpManager.getApiService().checkUpdate(date);
        listResultCanCall.enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                Log.i("shen",response.body().toString());
                Log.i("shen",response.body()+"");
                List<AppInfo> data = response.body().getData();
                String url = data.get(0).getUrl();
                Log.i("shen",data.toString());
                Log.i("shen",data+"");
                Log.i("shen",url+"");
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {

            }
        });*/
        if (appList.size() < 1 || appList == null) {
            mView.showNoData();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mView.hideLoadingDialog();
                    mView.hideNoData();
                    //进行网络请求获取更新包信息
                    mDatas.addAll(appList);
                    UpdateAppList.list.addAll(appList);
                    mView.showInstallPkgList(mDatas);
                    setNum(0);
                }
            }, 2000);
        }
    }

    public List<AppInfoBean> getList() {
        return mDatas;
    }

    @Override
    public void getSDInfo() {
        long freeSize = SystemUtil.getSDCardAvailableSpace();
        long totalSize = SystemUtil.getSDCardTotalSpace();
        int progress = (int) (((totalSize - freeSize) * 100) / totalSize);
        String freeStorage = mContext.getResources().getString(R.string.uninsatll_manager_free_storage) + StringUtils.formatFileSize(freeSize, false);
        mView.showSDProgressbar(progress, freeStorage);
    }

    @Override
    public void refreshInstallPkgList() {

    }

    @Override
    public void getListSize() {
        if (mDatas.size() < 1) {
            mView.showStartAutoUpdate();
        }
    }

    @Override
    public void clearList() {
        mDatas.clear();
        mView.refreshAll();
    }

    @Override
    public void release() {
        mView.hideLoadingDialog();
    }

    /**
     * 行数提示
     *
     * @param position
     */
    public void setNum(int position) {
        int total = mDatas.size() / 3;
        if (mDatas.size() % 3 != 0) {
            total += 1;
        }
        int cur = position / 3 + 1;
        if (total == 0) {
            cur = 0;
        }
        mView.showCurrentNum(cur, total);
    }

    /**
     * 是否已安装
     * 刷新图标（可能多重版本）通过广播获取安装完成刷新ui  +&& bean.getVersionCode().equals(String.valueOf(versonCode))
     *
     * @param packageName
     * @param //int       versonCode   && bean.getVersionCode().equals(String.valueOf(versonCode))
     */
    public void isInstalled(String packageName, int versionCode) {
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            AppInfoBean bean = mDatas.get(i);
            if (bean.getPackageName().equals(packageName)) {
                //if (bean.getInstall()) {
                bean.setUpdated(true);
                bean.setInstalled(false);
                mView.refreshAll();
                Log.i(TAG, "isInstalled: " + packageName + "22222");
                Toast.makeText(MyApp.mContext, packageName + "22222", Toast.LENGTH_LONG).show();
                //}
            }
        }
    }
}
