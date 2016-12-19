package com.can.appstore.update;

import android.content.Context;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.ListResult;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.update.model.AppInfoBean;
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
import retrofit2.Response;

/**
 * 自动更新添加队列
 * Created by shenpx on 2016/11/25 0025.
 */

public class AutoUpdate {

    private static final String TAG = "autoUpdate";

    private static AutoUpdate instance = null;

    public List<AppInfoBean> mUpdateNumDatas;//未更新应用集合
    public List<AppInfoBean> mUpdateApkNumDatas;//未更新应用集合

    private AutoUpdate() {
    }

    public static AutoUpdate getInstance() {
        synchronized (AutoUpdate.class) {
            if (instance == null) {
                instance = new AutoUpdate();
            }
        }
        return instance;
    }

    /**
     * 检测自动更新添加队列
     *
     * @param context
     */
    public void autoUpdate(final Context context) {

        mUpdateNumDatas = new ArrayList<AppInfoBean>();
        mUpdateApkNumDatas = new ArrayList<AppInfoBean>();
        mUpdateNumDatas.clear();
        mUpdateApkNumDatas.clear();

        //检测网络获取更新包数据
        if (!NetworkUtils.isNetworkConnected(context)) {
            PromptUtils.toast(context.getApplicationContext(), context.getApplicationContext().getResources().getString(R.string.no_network));
            EventBus.getDefault().post(new UpdateApkModel(0));
            return;
        }

        final List appList = UpdateUtils.getAppList();
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
                Log.i(TAG, "getUpdateApkNum: " + data.size());
                //判断是否开启自动更新
                boolean isAutoUpdate = PreferencesUtils.getBoolean(context.getApplicationContext(), "AUTO_UPDATE", false);
                if (!isAutoUpdate) {
                    EventBus.getDefault().post(new UpdateApkModel(data.size()));
                    return;
                }else {
                    EventBus.getDefault().post(new UpdateApkModel(0));
                }
                //添加队列
                addAutoUpdateTask(mDownloadManager, data);
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                EventBus.getDefault().post(new UpdateApkModel(0));
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
                downloadTask.setFileName(data.get(i).getName()+"1");
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
}
