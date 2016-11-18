package com.can.appstore.installpkg;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.can.appstore.MyApp;
import com.can.appstore.R;
import com.can.appstore.installpkg.utils.InstallPkgUtils;
import com.can.appstore.update.model.AppInfoBean;
import com.can.appstore.update.utils.UpdateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenpx on 2016/11/9 0009.
 */

public class InstallPresenter implements InstallContract.Presenter {

    private static final String TAG = "installPresenter";
    private InstallContract.View mView;
    private Context mContext;
    private String mPath;
    private int mSdTotalSize;
    private int mSdSurplusSize;
    private String mSdAvaliableSize;
    private List<AppInfoBean> mDatas;//安装包集合

    public InstallPresenter(InstallContract.View mView, Context context) {
        this.mView = mView;
        this.mContext = context;
        mDatas = new ArrayList<AppInfoBean>();
    }

    @Override
    public void getInstallPkgList() {
        mDatas.clear();
        mView.showInstallPkgList(mDatas);
        mView.showLoadingDialog();
        InstallPkgUtils.myFiles.clear();
        mPath = Environment.getExternalStorageDirectory().getPath().toString() + File.separator + "Movies";
        //mPath = MyApp.mContext.getExternalCacheDir().getPath().toString()+ File.separator;
        List appList = InstallPkgUtils.FindAllAPKFile(mPath);
        mDatas.clear();
        if (appList.size() < 1) {
            mView.showNoData();
        } else {
            mView.hideLoadingDialog();
            mView.hideNoData();
            mDatas.addAll(appList);
            //setNum(0);
            mView.showInstallPkgList(mDatas);
        }
    }

    @Override
    public void getSDInfo() {
        mSdTotalSize = UpdateUtils.getSDTotalSize();
        mSdSurplusSize = UpdateUtils.getSDSurplusSize();
        mSdAvaliableSize = UpdateUtils.getSDAvaliableSize();
        mView.showSDProgressbar(mSdSurplusSize, mSdTotalSize, mSdAvaliableSize);
    }

    /**
     * 删除全部
     *
     * @param
     */
    @Override
    public void deleteAll() {
        mDatas.clear();
        mView.refreshAll();
        mView.showNoData();
        setNum(0);
    }

    /**
     * 删除部分
     */
    @Override
    public void deleteInstall() {
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            AppInfoBean bean = mDatas.get(i);
            if (bean.getInstall()) {
                mDatas.remove(i);
//                InstallPkgUtils.deleteApkPkg(mDatas.get(i).getFliePath());//可以删除安装包
                mView.refreshItem(i);
            }
        }
        setNum(0);
    }

    /**
     * 删除item
     *
     * @param position
     */
    @Override
    public void deleteOne(int position) {
        mDatas.remove(position);
//        InstallPkgUtils.deleteApkPkg(mDatas.get(position).getFliePath());//可以删除安装包
        mView.refreshAll();
        setNum(0);
    }

    @Override
    public void refreshInstallPkgList() {

    }

    public AppInfoBean getItem(int position) {
        if (position < 0 || position > mDatas.size()) {
            return null;
        }
        AppInfoBean appInfoBean = mDatas.get(position);
        return appInfoBean;
    }

    /**
     * 释放资源
     */
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
     * @param versonCode, int versonCode   && bean.getVersionCode().equals(String.valueOf(versonCode))
     */
    public void isInstalled(String packageName) {
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            AppInfoBean bean = mDatas.get(i);
            if (bean.getPackageName().equals(packageName) ) {
                if (bean.getInstall()) {
                    //bean.setInstall(true);
                    mView.refreshItem(i);
                    Toast.makeText(MyApp.mContext, packageName + "111111", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * 安装应用
     */
    public void installApk(int position) {
        mDatas.get(position).setInstalling(true);//开始安装
        //mInstallDatas.add(mDatas.get(position));//加入安装中集合
        mDatas.get(position).setInstall(true);//positon传递
        InstallPkgUtils.installApkFromF(MyApp.mContext,
                new File(mDatas.get(position).getFliePath()), true, mDatas.get(position).getPackageName());
    }
    /**
     * 静默安装应用
     */
    public void installApp(int position) {
        mDatas.get(position).setInstalling(true);//开始安装
        //mInstallDatas.add(mDatas.get(position));//加入安装中集合
        //mDatas.get(position).setInstall(true);//positon传递
        //mView.refreshItem(position);
        int result = InstallPkgUtils.installApp(mDatas.get(position).getFliePath());
        if(result == 0){
            mDatas.get(position).setInstalling(false);
            mDatas.get(position).setInstall(true);
            isInstalled(mDatas.get(position).getPackageName());
        }else{
            mDatas.get(position).setInstalling(false);
            //mView.refreshItem(position);
        }
    }

}
