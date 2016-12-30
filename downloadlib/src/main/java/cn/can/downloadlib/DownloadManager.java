package cn.can.downloadlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import cn.can.downloadlib.utils.ApkUtils;
import cn.can.downloadlib.utils.FileUtils;
import cn.can.downloadlib.utils.ShellUtils;
import cn.can.downloadlib.utils.ToastUtils;
import okhttp3.OkHttpClient;

/**
 * ================================================
 * 作    者：朱洪龙
 * 版    本：1.0
 * 创建日期：2016/10/10
 * 描    述：下载器
 * 修订历史：
 * ================================================
 */
public class DownloadManager implements AppInstallListener {
    private static final String TAG = "DownloadManager";
    private static final int READ_TIMEOUT = 5;
    private static final int WRITE_TIMEOUT = 2;
    private static final int CONNECT_TIMEOUT = 5;
    private static final int DELAY_TIME = 1000;
    private static final int MSG_SUBMIT_TASK = 1000;
    private static final int MSG_APP_INSTALL = 1001;
    private static final int MSG_APP_UNINSTALL = 1002;

    private static DownloadManager mInstance;
    private static DownloadDao mDownloadDao;
    private Context mContext;
    private int mPoolSize = 3;//Runtime.getRuntime().availableProcessors();
    private int mLimitSpace = 100;
    private String mDownloadPath;
    private ExecutorService mExecutorService;
    private OkHttpClient mOkHttpClient;
    private List<AppInstallListener> mAppInstallListeners;
//    private Map<String, DownloadTask> mSingleTaskMap;

    private TaskManager mTaskManager = new TaskManager();

    private DownloadTaskCountListener mTaskCntListener;

