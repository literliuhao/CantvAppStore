package com.can.appstore.update;

import android.content.Context;
import android.util.Log;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.ListResult;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.installpkg.InstallApkModel;
import com.can.appstore.installpkg.utils.InstallPkgUtils;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.model.UpdateApkInstallModel;
import com.can.appstore.update.model.UpdateApkModel;
import com.can.appstore.update.utils.UpdateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.PreferencesUtils;
import cn.can.tvlib.utils.PromptUtils;
import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;
import retrofit2.Response;

/**
 * Created by shenpx on 2016/11/10 0010.
 */

public class UpdatePresenter implements UpdateContract.Presenter {

    private static final String TAG = "updatePresenter";
    private UpdateContract.View mView;
    private Context mContext;
    private List<AppInfoBean> mDatas;//更新应用集合
    private List<AppInfoBean> mAppInfoBeanList;

    public UpdatePresenter(UpdateContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
        mDatas = new ArrayList<AppInfoBean>();
    }

    @Override
    public void getInstallPkgList(boolean isAutoUpdate) {
        mDatas.clear();
        AutoUpdate.getInstance().mUpdateNumDatas.clear();
        mView.hideNoData();
        mView.showInstallPkgList(mDatas);
        if (isAutoUpdate) {
            mView.hideNoData();
            mView.showStartAutoUpdate();
            return;
        }
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            mView.showInternetError();
            PromptUtils.toast(mContext, MyApp.getContext().getResources().getString(R.string.no_network));
            return;
        }
        mView.showLoading();
        final List appList = UpdateUtils.getAppList();
        //mAppInfoBeanList = UpdateUtils.getAppInfoBeanList();
         /*AppInfo appInfo1 = new AppInfo();
        appInfo1.setPackageName("cn.cibntv.ott");
        appInfo1.setVersionCode(4);
        AppInfo appInfo2 = new AppInfo();
        appInfo2.setPackageName("打怪");
        appInfo2.setVersionCode(4);
        appList.add(appInfo1);
        appList.add(appInfo2);*/
        //进行网络请求获取更新包信息
        CanCall<ListResult<AppInfo>> listResultCanCall = HttpManager.getApiService().checkUpdate(appList);
        listResultCanCall.enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                List<AppInfo> data = response.body().getData();
                mAppInfoBeanList = AppInfoBean.getAppInfoBeanList(data);
                Log.i(TAG, data.size() + "");
                Log.i(TAG, data.toString());
                if (mAppInfoBeanList.size() < 1 || mAppInfoBeanList == null) {
                    mView.hideLoading();
                    mView.showNoData();
                } else {
                    mView.hideLoading();
                    mView.hideNoData();
                    mDatas.addAll(mAppInfoBeanList);
                    AutoUpdate.getInstance().mUpdateNumDatas.addAll(mAppInfoBeanList);
                    mView.showInstallPkgList(mDatas);
                    setNum(0);
                }
                //发送数量
                EventBus.getDefault().post(new UpdateApkModel(data.size()));
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                Log.i(TAG, "onFailure");
                mView.hideLoading();
                mView.showNoData();
            }
        });

        /*if (mAppInfoBeanList.size() < 1 || mAppInfoBeanList == null) {
            mView.hideLoading();
            mView.showNoData();
        } else {
            mView.hideLoading();
            mView.hideNoData();
            mDatas.addAll(mAppInfoBeanList);
            AutoUpdate.getInstance().mUpdateNumDatas.addAll(mAppInfoBeanList);
            mView.showInstallPkgList(mDatas);
            setNum(0);
        }
        //发送数量
        EventBus.getDefault().post(new UpdateApkModel(mAppInfoBeanList.size()));
        Log.i(TAG, "getUpdateApkNum: " + AutoUpdate.getInstance().mUpdateNumDatas.size());*/

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
        mView.hideLoading();
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
     * 是否无数据
     *
     * @param position
     * @return
     */
    public boolean isNull(int position) {
        if (mDatas.size() == 0 || mDatas == null) {
            return true;
        }
        return false;
    }

    /**
     * 检测自动更新添加队列
     *
     * @param context
     */
    public void autoUpdate(Context context) {

        //检测网络获取更新包数据
        if (!NetworkUtils.isNetworkConnected(context)) {
            PromptUtils.toast(mContext, MyApp.getContext().getResources().getString(R.string.no_network));
            return;
        }

        final List appList = UpdateUtils.getAppList();
        //mAppInfoBeanList = UpdateUtils.getAppInfoBeanList();
        /*AppInfo appInfo1 = new AppInfo();
        appInfo1.setPackageName("cn.cibntv.ott");
        appInfo1.setVersionCode(4);
        AppInfo appInfo2 = new AppInfo();
        appInfo2.setPackageName("打怪");
        appInfo2.setVersionCode(4);
        appList.add(appInfo1);
        appList.add(appInfo2);*/
        final DownloadManager mDownloadManager = DownloadManager.getInstance(context);
        Log.i(TAG, "autoUpdate: " + appList.toString());
        CanCall<ListResult<AppInfo>> listResultCanCall = HttpManager.getApiService().checkUpdate(appList);
        listResultCanCall.enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                List<AppInfo> data = response.body().getData();

                //发送数量
                EventBus.getDefault().post(new UpdateApkModel(data.size()));
                Log.i(TAG, "getUpdateApkNum: " + AutoUpdate.getInstance().mUpdateNumDatas.size());
                //判断是否开启自动更新
                boolean isAutoUpdate = PreferencesUtils.getBoolean(MyApp.getContext(), "AUTO_UPDATE", false);
                if (isAutoUpdate == false) {
                    return;
                }
                //添加队列
                addAutoUpdateTask(mDownloadManager, data);
                Log.i(TAG, "autoUpdate: " + data.size());
                mView.refreshAll();
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
            if (downloadTask == null) {
                downloadTask = new DownloadTask();
                String md5 = MD5.MD5(downloadUrl);
                downloadTask.setFileName(md5);
                downloadTask.setId(md5);
                downloadTask.setUrl(downloadUrl);
                mDownloadManager.addDownloadTask(downloadTask, new DownloadTaskListener() {
                    @Override
                    public void onPrepare(DownloadTask downloadTask) {

                    }

                    @Override
                    public void onStart(DownloadTask downloadTask) {

                    }

                    @Override
                    public void onDownloading(DownloadTask downloadTask) {

                    }

                    @Override
                    public void onPause(DownloadTask downloadTask) {

                    }

                    @Override
                    public void onCancel(DownloadTask downloadTask) {

                    }

                    @Override
                    public void onCompleted(DownloadTask downloadTask) {

                    }

                    @Override
                    public void onError(DownloadTask downloadTask, int errorCode) {

                    }
                });
            }
        }
    }

    /**
     * 获取可更新app数量
     *
     * @return
     */
    public void getUpdateApkNum(int position) {
        try {
            AutoUpdate.getInstance().mUpdateNumDatas.remove(0);
            //发送数量
            EventBus.getDefault().post(new UpdateApkModel(AutoUpdate.getInstance().mUpdateNumDatas.size()));
            Log.i(TAG, "getUpdateApkNum: " + AutoUpdate.getInstance().mUpdateNumDatas.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getUpdateApkNum: " + AutoUpdate.getInstance().mUpdateNumDatas.size());
    }

    /**
     * 获取指定item位置
     * @param url
     * @return
     */
    public int getItemPosition(String url) {

        for (int i=0;i<mDatas.size();i++){
            String downloadUrl = mDatas.get(i).getDownloadUrl();
            if(downloadUrl.equals(url)){
                return i;
            }else if(MD5.MD5(downloadUrl).equals(url)){
                return i;
            }
        }

        return 0;

    }

    /**
     * 静默安装应用
     */
    public void installApp(String saveDirPath,int position,String url) {
        mDatas.get(position).setInstalling(true);//开始安装
        String fliePath = mDatas.get(position).getFliePath();
        int result = InstallPkgUtils.installApp(saveDirPath);
        if (result == 0) {
            EventBus.getDefault().post(new UpdateApkInstallModel(0,mDatas.get(position).getAppName(),url));
        } else {
            EventBus.getDefault().post(new UpdateApkInstallModel(1,mDatas.get(position).getAppName(),url));
        }
    }

}
