package com.can.appstore.download;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.can.appstore.R;
import com.can.appstore.speciallist.SpecialActivity;

import java.io.File;
import java.util.Map;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;

/**
 * ================================================
 * 作    者：
 * 版    本：
 * 创建日期：
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DownloadLeadAcitivity extends AppCompatActivity {

    private static final String TAG = "DownloadLeadAcitivity";
    private DownloadTaskListener mListener;
    private DownloadManager mDownLoadManager;
    String url1 = "http://ams.ott.cibntv.net/can/20160627/BevaErgeTV_V2_3.2.5_20160615_CANTV.apk";
    String url2 = "https://zndscdn.b0.upaiyun.com/apk/dangbeimarket_3.9.4_1026_znds.apk";
    String url3 = "http://172.16.11.65:8080/download/20161018/F2_Launcher_V532_20161018192912.apk";
    String url4 = "http://172.16.11.65:8080/download/20160930/CanTV_Launcher-4.7_V567_1475213562878.apk";
    String url5 = "http://172.16.11.65:8080/download/20161021/CanTV_Launcher-lowMem_V115_20161021183637.apk";
    String url6 = "http://172.16.11.65:8080/download/20161025/CanTV_Launcher_Voice_V568_20161025173514.apk";
    String url7 = "http://172.16.11.65:8080/download/20161025/CanTV_Launcher_Voice_V569_20161025173514.apk";
    String url8 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-4.7_V566_1475120588804.apk";
    String url9 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-JRX_V102_1475118835770.apk";
    String url10 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V110_1475118103540.apk";
    String url11 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V110_1475132940787.apk";
    String url12 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V110_1475144559170.apk";
    String url13 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V111_1475144803118.apk";
    String url14 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V111_1475144934086.apk";
    String url15 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V111_1475149511927.apk";
    String url16 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V111_1475149093617.apk";
    String url17 = "http://172.16.11.65:8080/download/20160929/CanTV_Launcher_Voice_V567_1475116447336.apk";
    String url18 = "http://172.16.11.65:8080/download/20160929/F1_Launcher_V530_1475119800127.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlead);
        initListener();
        initTest();
    }

    private void initListener() {
        mListener = new DownloadListener();
    }

    private void initTest() {
        mDownLoadManager = DownloadManager.getInstance(this.getApplicationContext());
       // mDownLoadManager.resumeAllTasks();
       // mDownLoadManager.setPoolSize(3);

        this.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTask downloadTask1, downloadTask2, downloadTask3, downloadTask4,
                        downloadTask5, downloadTask6, downloadTask7, downloadTask8, downloadTask9,
                        downloadTask10, downloadTask11, downloadTask12, downloadTask13, downloadTask14,
                        downloadTask15, downloadTask16, downloadTask17, downloadTask18;

                downloadTask1 = new DownloadTask(url1);
                downloadTask1.setFileName("测试1.apk");
                downloadTask2 = new DownloadTask(url2);
                downloadTask2.setFileName("测试2.apk");
                downloadTask3 = new DownloadTask(url3);
                downloadTask3.setFileName("测试3.apk");
                downloadTask4 = new DownloadTask(url4);
                downloadTask4.setFileName("测试4.apk");
                downloadTask5 = new DownloadTask(url5);
                downloadTask5.setFileName("测试5.apk");
                downloadTask6 = new DownloadTask(url6);
                downloadTask6.setFileName("测试6.apk");
                downloadTask7 = new DownloadTask(url7);
                downloadTask7.setFileName("测试7.apk");
                downloadTask8 = new DownloadTask(url8);
                downloadTask8.setFileName("测试8.apk");
                downloadTask9 = new DownloadTask(url9);
                downloadTask9.setFileName("测试9.apk");
                downloadTask10 = new DownloadTask(url10);
                downloadTask10.setFileName("测试10.apk");
                downloadTask11 = new DownloadTask(url11);
                downloadTask11.setFileName("测试11.apk");
                downloadTask12 = new DownloadTask(url12);
                downloadTask12.setFileName("测试12.apk");
                downloadTask13 = new DownloadTask(url13);
                downloadTask13.setFileName("测试13.apk");
                downloadTask14 = new DownloadTask(url14);
                downloadTask14.setFileName("测试14.apk");
                downloadTask15 = new DownloadTask(url15);
                downloadTask15.setFileName("测试15.apk");
                downloadTask16 = new DownloadTask(url16);
                downloadTask16.setFileName("测试16.apk");
                downloadTask17 = new DownloadTask(url17);
                downloadTask17.setFileName("测试17.apk");
                downloadTask18 = new DownloadTask(url18);
                downloadTask18.setFileName("测试18.apk");

                downloadTask1.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask2.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask3.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask4.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask5.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask6.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask7.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask8.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask9.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask10.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask11.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask12.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask13.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask14.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask15.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask16.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask17.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");
                downloadTask18.setSaveDirPath(Environment.getExternalStorageDirectory()+ File.separator+"candownload");


                mDownLoadManager.addDownloadTask(downloadTask1, mListener);
                mDownLoadManager.addDownloadTask(downloadTask2, mListener);
                mDownLoadManager.addDownloadTask(downloadTask3, mListener);
                mDownLoadManager.addDownloadTask(downloadTask4, mListener);
                mDownLoadManager.addDownloadTask(downloadTask5, mListener);
                mDownLoadManager.addDownloadTask(downloadTask6, mListener);
                mDownLoadManager.addDownloadTask(downloadTask7, mListener);
                mDownLoadManager.addDownloadTask(downloadTask8, mListener);
                mDownLoadManager.addDownloadTask(downloadTask9, mListener);
                mDownLoadManager.addDownloadTask(downloadTask10, mListener);
                mDownLoadManager.addDownloadTask(downloadTask11, mListener);
                mDownLoadManager.addDownloadTask(downloadTask12, mListener);
                mDownLoadManager.addDownloadTask(downloadTask13, mListener);
                mDownLoadManager.addDownloadTask(downloadTask14, mListener);
                mDownLoadManager.addDownloadTask(downloadTask15, mListener);
                mDownLoadManager.addDownloadTask(downloadTask16, mListener);
                mDownLoadManager.addDownloadTask(downloadTask17, mListener);
                mDownLoadManager.addDownloadTask(downloadTask18, mListener);
            }
        });


        this.findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadLeadAcitivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });
        this.findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadLeadAcitivity.this, SpecialActivity.class);
                startActivity(intent);
            }
        });


    }

    public static class DownloadListener implements DownloadTaskListener {
        @Override
        public void onPrepare(DownloadTask downloadTask) {
            Log.i(TAG, "onPrepare: taskid=" + downloadTask.getId());
        }

        @Override
        public void onStart(DownloadTask downloadTask) {
            Log.i(TAG, "onStart: taskTotalSize= " + downloadTask.getTotalSize());
        }

        @Override
        public void onDownloading(DownloadTask downloadTask) {
            Log.i(TAG, "onDownloading: taskName=" + downloadTask.getFileName() +
                    "  taskCompelteSize=" + downloadTask.getCompletedSize());
        }

        @Override
        public void onPause(DownloadTask downloadTask) {
            Log.i(TAG, "onPause: taskName=" + downloadTask.getFileName());
        }

        @Override
        public void onCancel(DownloadTask downloadTask) {
            Log.i(TAG, "onCancel: taskName=" + downloadTask.getFileName());
        }

        @Override
        public void onCompleted(DownloadTask downloadTask) {
            Log.i(TAG, "onCompleted: taskName=" + downloadTask.getFileName());
        }

        @Override
        public void onError(DownloadTask downloadTask, int errorCode) {
            Log.i(TAG, "onError:taskName=" + downloadTask.getFileName() + " errorCode=" + errorCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Map<String, DownloadTask> taskMap = mDownLoadManager.getCurrentTaskList();
        for (String taskid : taskMap.keySet()) {
            taskMap.get(taskid).removeAllDownloadListener();
        }

    }
}
