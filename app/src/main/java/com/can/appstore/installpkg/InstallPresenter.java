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

import cn.can.tvlib.utils.StringUtils;
import cn.can.tvlib.utils.SystemUtil;

/**
 * Created by shenpx on 2016/11/9 0009.
 */

public class InstallPresenter implements InstallContract.Presenter {

    private static final String TAG = "installPresenter";
    private InstallContract.View mView;
    private Context mContext;
    private String mPath;
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
        //InstallPkgUtils.myFiles.clear();
        //mPath = Environment.getExternalStorageDirectory().getPath().toString();
        mPath = Environment.getExternalStorageDirectory().getPath().toString() + File.separator + "Movies";
        //mPath = MyApp.mContext.getExternalCacheDir().getPath().toString();
        List appList = InstallPkgUtils.FindAllAPKFile(mPath);
        mDatas.clear();
        if (appList.size() < 1) {
            mView.hideLoadingDialog();
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
        long freeSize = SystemUtil.getSDCardAvailableSpace();
        long totalSize = SystemUtil.getSDCardTotalSpace();
        int progress = (int) (((totalSize - freeSize) * 100) / totalSize);
        String freeStorage = mContext.getResources().getString(R.string.uninsatll_manager_free_storage) + StringUtils.formatFileSize(freeSize, false);
        mView.showSDProgressbar(progress, freeStorage);
    }

    /**
     * 删除全部
     *
     * @param
     */
    @Override
    public void deleteAll() {
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            AppInfoBean bean = mDatas.get(i);
//                InstallPkgUtils.deleteApkPkg(mDatas.get(i).getFliePath());//可以删除安装包
            mDatas.remove(i);
        }
        //mDatas.clear();
        mView.refreshAll();
        mView.showNoData();
        setNum(0);
        getSDInfo();
    }

    /**
     * 删除部分
     */
    @Override
    public void deleteInstall() {
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            AppInfoBean bean = mDatas.get(i);
            if (bean.getInstall()) {
//                InstallPkgUtils.deleteApkPkg(mDatas.get(i).getFliePath());//可以删除安装包
                mDatas.remove(i);
                //mView.removeItem(i);
            }
        }
        mView.refreshAll();
        if (mDatas.size() == 0) {
            mView.showNoData();
        }
        setNum(0);
        getSDInfo();
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
        if (mDatas.size() == 0) {
            mView.showNoData();
        }
        setNum(0);
        getSDInfo();
        /*if (position == mDatas.size() - 1) {
            mView.deleteLastItem(position);
        }*/
    }

    @Override
    public void refreshInstallPkgList() {

    }

    /**
     * 获取指定位置item
     *
     * @param position
     * @return
     */
    public AppInfoBean getItem(int position) {
        if (position < 0 || position > mDatas.size()) {
            return null;
        }
        AppInfoBean appInfoBean = mDatas.get(position);
        return appInfoBean;
    }

    /**
     * 是否是最后一个item
     *
     * @param position
     * @return
     */
    public boolean isLastItem(int position) {
        if (position <= 0) {
            return false;
        }
        if (position == mDatas.size() - 1) {
            return true;
        }
        return false;
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
     * 刷新图标（可能多重版本）通过广播获取安装完成刷新ui  +&& bean.getVersionCode().equals(String.valueOf(versionCode))
     *
     * @param packageName
     * @param //int       versonCode   && bean.getVersionCode().equals(String.valueOf(versonCode))
     */
    public void isInstalled(String packageName, int versionCode) {
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            AppInfoBean bean = mDatas.get(i);
            if (bean.getPackageName().equals(packageName) && bean.getVersionCode().equals(String.valueOf(versionCode))) {
                if (bean.getInstall()) {
                    //bean.setInstall(true);
                    mView.refreshAll();
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
        //mDatas.get(position).setInstalling(true);//开始安装
        //mInstallDatas.add(mDatas.get(position));//加入安装中集合
        //mDatas.get(position).setInstall(true);//positon传递
        //mView.refreshAll();
        //String path = Environment.getExternalStorageDirectory().getPath().toString() + File.separator + "Movies"+File.separator;
        String fliePath = mDatas.get(position).getFliePath();
        int result = InstallPkgUtils.installApp2(fliePath);
        if (result == 0) {
            mDatas.get(position).setInstalling(false);
            mDatas.get(position).setInstall(true);
            //isInstalled(mDatas.get(position).getPackageName());
            //mView.refreshAll();
        } else {
            mDatas.get(position).setInstalling(true);
            mDatas.get(position).setInstall(false);
            mDatas.get(position).setInstalledFalse(true);
            mView.refreshAll();
        }
    }

    /**
     * 判断安装包版本与已安装应用大小差异
     *
     * @param context
     * @param position
     * @return
     */
    public int getVersonCode(Context context, int position) {
        String packageName = mDatas.get(position).getPackageName();
        if (!mDatas.get(position).getInstall()) {
            return 1;
        }
        int installedVersonCode = UpdateUtils.getVersonCode(context, packageName);
        String versionCode = mDatas.get(position).getVersionCode();
        int currentVersionCode = Integer.parseInt(versionCode);
        if (currentVersionCode < installedVersonCode) {
            return 0;
        } else if (currentVersionCode >= installedVersonCode) {
            return 1;
        } else {
            return 2;
        }
    }

}