    private HandlerThread mHandlerThread;
    private Handler mHander;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUBMIT_TASK:
                    if (NetworkUtils.isNetworkConnected(mContext)) {
                        if (((ThreadPoolExecutor) mExecutorService).getActiveCount() < mPoolSize) {
                            DownloadTask task = mTaskManager.poll();
                            if (task != null) {
                                Future future = mExecutorService.submit(task);
                            }
                        }
                    }
                    mHander.sendEmptyMessageDelayed(MSG_SUBMIT_TASK, DELAY_TIME);
                    break;
                case MSG_APP_INSTALL:
                    Bundle bundle = msg.getData();
                    String path = bundle.getString("path");
                    String id = bundle.getString("id");
                    DownloadTask downloadtask=getCurrentTaskById(id);
                    if(downloadtask!=null){
                        /**添加安装空间的判断*/
                        long space = mContext.getFilesDir().getUsableSpace();
                        if (space < downloadtask.getTotalSize()*1.5) {
                            onInstallFail(id);
                            ToastUtils.showMessageLong(mContext.getApplicationContext(), R.string.error_install_space_not_enough);
                            return false;
                        }
                    }
                    ShellUtils.execCommand("chmod 777 "+mContext.getFilesDir(),false);
                    ShellUtils.CommandResult res = ShellUtils.execCommand("pm install -r " + path, false);
                    /**修复安装成功result==0 未安装成功问题，添加res.successMsg  判断 2016-12-26 10:53:00 xzl*/
                    if (res.result == 0&&!TextUtils.isEmpty(res.successMsg)&&res.successMsg.equals("Success")) {
                        onInstallSucess(id);
                    } else {
                        onInstallFail(id);
                    }
                    break;
                case MSG_APP_UNINSTALL:
                    Bundle b = msg.getData();
                    String pkg = b.getString("pkgname");
                    ShellUtils.CommandResult result = ShellUtils.execCommand("pm uninstall " + pkg, false);
                    if (result.result == 0) {
                        onUninstallSucess(pkg);
                    } else {
                        onUninstallFail(pkg);
                    }
                    break;
            }
            return false;
        }
    };

    public DownloadManager(OkHttpClient client, Context context) {
        this.mOkHttpClient = client;
        this.mContext = context;
        mAppInstallListeners = new ArrayList<>();
    }

    private DownloadManager() {
        init();
        mAppInstallListeners = new ArrayList<>();
    }

    private DownloadManager(Context context, InputStream in) {
        this.mContext = context.getApplicationContext();
        init(in, null);
        mAppInstallListeners = new ArrayList<>();
    }

    /**
     * 支持https
     *
     * @param certificates
     * @return
     */
    public static SSLSocketFactory initCertificates(InputStream... certificates) {
        CertificateFactory certificateFactory;
        SSLContext sslContext = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory
                        .generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (sslContext != null) {
            return sslContext.getSocketFactory();
        }
        return null;
    }

    /**
     * @param context
     * @param sslKey  https签名文件
     * @return
     */
    public static DownloadManager getInstance(Context context, InputStream sslKey) {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager(context, sslKey);
                }
            }
        }
        return mInstance;
    }

    public static DownloadManager getInstance(Context context) {
        return getInstance(context, null);
    }

    public static DownloadManager getInstance(OkHttpClient okHttpClient, Context context) {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                mInstance = new DownloadManager(okHttpClient, context);
            }
        }
        return mInstance;
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    public Map<String, DownloadTask> getCurrentTaskList() {
        return mTaskManager.getCurrentTaskList();
    }


    /**
     * 设置线程池数量
     *
     * @param count
     */
    public void setPoolSize(int count) {
        mPoolSize = count;
    }

    /**
     * 初始化，使用OkHttpClient
     *
     * @param in
     * @param okHttpClient
     */
    private void init(InputStream in, OkHttpClient okHttpClient) {
        if (TextUtils.isEmpty(mDownloadPath)) {
            mDownloadPath = mContext.getFilesDir().getAbsolutePath() + File.separator + "download";
            File dir = new File(mDownloadPath);
            dir.mkdirs();
            dir.setWritable(true, false);
            dir.setReadable(true, false);
            dir.setExecutable(true, false);
        }

        mHandlerThread = new HandlerThread("queue");
        mHandlerThread.start();
        mHander = new Handler(mHandlerThread.getLooper(), mCallback);

        mExecutorService = Executors.newFixedThreadPool(mPoolSize);

        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(mContext, "downloadDB", null);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        mDownloadDao = daoMaster.newSession().getDownloadDao();

        if (okHttpClient != null) {
            mOkHttpClient = okHttpClient;
        } else {
            OkHttpClient.Builder buider = new OkHttpClient.Builder();
            if (in != null) {
                buider.sslSocketFactory(initCertificates(in));
            }
            mOkHttpClient = buider.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();
        }
    }

    private void init() {
        init(null, null);
    }

    /**
     * 如果任务存在，则返回task，否则返回null
     *
     * @param task
     * @param listener
     * @return
     */
    public boolean addDownloadTask(DownloadTask task, DownloadTaskListener listener) {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            return false;
        }

        /**上层已检测空间大小，故此暂时注掉，避免显示空间够用，却下载不了*/
