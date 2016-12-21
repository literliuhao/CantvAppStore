package cn.can.downloadlib;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.dataeye.sdk.api.app.channel.DCResource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import cn.can.downloadlib.utils.ShellUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * ================================================
 * 作    者：朱洪龙
 * 版    本：1.0
 * 创建日期：2016/10/20
 * 描    述：下载器
 * 修订历史：
 * ================================================
 */
public class DownloadTask implements Runnable {
    private static final String TAG = "DownloadTaskRunnable";
    private DownloadDBEntity mDbEntity;
    private DownloadDao mDownloadDao;
    private DownloadManager mDownloadManager;
    private OkHttpClient mOkHttpClient;
    private String mId;
    private long mTotalSize;
    private long mDownloadedSize;
    private String mUrl;
    private String mIcon;
    private String mAppId;
    private String mPkg;
    private String mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private RandomAccessFile mRandomAccessFile;
    private int UPDATE_SIZE = 512 * 1024;    // 512k存储一次
    private int mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;

    private String mFileName;

    private List<DownloadTaskListener> mDownloadlisteners = new ArrayList<>();
    private AppInstallListener mAppListener;

    public DownloadTask() {
    }

    public DownloadTask(String url) {
        mId = MD5.MD5(url);
        mUrl = url;
    }

    public static DownloadTask parse(DownloadDBEntity entity) {
        DownloadTask task = new DownloadTask();
        task.setDownloadStatus(entity.getDownloadStatus());
        task.setId(entity.getDownloadId());
        task.setUrl(entity.getUrl());
        task.setIcon(entity.getIcon());
        task.setFileName(entity.getFileName());
        task.setSaveDirPath(entity.getSaveDirPath());
        task.setCompletedSize(entity.getDownloadedSize());
        task.setDbEntity(entity);
        task.setTotalSize(entity.getTotalSize());
        return task;
    }

