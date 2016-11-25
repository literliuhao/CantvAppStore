package com.can.appstore.update;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.ListResult;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.utils.UpdateUtils;

import java.util.ArrayList;
import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadStatus;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.PreferencesUtils;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;
import cn.can.tvlib.utils.ToastUtils;
import retrofit2.Response;

import static android.R.attr.data;
import static com.tencent.bugly.crashreport.inner.InnerApi.context;

/**
 * Created by shenpx on 2016/11/10 0010.
 */

public class UpdatePresenter implements UpdateContract.Presenter {

    private static final String TAG = "updatePresenter";
    private UpdateContract.View mView;
    private Context mContext;
    private List<AppInfoBean> mDatas;//已安装应用
    private List<AppInfo> date;
    private List<AppInfoBean> mAppInfoBeanList;

    public UpdatePresenter(UpdateContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
        mDatas = new ArrayList<AppInfoBean>();
        date = new ArrayList<AppInfo>();
    }

    @Override
    public void getInstallPkgList(boolean isAutoUpdate) {
        mDatas.clear();
        date.clear();
        mView.hideNoData();
        mView.showInstallPkgList(mDatas);
        if (isAutoUpdate) {
            mView.hideNoData();
            mView.showStartAutoUpdate();
            return;
        }
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            mView.showInternetError();
            ToastUtils.showMessage(mContext, "网络连接异常，请检查网络。");
            return;
        }
        mView.showLoadingDialog();
        final List appList = UpdateUtils.getAppList();
        //date = AppInfoBean.getAppInfoList(appList);
        mDatas.clear();
        //进行网络请求获取更新包信息
        CanCall<ListResult<AppInfo>> listResultCanCall = HttpManager.getApiService().checkUpdate(appList);
        listResultCanCall.enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                List<AppInfo> data = response.body().getData();
                mAppInfoBeanList = AppInfoBean.getAppInfoBeanList(data);
                Log.i(TAG, data.size()+"");
                if (mAppInfoBeanList.size() < 1 || mAppInfoBeanList == null) {
                    mView.hideLoadingDialog();
                    mView.showNoData();
                } else {
                    mView.hideLoadingDialog();
                    mView.hideNoData();
                    mDatas.addAll(mAppInfoBeanList);
                    mView.showInstallPkgList(mDatas);
                    setNum(0);
                }
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                Log.i(TAG, "onFailure");
                mView.hideLoadingDialog();
                mView.showNoData();
            }
        });

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
     * 检测自动更新添加队列
     *
     * @param context
     */
    public void autoUpdate(Context context) {
        //判断是否开启自动更新
        Log.i(TAG, "autoUpdate: "+111111);
        boolean isAutoUpdate = PreferencesUtils.getBoolean(MyApp.mContext, "AUTO_UPDATE", false);
        if (isAutoUpdate == false) {
            return;
        }
        //检测网络获取更新包数据
        if (!NetworkUtils.isNetworkConnected(context)) {
            ToastUtils.showMessage(context, "网络连接异常，请检查网络。");
            return;
        }

        final List appList = UpdateUtils.getAppList();
       /* AppInfo appInfo1 = new AppInfo();
        appInfo1.setPackageName("cn.cibntv.ott");
        appInfo1.setVersionCode(4);
        AppInfo appInfo2 = new AppInfo();
        appInfo2.setPackageName("打怪");
        appInfo2.setVersionCode(4);
        date.add(appInfo1);
        date.add(appInfo2);
        appList.add(appInfo1);
        appList.add(appInfo2);*/
        final DownloadManager mDownloadManager = DownloadManager.getInstance(context);
        Log.i(TAG, "autoUpdate: "+appList.toString());
        CanCall<ListResult<AppInfo>> listResultCanCall = HttpManager.getApiService().checkUpdate(appList);
        listResultCanCall.enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                List<AppInfo> data = response.body().getData();
                //添加队列
                addAutoUpdateTask(mDownloadManager, data);
                //mView.refreshAll();
                Log.i(TAG, "autoUpdate: "+444444);
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {

            }
        });
    }

    //添加自动更新队列
    private void addAutoUpdateTask(DownloadManager mDownloadManager, List<AppInfo> data) {
        for (int i = 0; i < data.size(); i++) {
            String downloadUrl = data.get(i).getUrl();
            DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
            Log.i(TAG, "autoUpdate: "+222222);
            if (downloadTask == null) {
                downloadTask = new DownloadTask();
                String md5 = MD5.MD5(downloadUrl);
                downloadTask.setFileName(md5 + ".apk");
                downloadTask.setId(md5);
                downloadTask.setSaveDirPath(MyApp.mContext.getExternalCacheDir().getPath() + "/");
                downloadTask.setUrl(downloadUrl);
                mDownloadManager.addDownloadTask(downloadTask, null);
                Log.i(TAG, "autoUpdate: "+333333);
            }
        }
    }

}
