package com.can.appstore.update;

import android.content.Context;
import android.os.Handler;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.utils.UpdateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenpx on 2016/11/10 0010.
 */

public class UpdatePresenter implements UpdateContract.Presenter{

    private static final String TAG = "updatePresenter";
    private UpdateContract.View mView;
    private Context mContext;
    private String url;
    private int mSdTotalSize;
    private int mSdSurplusSize;
    private String mSdAvaliableSize;
    private List<AppInfoBean> mDatas;//已安装应用

    public UpdatePresenter(UpdateContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
        mDatas = new ArrayList<AppInfoBean>();
    }

    @Override
    public void getInstallPkgList(boolean isAutoUpdate) {
        mDatas.clear();
        mView.showInstallPkgList(mDatas);
        if (isAutoUpdate) {
            mView.hideNoData();
            mView.showStartAutoUpdate();
            return;
        }
        mView.showLoadingDialog();
        final List appList = UpdateUtils.getAppList();
        mDatas.clear();
        //进行网络请求获取更新包信息
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
                    mView.showInstallPkgList(mDatas);
                    //setNum(0);
                }
            }, 2000);
        }
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
}