    @Override
    public void run() {
        if (mDownloadStatus == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
            return;
        }
        mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_PREPARE;
        onPrepare();
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            mDbEntity = mDownloadDao.load(mId);
            String path = mSaveDirPath + File.separator + mFileName;
            mRandomAccessFile = new RandomAccessFile(path, "rwd");
            if (mDbEntity != null) {
                mDownloadedSize = mDbEntity.getDownloadedSize();
                mTotalSize = mDbEntity.getTotalSize();
            }
            if (mRandomAccessFile.length() < mDownloadedSize) {
                mDownloadedSize = mRandomAccessFile.length();
            }
            long fileLength = mRandomAccessFile.length();
            if (fileLength != 0 && mTotalSize <= fileLength) {
                mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
                mTotalSize = mDownloadedSize = fileLength;
                mDbEntity = new DownloadDBEntity(mId, mTotalSize, mTotalSize, mUrl, mSaveDirPath,
                        mFileName, mDownloadStatus, mIcon, mAppId, mPkg);
                mDownloadDao.insertOrReplace(mDbEntity);
                onCompleted();
                return;
            }
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_START;
            onStart();
            Log.e(TAG,"*****onStart*****" +mUrl + "DownloadedSize:" + mDownloadedSize);
            Request request = new Request.Builder()
                    .url(mUrl)
                    .header("RANGE", "bytes=" + mDownloadedSize + "-")
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
                    if (mTotalSize <= 0) {
                        mTotalSize = responseBody.contentLength();
                        /** 下载过程中删除任务，此处mDbEntity为空 xingzl 2016-11-15 15:37:43 start*/
                        if (mDbEntity == null) {
                            mDbEntity = new DownloadDBEntity(mId, mTotalSize, mDownloadedSize, mUrl, mSaveDirPath,
                                    mFileName, mDownloadStatus, mIcon, mAppId, mPkg);
                        } else {
                            mDbEntity.setTotalSize(mTotalSize);
                        }
                        mDownloadDao.update(mDbEntity);
                    }
                    if (TextUtils.isEmpty(response.header("Content-Range"))) {
                        //返回的没有Content-Range 不支持断点下载 需要重新下载
                        File alreadyDownloadedFile = new File(path);
                        if (alreadyDownloadedFile.exists()) {
                            alreadyDownloadedFile.delete();
                        }
                        mRandomAccessFile = new RandomAccessFile(path, "rwd");
                        mDownloadedSize = 0;
                    }
                    mRandomAccessFile.seek(mDownloadedSize);
                    inputStream = responseBody.byteStream();
                    bis = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[256 * 1024];
                    int length;
                    int buffOffset = 0;
                    if (mDbEntity == null) {
                        mDbEntity = new DownloadDBEntity(mId, mTotalSize, 0L, mUrl, mSaveDirPath,
                                mFileName, mDownloadStatus, mIcon, mAppId, mPkg);
                        mDownloadDao.insertOrReplace(mDbEntity);
                    }
                    while ((length = bis.read(buffer)) > 0 && mDownloadStatus != DownloadStatus
                            .DOWNLOAD_STATUS_CANCEL && mDownloadStatus != DownloadStatus
                            .DOWNLOAD_STATUS_PAUSE) {
                        mRandomAccessFile.write(buffer, 0, length);
                        mDownloadedSize += length;
                        buffOffset += length;
                        if (buffOffset >= UPDATE_SIZE) {
                            // Update download information database
                            buffOffset = 0;
                            //考虑是否需要频繁进行数据库的读取，如果在下载过程程序崩溃的话，程序不会保存最新的下载进度,并且下载过程不会更新进度
                            if (DownloadStatus.DOWNLOAD_STATUS_CANCEL != mDownloadStatus) {
                                mDbEntity.setDownloadedSize(mDownloadedSize);
                                mDownloadDao.update(mDbEntity);
                                onDownloading();
                            }
                        }
                    }
                    if (DownloadStatus.DOWNLOAD_STATUS_CANCEL != mDownloadStatus) {
                        mDbEntity.setDownloadedSize(mDownloadedSize);
                        mDownloadDao.update(mDbEntity);
                        onDownloading();
                    }
                }
            } else {
                mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
                onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR);
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "*******FileNotFoundException*******");
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_FILE_NOT_FOUND);
            return;
        } catch (SocketException e) {
            Log.d(TAG, "*******SocketException*******");
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_NETWORK_ERROR);
            return;
        } catch (IOException e) {
            Log.d(TAG, "*******IOException*******");
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR);
            return;
        } catch (Exception e) {
            Log.d(TAG, "*******Exception*******");
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_UNKONW_ERROR);
            return;
        } finally {
            if (mDbEntity != null) {
                mDbEntity.setDownloadedSize(mDownloadedSize);
                mDownloadDao.update(mDbEntity);
            }

            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mRandomAccessFile != null) {
                try {
                    mRandomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /** 添加mTotalSize！=0 处理 xzl 2016-12-6 17:38:12*/
        if (mTotalSize == mDownloadedSize&&mTotalSize!=0) {
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
        }
        mDbEntity.setDownloadStatus(mDownloadStatus);
        mDownloadDao.update(mDbEntity);

        switch (mDownloadStatus) {
            case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                onCompleted();
                break;
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                onPause();
                break;
            case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                mDownloadDao.delete(mDbEntity);
                File temp = new File(mSaveDirPath + File.separator + mFileName);
                if (temp.exists())
                    temp.delete();
                onCancel();
                break;
        }
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public float getPercent() {
        return mTotalSize == 0 ? 0 : mDownloadedSize * 100 / mTotalSize;
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public void setTotalSize(long toolSize) {
        this.mTotalSize = toolSize;
    }

    public long getCompletedSize() {
        return mDownloadedSize;
    }

    public void setCompletedSize(long completedSize) {
        this.mDownloadedSize = completedSize;
    }

    public String getSaveDirPath() {
        return mSaveDirPath;
    }

    public void setSaveDirPath(String saveDirPath) {
        File file = new File(saveDirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.mSaveDirPath = saveDirPath;
    }

    public int getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.mDownloadStatus = downloadStatus;
    }

    public void setDownloadDao(DownloadDao downloadDao) {
        this.mDownloadDao = downloadDao;
    }

    public void setDbEntity(DownloadDBEntity dbEntity) {
        this.mDbEntity = dbEntity;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setHttpClient(OkHttpClient client) {
        this.mOkHttpClient = client;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public String getFilePath() {
        return getSaveDirPath() + File.separator + getFileName();
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public String getAppId() {
        return mAppId;
    }

    public void setAppId(String mAppId) {
        this.mAppId = mAppId;
    }

    public String getPkg() {
        return mPkg;
    }

    public void setPkg(String mPkg) {
        this.mPkg = mPkg;
    }

    /**
     * 取消任务，删除下载的文件
     */
    public void cancel() {
        mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_CANCEL;
        File temp = new File(mSaveDirPath + File.separator + mFileName);
        if (temp.exists()) {
            temp.delete();
        }
    }

    public void pause() {
        mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_PAUSE;
    }

    private void onPrepare() {
        for (DownloadTaskListener listener : mDownloadlisteners) {
            listener.onPrepare(this);
        }
    }

    private void onStart() {
        for (DownloadTaskListener listener : mDownloadlisteners) {
            listener.onStart(this);
        }
    }

    private void onDownloading() {
        for (DownloadTaskListener listener : mDownloadlisteners) {
            listener.onDownloading(this);
        }
    }

    private void onCompleted() {
        ShellUtils.execCommand("chmod 666 "+ mSaveDirPath+File.separator+mFileName, false);
        for (DownloadTaskListener listener : mDownloadlisteners) {
            DCResource.onDownloadSuccess(this.getFileName());
            listener.onCompleted(this);
        }
        mAppListener.onInstalling(this);
    }

    private void onPause() {
        for (DownloadTaskListener listener : mDownloadlisteners) {
            listener.onPause(this);
        }
    }

    private void onCancel() {
        for (DownloadTaskListener listener : mDownloadlisteners) {
            listener.onCancel(this);
        }
    }

    private void onError(int errorCode) {
        for (DownloadTaskListener listener : mDownloadlisteners) {
            listener.onError(this, errorCode);
            if (DownloadTaskListener.DOWNLOAD_ERROR_NETWORK_ERROR == errorCode) {
                TaskManager.mErrorTaskQueue.add(this.getId()); // 处理任务异常时，获取异常任务taskId
            }
        }
    }

    public void setAppListener(AppInstallListener listener) {
        mAppListener = listener;
    }

    public void addDownloadListener(DownloadTaskListener listener) {
        /**添加重复元素判断 xzl */
        if(!mDownloadlisteners.contains(listener)){
            mDownloadlisteners.add(listener);
        }
    }

    /**
     * @param listener
     */
    public void removeDownloadListener(DownloadTaskListener listener) {
        if (listener != null) {
            mDownloadlisteners.remove(listener);
        }
    }

    public void removeAllDownloadListener() {
        this.mDownloadlisteners.clear();
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.mDownloadManager = downloadManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DownloadTask that = (DownloadTask) o;

        if (mId != null ? !mId.equals(that.mId) : that.mId != null) {
            return false;
        }

        return (mUrl != null ? mUrl.equals(that.mUrl) : that.mUrl == null);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "DownloadTask{" +
                "mId='" + mId + '\'' +
                ", mTotalSize=" + mTotalSize +
                ", mDownloadedSize=" + mDownloadedSize +
                ", mUrl='" + mUrl + '\'' +
                ", mIcon='" + mIcon + '\'' +
                ", mSaveDirPath='" + mSaveDirPath + '\'' +
                ", mDownloadStatus=" + mDownloadStatus +
                ", mFileName='" + mFileName + '\'' +
                '}';
    }
}


