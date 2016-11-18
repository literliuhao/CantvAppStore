package com.can.appstore.update;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.can.appstore.MyApp;
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

/**
 * Created by shenpx on 2016/11/10 0010.
 */

public class UpdatePresenter implements UpdateContract.Presenter, DownloadTaskListener {

    private static final String TAG = "updatePresenter";
    private UpdateContract.View mView;
    private UpdateContract.UpdateView mUpdateView;
    private DownloadManager mDownloadManager;
    private Context mContext;
    private String url;
    private int mSdTotalSize;
    private int mSdSurplusSize;
    private String mSdAvaliableSize;
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
        mDatas.clear();
        date.clear();
        mView.showInstallPkgList(mDatas);
        if (isAutoUpdate) {
            mView.hideNoData();
            mView.showStartAutoUpdate();
            return;
        }
        mView.showLoadingDialog();
        final List appList = UpdateUtils.getAppList();
        mDatas.clear();
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
                    //setNum(0);
                }
            }, 2000);
        }
    }

    public List<AppInfoBean> getList(){
        return mDatas;
    }

    @Override
    public void getSDInfo() {
        mSdTotalSize = UpdateUtils.getSDTotalSize();
        mSdSurplusSize = UpdateUtils.getSDSurplusSize();
        mSdAvaliableSize = UpdateUtils.getSDAvaliableSize();
        mView.showSDProgressbar(mSdSurplusSize, mSdTotalSize, mSdAvaliableSize);
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
     * 初始化更新状态
     */
    public void initUpdateStatus(int position) {
        String downloadUrl = mDatas.get(position).getDownloadUrl();
        mDownloadManager = DownloadManager.getInstance(MyApp.mContext);
        DownloadTask curDownloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (curDownloadTask != null) {
            int downloadStatus = curDownloadTask.getDownloadStatus();
            //获取更新状态
            if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PREPARE || downloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                mUpdateView.refreshUpdateButton("等待中", true);
                mUpdateView.refreshUpdateProgress((int) curDownloadTask.getPercent(), false);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                mUpdateView.refreshUpdateButton("下载中", true);
                mUpdateView.refreshUpdateProgress((int) curDownloadTask.getPercent(), true);
            } else if (downloadStatus == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                mUpdateView.refreshUpdateButton("安装中", true);
                mUpdateView.refreshUpdateProgress((int) curDownloadTask.getTotalSize(), false);
            }
            mDownloadManager.addDownloadListener(curDownloadTask, UpdatePresenter.this);
        } else {
            mUpdateView.refreshUpdateButton("", false);
            mUpdateView.refreshUpdateProgress(0, false);
        }
    }

    /**
     * 添加更新任务
     */
    public void addUpdateTask(int position) {
        String downloadUrl = mDatas.get(position).getDownloadUrl();
        mDownloadManager = DownloadManager.getInstance(mContext);
        DownloadTask downloadTask = mDownloadManager.getCurrentTaskById(MD5.MD5(downloadUrl));
        if (downloadTask != null) {
            int status = downloadTask.getDownloadStatus();

            if (status == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING
                    || status == DownloadStatus.DOWNLOAD_STATUS_PREPARE
                    || status == DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                //mDownloadManager.addDownloadListener(downloadTask, UpdatePresenter.this);
                return;
            } else if (status == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                mDownloadManager.resume(downloadTask.getId());
            }
        } else {
            downloadTask = new DownloadTask();
            String md5 = MD5.MD5(downloadUrl);
            downloadTask.setFileName(md5);
            downloadTask.setId(md5);
            downloadTask.setSaveDirPath(mContext.getExternalCacheDir().getPath() + "/");
            downloadTask.setUrl(downloadUrl);
            mDownloadManager.addDownloadTask(downloadTask, UpdatePresenter.this);
        }
    }


    /**
     * 更新进度监听
     *
     * @param downloadTask
     */
    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.i(TAG, "onPrepare");
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PREPARE) {
            mUpdateView.refreshUpdateButton("等待中", true);
            mUpdateView.refreshUpdateProgress(0, false);
        }
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.i(TAG, "onStart");
        if (downloadTask.getDownloadStatus() == 0) {
            mUpdateView.refreshUpdateButton("等待中", true);
            mUpdateView.refreshUpdateProgress(0, false);
        }
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
        Log.i(TAG, "onDownloading");
        mUpdateView.refreshUpdateButton("下载中", true);
        mUpdateView.refreshUpdateProgress((int) downloadTask.getPercent(), true);
    }

    @Override
    public void onPause(DownloadTask downloadTask) {
        Log.i(TAG, "onPause");
        mUpdateView.refreshUpdateButton("等待中", true);
        mUpdateView.refreshUpdateProgress((int) downloadTask.getPercent(), false);
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {
        Log.i(TAG, "onCancel");
//        mUpdateView.refreshUpdateButton("等待中", true);
//        mUpdateView.refreshUpdateProgress((int) downloadTask.getPercent(), false);
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        Log.i(TAG, "onCompleted");
        if (downloadTask != null) {
            mUpdateView.refreshUpdateButton("安装", true);
            mUpdateView.refreshUpdateProgress((int) downloadTask.getTotalSize(), false);
            mUpdateView.showCompleted("下载完成");
        }
    }

    @Override
    public void onError(DownloadTask downloadTask, int errorCode) {
        Log.i(TAG, "onError");
        switch (errorCode) {
            case DOWNLOAD_ERROR_FILE_NOT_FOUND:
                mUpdateView.showCompleted("未找到下载文件");
                break;
            case DOWNLOAD_ERROR_IO_ERROR:
                mUpdateView.showCompleted("IO异常");
                break;
            case DOWNLOAD_ERROR_NETWORK_ERROR:
                mUpdateView.showCompleted("网络异常，请重试！");
                break;
            /*case DOWNLOAD_ERROR_UNKONW_ERROR:
                mUpdateView.showCompleted("未知错误");
                break;*/
            default:
                break;
        }
    }
}
