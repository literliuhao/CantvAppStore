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
import cn.can.tvlib.utils.NetworkUtils;
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
        mUpdateNumDatas = new ArrayList<AppInfoBean>();
        mUpdateApkNumDatas = new ArrayList<AppInfoBean>();
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
        mUpdateNumDatas.clear();
        mUpdateApkNumDatas.clear();

        //检测网络获取更新包数据
        if (!NetworkUtils.isNetworkConnected(context)) {
            PromptUtils.toast(context.getApplicationContext(), context.getApplicationContext().getResources().getString(R.string.no_network));
            EventBus.getDefault().post(new UpdateApkModel(0));
            return;
        }

        final List appList = UpdateUtils.getAppList();
//        AppInfo appInfo1 = new AppInfo();
//        appInfo1.setPackageName("cn.cibntv.ott");
//        appInfo1.setVersionCode(4);
//        AppInfo appInfo2 = new AppInfo();
//        appInfo2.setPackageName("打怪");
//        appInfo2.setVersionCode(4);
//        appList.add(appInfo1);
//        appList.add(appInfo2);
        final DownloadManager mDownloadManager = DownloadManager.getInstance(context);
        Log.i(TAG, "autoUpdate: " + appList.toString());
        CanCall<ListResult<AppInfo>> listResultCanCall = HttpManager.getApiService().checkUpdate(appList);
        listResultCanCall.enqueue(new CanCallback<ListResult<AppInfo>>() {
            @Override
            public void onResponse(CanCall<ListResult<AppInfo>> call, Response<ListResult<AppInfo>> response) throws Exception {
                List<AppInfo> data = response.body().getData();
                Log.i(TAG, "getUpdateApkNum: " + data.size());
                //判断是否开启自动更新
                EventBus.getDefault().post(new UpdateApkModel(data.size()));
            }

            @Override
            public void onFailure(CanCall<ListResult<AppInfo>> call, CanErrorWrapper errorWrapper) {
                EventBus.getDefault().post(new UpdateApkModel(0));
            }
        });
    }
}
