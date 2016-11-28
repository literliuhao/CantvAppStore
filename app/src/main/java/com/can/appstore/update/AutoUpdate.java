package com.can.appstore.update;

import android.content.Context;

import com.can.appstore.MyApp;
import com.can.appstore.entity.AppInfo;
import com.can.appstore.entity.ListResult;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.utils.UpdateUtils;

import java.util.List;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.MD5;
import cn.can.tvlib.utils.NetworkUtils;
import cn.can.tvlib.utils.PreferencesUtils;
import cn.can.tvlib.utils.ToastUtils;
import retrofit2.Response;

/**
 * 自动更新添加队列
 * Created by shenpx on 2016/11/25 0025.
 */

public class AutoUpdate {

    /**
     * 检测自动更新添加队列
     * @param context
     */
    public static void autoUpdate(Context context) {
        //判断是否开启自动更新
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
        final List date =  AppInfoBean.getAppInfoList(appList);
        final DownloadManager mDownloadManager  = DownloadManager.getInstance(context);

        CanCall<ListResult<AppInfo>> listResultCanCall = HttpManager.getApiService().checkUpdate(date);
        listResultCanCall.enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                List<AppInfo> data = response.body().getData();
                //添加队列
                addAutoUpdateTask(mDownloadManager ,data);
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {

            }
        });
    }

    //添加自动更新队列
    private static void addAutoUpdateTask(DownloadManager mDownloadManager, List<AppInfo> data) {
        for (int i = 0; i <data.size() ; i++) {
            String downloadUrl = data.get(i).getUrl();
            DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
            if(downloadTask == null){
                downloadTask = new DownloadTask();
                String md5 = MD5.MD5(downloadUrl);
                downloadTask.setFileName(md5 + ".apk");
                downloadTask.setId(md5);
                downloadTask.setUrl(downloadUrl);
                mDownloadManager.addDownloadTask(downloadTask,null);
            }
        }
    }
}