//        long space = mContext.getFilesDir().getUsableSpace() >>20;
//        if (space < mLimitSpace) {
//            ToastUtils.showMessageLong(mContext, R.string.error_msg);
//            task.setDownloadStatus(DownloadStatus.SPACE_NOT_ENOUGH);
//            return false;
//        }

        DownloadTask downloadTask = mTaskManager.get(task.getId());
        if (null != downloadTask && downloadTask.getDownloadStatus() != DownloadStatus
                .DOWNLOAD_STATUS_CANCEL) {
            Log.i(TAG, "task already exist");
            return false;
        }

        task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PREPARE);
        task.setDownloadDao(mDownloadDao);
        task.setHttpClient(mOkHttpClient);
        task.addDownloadListener(listener);
        task.setSaveDirPath(mDownloadPath);
        task.setAppListener(this);
        mTaskManager.put(task);

        if (getDBTaskById(task.getId()) == null) {
            DownloadDBEntity dbEntity = new DownloadDBEntity(task.getId(), task.getTotalSize(),
                    task.getCompletedSize(), task.getUrl(), task.getSaveDirPath(), task
                    .getFileName(), task.getDownloadStatus(), task.getIcon(), task.getAppId(),task.getPkg());
            mDownloadDao.insertOrReplace(dbEntity);
        }
        if(mTaskCntListener!=null){
            mTaskCntListener.getTaskCount(getCurrentTaskList().size());
        }
        /**修复添加任务时，多个消息同时轮询问题 xzl 2016-11-30 16:20:33  start */
        mHander.removeMessages(MSG_SUBMIT_TASK);
        /**修复添加任务时，多个消息同时轮询问题 xzl 2016-11-30 16:20:33  end */
        mHander.sendEmptyMessage(MSG_SUBMIT_TASK);
        return true;
    }

    /**
     * 根据taskId获取task
     *
     * @param taskId
     * @return
     */
    public DownloadTask resume(String taskId) {
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            return null;
        }
        //        /**读取数据库task，不轮询提交任务问题 xingzhaolei 2016-11-4 17:05:13 start*/
        //        mHander.removeMessages(MSG_SUBMIT_TASK);
        //        mHander.sendEmptyMessage(MSG_SUBMIT_TASK);
        //
        //        DownloadTask downloadTask = getCurrentTaskById(taskId);
        //        if (downloadTask != null) {
        //            if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
        //                downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_INIT);
        //                Future future = mExecutorService.submit(downloadTask);
        //            }
        //        } else {
        //            downloadTask = getDBTaskById(taskId);
        //            if (downloadTask != null) {
        //                downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_INIT);
        //                /**修复数据库获取task 无法resume 问题  xingzl 2016-11-4 16:51:58 start*/
        //                downloadTask.setDownloadDao(mDownloadDao);
        //                downloadTask.setHttpClient(mOkHttpClient);
        //                downloadTask.setAppListener(this);
        //                mTaskManager.put(downloadTask);
        //                Future future = mExecutorService.submit(downloadTask);
        //            }
        //        }

        DownloadTask downloadTask = getCurrentTaskById(taskId);
        if (downloadTask == null) {
            downloadTask = getDBTaskById(taskId);
            if (downloadTask == null) {
                return null;
            } else {
                downloadTask.setDownloadDao(mDownloadDao);
                downloadTask.setHttpClient(mOkHttpClient);
                downloadTask.setAppListener(this);
                mTaskManager.put(downloadTask);
            }
        }
        if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PAUSE
                || downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_ERROR
                || downloadTask.getDownloadStatus() == DownloadStatus.SPACE_NOT_ENOUGH) {
            downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_INIT);
            mTaskManager.put(downloadTask);
        }
        /**修复暂停恢复任务无法继续下载问题xzl 2016-11-30 16:20:33  start */
        mHander.removeMessages(MSG_SUBMIT_TASK);
        mHander.sendEmptyMessage(MSG_SUBMIT_TASK);
        /**修复暂停恢复任务无法继续下载问题xzl 2016-11-30 16:20:33  end */
        return downloadTask;
    }

    /**
     * 添加下载监听，安装app监听
     *
     * @param task
     * @param listener
     */
    public void addDownloadListener(DownloadTask task, DownloadTaskListener listener) {
        task.addDownloadListener(listener);
        task.setAppListener(this);
    }

    /**
     * 删除下载监听
     *
     * @param task
     * @param listener
     */
    public void removeDownloadListener(DownloadTask task, DownloadTaskListener listener) {
        task.removeDownloadListener(listener);
    }

    /**
     * 删除任务
     *
     * @param id
     */
    private void deleteTask(String id) {
        mTaskManager.remove(id);
        mDownloadDao.deleteByKey(id);
        if(mTaskCntListener!=null){
            mTaskCntListener.getTaskCount(getCurrentTaskList().size());
        }
    }

    /**
     * 取消任务
     *
     * @param task
     */
    public void cancel(DownloadTask task) {
        mTaskManager.remove(task.getId());
        task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        task.cancel();
        mDownloadDao.deleteByKey(task.getId());
        if(mTaskCntListener!=null){
            mTaskCntListener.getTaskCount(getCurrentTaskList().size());
        }
    }

    /**
     * 取消所有任务
     */
    public void cancelAll() {

        mDownloadDao.deleteAll();
        mTaskManager.release();
        if(mTaskCntListener!=null){
            mTaskCntListener.getTaskCount(getCurrentTaskList().size());
        }
        //        mHander.removeCallbacksAndMessages(null);
        mHander.removeMessages(MSG_SUBMIT_TASK);
    }

    /**
     * 取消任务
     *
     * @param taskId
     */
    public void cancel(String taskId) {
        DownloadTask task = getTaskById(taskId);
        if (task != null) {
            cancel(task);
        }
    }

    /**
     * 暂停任务
     *
     * @param task
     */
    public void pause(DownloadTask task) {
        task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
    }

    /**
     * 暂停任务
     *
     * @param taskId
     */
    public void pause(String taskId) {
        DownloadTask task = getTaskById(taskId);
        if (task != null) {
            pause(task);
        }
    }

    /**
     * 读取任务属性信息
     *
     * @return
     */
    public List<DownloadDBEntity> loadAllDownloadEntityFromDB() {
        return mDownloadDao.loadAll();
    }

    /**
     * 读取未执行任务
     *
     * @return
     */
    public List<DownloadTask> loadAllDownloadTaskFromDB() {
        List<DownloadDBEntity> list = loadAllDownloadEntityFromDB();
        List<DownloadTask> downloadTaskList = null;
        if (list != null && !list.isEmpty()) {
            downloadTaskList = new ArrayList<>();
            for (DownloadDBEntity entity : list) {
                downloadTaskList.add(DownloadTask.parse(entity));
            }
        }
        return downloadTaskList;
    }

    /**
     * 获取所有任务（执行中与未执行）
     *
     * @return
     */
    public List<DownloadTask> loadAllTask() {
        List<DownloadTask> list = loadAllDownloadTaskFromDB();
        Map<String, DownloadTask> currentTaskMap = getCurrentTaskList();
        List<DownloadTask> currentList = new ArrayList<>();
        if (currentTaskMap != null) {
            currentList.addAll(currentTaskMap.values());
        }
        if (!currentList.isEmpty() && list != null) {
            for (DownloadTask task : list) {
                if (!currentList.contains(task)) {
                    currentList.add(task);
                }
            }
        } else {
            if (list != null) {
                currentList.addAll(list);
            }
        }
        return currentList;
    }

    /**
     * 恢复所有任务
     *
     * @return
     */
    public void resumeAllTasks() {
        /*********************是否需要考虑内存中的任务队列******************************/
        //        List<DownloadTask> list = loadAllDownloadTaskFromDB();
        //        Map<String, DownloadTask> currentTaskMap = getCurrentTaskList();
        //        List<DownloadTask> currentList = new ArrayList<DownloadTask>();
        //        if (currentTaskMap != null) {
        //            currentList.addAll(currentTaskMap.values());
        //        }
        //        if (!currentList.isEmpty() && list != null) {
        //            for (DownloadTask task : list) {
        //                if (!currentList.contains(task)) {
        //                    currentList.add(task);
        //                    task.setDownloadDao(mDownloadDao);
        //                    task.setHttpClient(mOkHttpClient);
        //                    currentTaskMap.put(task.getId(),task);
        //                    mTaskManager.put(task);
        //                    /**从数据库查到的数据直接加入任务队列中 免去下载页每次resume（taskid）时，
        //                     需重新更新列表数据问题。xingzl start*/
        //                }
        //            }
        //        } else {
        //            if (list != null) {
        //                currentList.addAll(list);
        //                for (DownloadTask task: list) {
        //                    mTaskManager.put(task);
        //                    task.setDownloadDao(mDownloadDao);
        //                    task.setHttpClient(mOkHttpClient);
        //                    currentTaskMap.put(task.getId(),task);
        //                }
        //            }
        //        }
        /***************************************************/
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            return;
        }
        List<DownloadTask> list = loadAllDownloadTaskFromDB();
        if (list != null) {
            for (DownloadTask task : list) {
                task.setDownloadDao(mDownloadDao);
                task.setHttpClient(mOkHttpClient);
                task.setAppListener(this);
                mTaskManager.put(task);
            }
        }
        if(mTaskCntListener!=null){
            mTaskCntListener.getTaskCount(getCurrentTaskList().size());
        }
        /**读取数据库task，不轮询提交任务问题 xingzhaolei 2016-11-23 17:53:25 start*/
        if (list != null && list.size() > 0) {
            mHander.removeMessages(MSG_SUBMIT_TASK);
            mHander.sendEmptyMessage(MSG_SUBMIT_TASK);
        }
    }

    /**
     * 获取队列中的任务
     *
     * @param taskId
     * @return
     */
    public DownloadTask getCurrentTaskById(String taskId) {
        return mTaskManager.get(taskId);
    }


    /**
     * 通过taskId获取任务
     *
     * @param taskId
     * @return
     */
    public DownloadTask getTaskById(String taskId) {
        DownloadTask task = null;
        task = getCurrentTaskById(taskId);
        if (task != null) {
            return task;
        }
        return getDBTaskById(taskId);
    }

    /**
     * 通过taskId获取任务
     *
     * @param taskId
     * @return
     */
    public DownloadTask getDBTaskById(String taskId) {
        DownloadDBEntity entity = mDownloadDao.load(taskId);
        if (entity != null) {
            return DownloadTask.parse(entity);
        }
        return null;
    }

    public void release() {
        /**移除所有消息  xingzhaolei 2016-11-04 17:01:58 start*/
        mHander.removeCallbacksAndMessages(null);
        /**移除所有消息  xingzhaolei 2016-11-04 17:01:58 end*/

        mTaskManager.release();

        mExecutorService.shutdownNow();
        mExecutorService = null;

        mOkHttpClient = null;
        mDownloadDao = null;
    }

    /**
     * 单一任务，不加入任务队列
     *
     * @param task
     * @param listener
     */
    public void singleTask(UpgradeTask task, UpgradeTaskListener listener, String downloadPath) {
        if (!NetworkUtils.isNetworkConnected(mContext.getApplicationContext())) {
            return;
        }
//        if (mSingleTaskMap == null) {
//            mSingleTaskMap = new LinkedHashMap<>();
//        }
        task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PREPARE);
        task.addDownloadListener(listener);
        task.setSaveDirPath(downloadPath);
        task.setHttpClient(mOkHttpClient);
