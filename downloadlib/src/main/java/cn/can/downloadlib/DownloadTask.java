package cn.can.downloadlib;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import cn.can.downloadlib.entity.DownloadDBEntity;
import cn.can.downloadlib.gen.DownloadDBEntityDao;
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
    private DownloadDBEntity mDbEntity;
    private DownloadDBEntityDao mDownloadDao;
    private DownloadManager mDownloadManager;
    private OkHttpClient mOkHttpClient;
    private String mId;
    private long mTotalSize;
    private long mDownloadedSize;
    private String mUrl;
    private String mSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private RandomAccessFile mRandomAccessFile;
    private int UPDATE_SIZE = 512 * 1024;    // 512k存储一次
    private int mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;

    private String mFileName;

    private List<DownloadTaskListener> mDownloadlisteners = new ArrayList<DownloadTaskListener>();

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
            String path = mSaveDirPath + File.separator+mFileName;
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
                        mFileName, mDownloadStatus);
                mDownloadDao.insertOrReplace(mDbEntity);
                onCompleted();
                return;
            }
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_START;
            onStart();
            Log.e("debug", mUrl + "DownloadedSize:" + mDownloadedSize);
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
                        mDbEntity.setTotalSize(mTotalSize);
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
                    int length = 0;
                    int buffOffset = 0;
                    if (mDbEntity == null) {
                        mDbEntity = new DownloadDBEntity(mId, mTotalSize, 0L, mUrl, mSaveDirPath,
                                mFileName, mDownloadStatus);
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
                            mDbEntity.setDownloadedSize(mDownloadedSize);
                            mDownloadDao.update(mDbEntity);
                            onDownloading();
                        }
                    }
                    mDbEntity.setDownloadedSize(mDownloadedSize);
                    mDownloadDao.update(mDbEntity);
                    onDownloading();
                }
            } else {
                mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
                onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_FILE_NOT_FOUND);
            return;
        } catch (SocketException e) {
            Log.d("","*******SocketException*******");
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_NETWORK_ERROR);
            return;
        } catch (IOException e) {
            Log.d("","*******IOException*******");
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_IO_ERROR);
            return;
        } catch (Exception e) {
            Log.d("","*******Exception*******");
            e.printStackTrace();
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            onError(DownloadTaskListener.DOWNLOAD_ERROR_UNKONW_ERROR);
            return;
        } finally {
            mDbEntity.setDownloadedSize(mDownloadedSize);
            mDownloadDao.update(mDbEntity);
            if (bis != null) try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mRandomAccessFile != null) try {
                mRandomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mTotalSize == mDownloadedSize)
            mDownloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
        mDbEntity.setDownloadStatus(mDownloadStatus);
        mDownloadDao.update(mDbEntity);
        Log.d("onDownloadComplete2", mDbEntity.toString());


        switch (mDownloadStatus) {
            case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                onCompleted();
                break;
            case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                onPause();
                break;
            case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                mDownloadDao.delete(mDbEntity);
                File temp = new File(mSaveDirPath + mFileName);
                if (temp.exists()) temp.delete();
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
        this.mSaveDirPath = saveDirPath;
    }

    public int getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.mDownloadStatus = downloadStatus;
    }

    public void setDownloadDao(DownloadDBEntityDao downloadDao) {
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

    /**
     * 取消任务，删除下载的文件
     */
    public void cancel() {
        setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        File temp = new File(mSaveDirPath +File.separator+ mFileName);
        if (temp.exists()) {
            temp.delete();
        }
    }

    public void pause() {
        setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
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
        Log.d("onDownloading", mId + " listener size:" + mDownloadlisteners.size());
        for (DownloadTaskListener listener : mDownloadlisteners) {
            listener.onDownloading(this);
        }
    }

    private void onCompleted() {
        for (DownloadTaskListener listener : mDownloadlisteners) {
            listener.onCompleted(this);
        }
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

    public void addDownloadListener(DownloadTaskListener listener) {
        mDownloadlisteners.add(listener);
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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
}
