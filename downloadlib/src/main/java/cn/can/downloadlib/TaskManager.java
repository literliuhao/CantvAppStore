package cn.can.downloadlib;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * ================================================
 * 作    者：朱洪龙
 * 版    本：1.0
 * 创建日期：2016/11/07
 * 描    述：任务队列管理
 * 修订历史：
 * ================================================
 */
public class TaskManager {
    private static final int MAX_TASKS = 50;
    // 工作队列
    private Map<String, DownloadTask> mCurrentTaskMap = new HashMap<String, DownloadTask>(MAX_TASKS);
    // 工作队列FIFO
    private BlockingQueue<String> mWorkTaskQueue = new LinkedBlockingQueue<>(MAX_TASKS);
    // 异常任务队列
    public static BlockingQueue<String> mErrorTaskQueue = new LinkedBlockingQueue<>(MAX_TASKS);

    public DownloadTask get(String taskId) {
        return mCurrentTaskMap.get(taskId);
    }

    /**
     * 从任务队列中取出任务FIFO
     * @return
     */
    public DownloadTask poll() {
        // 从异常队列中取异常任务重试
        String taskId = mErrorTaskQueue.poll();
        if (TextUtils.isEmpty(taskId)) {
            // 从工作队列中取任务
            taskId = mWorkTaskQueue.poll();
        }
        if (taskId != null) {
            // 从任务池中取任务
            return mCurrentTaskMap.get(taskId);
        }
        return null;
    }

    public Map<String,DownloadTask> getCurrentTaskList() {
        return mCurrentTaskMap;
    }

    /**
     * 添加任务到队列中
     * @param task
     * @return
     */
    public boolean put(DownloadTask task) {
        boolean flg =  mWorkTaskQueue.offer(task.getId());
        if (flg) {
            mCurrentTaskMap.put(task.getId(), task);
        }
        return flg;
    }

    public void remove(String taskId) {
        mCurrentTaskMap.remove(taskId);
        mWorkTaskQueue.remove(taskId);
    }

    public void release(){
        for (String key : mCurrentTaskMap.keySet()) {
            mCurrentTaskMap.get(key).removeAllDownloadListener();
            mCurrentTaskMap.get(key).pause();
        }
        mCurrentTaskMap.clear();
        mCurrentTaskMap = null;
        mWorkTaskQueue.clear();
        mWorkTaskQueue = null;
        mErrorTaskQueue.clear();
        mErrorTaskQueue = null;
    }
}
