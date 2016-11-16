package cn.can.downloadlib;

/**
 * ================================================
 * 作    者：朱洪龙
 * 版    本：1.0
 * 创建日期：2016/10/20
 * 描    述：下载器
 * 修订历史：
 * ================================================
 */
public interface AppInstallListener {

    void onInstalling(DownloadTask downloadTask);
    void onInstallSucess(String id);
    void onInstallFail(String id);

    public static final int APP_INSTALLING = 8;
    public static final int APP_INSTALL_SUCESS = 9;
    public static final int APP_INSTALL_FAIL = 10;
}
