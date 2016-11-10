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
public interface DownloadTaskListener {
    void onPrepare(DownloadTask downloadTask);
    void onStart(DownloadTask downloadTask);
    void onDownloading(DownloadTask downloadTask);
    void onPause(DownloadTask downloadTask);
    void onCancel(DownloadTask downloadTask);
    void onCompleted(DownloadTask downloadTask);
    void onError(DownloadTask downloadTask,int errorCode);
    void onInstall(DownloadTask downloadTask);

    int DOWNLOAD_ERROR_FILE_NOT_FOUND = -1;
    int DOWNLOAD_ERROR_IO_ERROR = -2;
    int DOWNLOAD_ERROR_NETWORK_ERROR = -3;
    int DOWNLOAD_ERROR_UNKONW_ERROR = -4;

}
