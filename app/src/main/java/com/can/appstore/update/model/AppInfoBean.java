package com.can.appstore.update.model;

import android.graphics.drawable.Drawable;

import com.can.appstore.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shenpx on 2016/10/12 0012.
 * app信息bean
 */

public class AppInfoBean {

    private String packageName;
    private String appName;
    private String iconUrl;//图标，url
    private int appSize;
    private String appSizeStr;
    private String versionCode;
    private String downloadUrl;
    private int progress;
    private boolean install;//是否已安装
    private String versionName;
    private Drawable icon;//图标，删除后期
    private String fliePath;//路径
    private boolean isInstalling;//是否开始安装
    private boolean isUpdated;//更新完成
    private boolean isInstalled;//安装结束
    private boolean isInstalledFalse;//安装失败
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public boolean getInstalledFalse() {
        return isInstalledFalse;
    }

    public void setInstalledFalse(boolean installedFalse) {
        isInstalledFalse = installedFalse;
    }

    public boolean getUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public boolean getInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }


    public boolean getIsInstalling() {
        return isInstalling;
    }

    public void setInstalling(boolean installing) {
        isInstalling = installing;
    }

    public String getFliePath() {
        return fliePath;
    }

    public void setFliePath(String fliePath) {
        this.fliePath = fliePath;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean getInstall() {
        return install;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getAppSize() {
        return appSize;
    }

    public void setAppSize(int appSize) {
        this.appSize = appSize;
    }

    public String getAppSizeStr() {
        return appSizeStr;
    }

    public void setAppSizeStr(String appSizeStr) {
        this.appSizeStr = appSizeStr;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "AppInfoBean{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", appSize='" + appSize + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", progress_updateapp=" + progress +
                '}';
    }

    /**
     * AppInfoBean装换为AppInfo
     *
     * @param bean
     * @return
     */
    public static AppInfo getAppInfo(AppInfoBean bean) {
        if (bean != null) {
            AppInfo appInfo = new AppInfo();
            appInfo.setName(bean.getAppName());
            appInfo.setPackageName(bean.getPackageName());
            appInfo.setVersionCode(Integer.parseInt(bean.getVersionCode()));
            appInfo.setIcon(bean.getIconUrl());
            appInfo.setMd5(bean.getMd5());
            appInfo.setVersionName(bean.getVersionName());
            appInfo.setUrl(bean.getDownloadUrl());
            appInfo.setSize(bean.getAppSize());
            appInfo.setSizeStr(bean.getAppSizeStr());
            return appInfo;
        } else {
            return null;
        }
    }

    /**
     * AppInfo装换为AppInfoBean
     *
     * @param bean
     * @return
     */
    public static AppInfoBean getAppInfoBean(AppInfo bean) {
        if (bean != null) {
            AppInfoBean appInfoBean = new AppInfoBean();
            appInfoBean.setAppName(bean.getName());
            appInfoBean.setPackageName(bean.getPackageName());
            appInfoBean.setVersionCode(String.valueOf(bean.getVersionCode()));
            appInfoBean.setIconUrl(bean.getIcon());//图片url
            appInfoBean.setMd5(bean.getMd5());//MD5值
            appInfoBean.setVersionName(bean.getVersionName());//版本名
            appInfoBean.setDownloadUrl(bean.getUrl());//下载地址
            appInfoBean.setAppSize(bean.getSize());//apk大小
            appInfoBean.setAppSizeStr(bean.getSizeStr());
            return appInfoBean;
        } else {
            return null;
        }
    }

    /**
     * List<AppInfo>转化为List<AppInfoBean>
     * @param mList
     * @return
     */
    public static List<AppInfoBean> getAppInfoBeanList(List<AppInfo> mList){

        List<AppInfoBean> mAppInfoBeanList = new ArrayList<AppInfoBean>();
        for (int i=0;i<mList.size();i++){
            AppInfo bean = mList.get(i);
            AppInfoBean appInfoBean = new AppInfoBean();
            appInfoBean.setAppName(bean.getName());
            appInfoBean.setPackageName(bean.getPackageName());
            appInfoBean.setVersionCode(String.valueOf(bean.getVersionCode()));
            appInfoBean.setIconUrl(bean.getIcon());//图片url
            appInfoBean.setMd5(bean.getMd5());//MD5值
            appInfoBean.setVersionName(bean.getVersionName());//版本名
            appInfoBean.setDownloadUrl(bean.getUrl());//下载地址
            appInfoBean.setAppSize(bean.getSize());//apk大小
            appInfoBean.setAppSizeStr(bean.getSizeStr());
            mAppInfoBeanList.add(appInfoBean);
        }
        return mAppInfoBeanList;
    }

    /**
     * List<AppInfoBean>转化为List<AppInfo>
     * @param mList
     * @return
     */
    public static List<AppInfo> getAppInfoList(List<AppInfoBean> mList){

        List<AppInfo> mAppInfoList = new ArrayList<AppInfo>();
        for (int i=0;i<mList.size();i++){
            AppInfoBean bean = mList.get(i);
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(bean.getPackageName());
            appInfo.setVersionCode(Integer.parseInt(bean.getVersionCode()));
            mAppInfoList.add(appInfo);
        }
        return mAppInfoList;
    }

}