//        if (getDBTaskById(task.getId()) == null) {
//            DownloadDBEntity dbEntity = new DownloadDBEntity(task.getId(), task.getTotalSize(),
//                    task.getCompletedSize(), task.getUrl(), task.getSaveDirPath(), task
//                    .getFileName(), task.getDownloadStatus(), task.getIcon(), task.getAppId(),task.getPkg());
//            mDownloadDao.insertOrReplace(dbEntity);
//        }
//        if (mSingleTaskMap.containsKey(task.getId())) {
//            mSingleTaskMap.remove(task.getId());
//        }
//        mSingleTaskMap.put(task.getId(), task);
        new Thread(task).start();
    }

//    public void addSingleTaskListener(String taskId, UpgradeTaskListener taskListener) {
//        if (mSingleTaskMap != null) {
//            DownloadTask task = mSingleTaskMap.get(taskId);
//            if (task == null) {
//                return null;
//            }
//            task.addDownloadListener(taskListener);
//            return task;
//        }
//        return null;
//    }

//    public DownloadTask getSigleTaskById(String taskId) {
//        if (mSingleTaskMap == null) {
//            return null;
//        }
//        return mSingleTaskMap.get(taskId);
//    }

//    public void deleteSigleTask(String taskId) {
//        if (mSingleTaskMap != null && taskId != null) {
//            mSingleTaskMap.remove(taskId);
//            mDownloadDao.deleteByKey(taskId);
//        }
//    }

    public void setLimitSpace(int size) {
        mLimitSpace = size;
    }

    public String getDownloadPath() {
        return mDownloadPath;
    }

    @Override
    public void onInstalling(DownloadTask downloadTask) {
//        if (mSingleTaskMap != null && mSingleTaskMap.containsKey(downloadTask.getId())) {
//            return;
//        }
        install(downloadTask);
        if (mAppInstallListeners != null) {
            Iterator<AppInstallListener> iter = mAppInstallListeners.iterator();
            while (iter.hasNext()) {
                AppInstallListener listener = iter.next();
                listener.onInstalling(downloadTask);
            }
        }
    }

    @Override
    public void onInstallSucess(String id) {
        DownloadTask task = getCurrentTaskById(id);
        if (task != null) {
            task.setDownloadStatus(AppInstallListener.APP_INSTALL_SUCESS);
            Log.i(TAG, "***InstallSucess***" + task.getFileName());
            ToastUtils.showMessageLong(mContext, task.getFileName() + mContext.getResources().getString(R.string
                    .install_sucess));
            String pkg = ApkUtils.getPkgNameFromApkFile(mContext, task.getFilePath());
            task.setPkg(pkg);
            deleteTask(id);
            FileUtils.deleteFile(task.getFilePath());

        }
        if (mAppInstallListeners != null) {
            Iterator<AppInstallListener> iter = mAppInstallListeners.iterator();
            while (iter.hasNext()) {
                AppInstallListener listener = iter.next();
                listener.onInstallSucess(id);
            }
        }
    }

    @Override
    public void onInstallFail(String id) {
        DownloadTask task = getCurrentTaskById(id);
        if (task != null) {
            task.setDownloadStatus(AppInstallListener.APP_INSTALL_FAIL);
            Log.i(TAG, "***InstallFail***" + task.getFileName());
            ToastUtils.showMessageLong(mContext, task.getFileName() + mContext.getResources().getString(R.string
                    .error_install));
        }
        if (mAppInstallListeners != null) {
            Iterator<AppInstallListener> iter = mAppInstallListeners.iterator();
            while (iter.hasNext()) {
                AppInstallListener listener = iter.next();
                listener.onInstallFail(id);
            }
        }
    }

    @Override
    public void onUninstallFail(String id) {
        if (mAppInstallListeners != null) {
            Iterator<AppInstallListener> iter = mAppInstallListeners.iterator();
            while (iter.hasNext()) {
                AppInstallListener listener = iter.next();
                listener.onUninstallFail(id);
            }
        }
    }

    @Override
    public void onUninstallSucess(String id) {
        if (mAppInstallListeners != null) {
            Iterator<AppInstallListener> iter = mAppInstallListeners.iterator();
            while (iter.hasNext()) {
                AppInstallListener listener = iter.next();
                listener.onUninstallSucess(id);
            }
        }
    }

    /**
     * 设置应用安装监听器
     *
     * @param listener
     */
    public void setAppInstallListener(AppInstallListener listener) {
        if (mAppInstallListeners == null) {
            mAppInstallListeners = new ArrayList<>();
        }
        mAppInstallListeners.add(listener);
    }

    /**
     * 删除安装应用监听
     *
     * @param listener
     */
    public void removeAppInstallListener(AppInstallListener listener) {
        if (mAppInstallListeners != null) {
            mAppInstallListeners.remove(listener);
        }
    }

    /**
     * 安装APP
     *
     * @param downloadTask
     */
    public void install(DownloadTask downloadTask) {
        /**添加叛空处理，xzl 2016-12-8 17:51:28*/
        downloadTask.setDownloadStatus(AppInstallListener.APP_INSTALLING);
        Message msg = Message.obtain();
        msg.what = MSG_APP_INSTALL;
        Bundle bundle = new Bundle();
        bundle.putString("path", downloadTask.getFilePath());
        bundle.putString("id", downloadTask.getId());
        msg.setData(bundle);
        mHander.sendMessage(msg);
    }

    /**
     * 卸载
     *
     * @param pkg
     */
    public void uninstall(String pkg) {
        Message msg = Message.obtain();
        msg.what = MSG_APP_UNINSTALL;
        Bundle bundle = new Bundle();
        bundle.putString("pkgname", pkg);
        msg.setData(bundle);
        mHander.sendMessage(msg);
    }

    public void setTaskCntListener(DownloadTaskCountListener listener) {
        mTaskCntListener = listener;
    }
    public void removeTaskCntListener(){
        mTaskCntListener=null;
    }
}
