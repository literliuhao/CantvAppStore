package com.can.appstore.download;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.can.appstore.R;
import com.can.appstore.specialtopic.SpecialActivity;

import java.io.File;
import java.util.Map;

import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.tvlib.utils.FileUtils;

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
    private  DownloadManager mDownLoadManager;
    private DownloadTask downloadTask1,downloadTask2,downloadTask3,downloadTask4,
            downloadTask5,downloadTask6,downloadTask7,downloadTask8,downloadTask9,
            downloadTask10,downloadTask11,downloadTask12,downloadTask13,downloadTask14,
            downloadTask15,downloadTask16,downloadTask17,downloadTask18;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlead);
        initListener();
        initTest();
    }

    private void initListener() {
        mListener=new DownloadListener();
    }

    private void initTest() {
        String url1="http://172.16.11.65:8080/download/20161018/F2_Launcher_V536_20161018191036.apk";
        String url2="http://172.16.11.65:8080/download/20160930/CanTV_Launcher-4.7_V567_1475213368761.apk";
        String url3="http://172.16.11.65:8080/download/20161018/F2_Launcher_V532_20161018192912.apk";
        String url4="http://172.16.11.65:8080/download/20160930/CanTV_Launcher-4.7_V567_1475213562878.apk";
        String url5="http://172.16.11.65:8080/download/20161021/CanTV_Launcher-lowMem_V115_20161021183637.apk";
        String url6="http://172.16.11.65:8080/download/20161025/CanTV_Launcher_Voice_V568_20161025173514.apk";
        String url7="http://172.16.11.65:8080/download/20161025/CanTV_Launcher_Voice_V569_20161025173514.apk";
        String url8="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-4.7_V566_1475120588804.apk";
        String url9="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-JRX_V102_1475118835770.apk";
        String url10="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V110_1475118103540.apk";
        String url11="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V110_1475132940787.apk";
        String url12="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V110_1475144559170.apk";
        String url13="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V111_1475144803118.apk";
        String url14="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V111_1475144934086.apk";
        String url15="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V111_1475149511927.apk";
        String url16="http://172.16.11.65:8080/download/20160929/CanTV_Launcher-lowMem_V111_1475149093617.apk";
        String url17="http://172.16.11.65:8080/download/20160929/CanTV_Launcher_Voice_V567_1475116447336.apk";
        String url18="http://172.16.11.65:8080/download/20160929/F1_Launcher_V530_1475119800127.apk";
        mDownLoadManager=DownloadManager.getInstance(this.getApplicationContext());
        mDownLoadManager.resumeAllTasks();
        mDownLoadManager.setPoolSize(3);
        downloadTask1=new DownloadTask(url1);
        downloadTask1.setFileName("测试1.apk");
        downloadTask2=new DownloadTask(url2);
        downloadTask2.setFileName("测试2.apk");
        downloadTask3=new DownloadTask(url3);
        downloadTask3.setFileName("测试3.apk");
        downloadTask4=new DownloadTask(url4);
        downloadTask4.setFileName("测试4.apk");
        downloadTask5=new DownloadTask(url5);
        downloadTask5.setFileName("测试5.apk");
        downloadTask6=new DownloadTask(url6);
        downloadTask6.setFileName("测试6.apk");
        downloadTask7=new DownloadTask(url7);
        downloadTask7.setFileName("测试7.apk");
        downloadTask8=new DownloadTask(url8);
        downloadTask8.setFileName("测试8.apk");
        downloadTask9=new DownloadTask(url9);
        downloadTask9.setFileName("测试9.apk");
        downloadTask10=new DownloadTask(url10);
        downloadTask10.setFileName("测试10.apk");
        downloadTask11=new DownloadTask(url11);
        downloadTask11.setFileName("测试11.apk");
        downloadTask12=new DownloadTask(url12);
        downloadTask12.setFileName("测试12.apk");
        downloadTask13=new DownloadTask(url13);
        downloadTask13.setFileName("测试13.apk");
        downloadTask14=new DownloadTask(url14);
        downloadTask14.setFileName("测试14.apk");
        downloadTask15=new DownloadTask(url15);
        downloadTask15.setFileName("测试15.apk");
        downloadTask16=new DownloadTask(url16);
        downloadTask16.setFileName("测试16.apk");
        downloadTask17=new DownloadTask(url17);
        downloadTask17.setFileName("测试17.apk");
        downloadTask18=new DownloadTask(url18);
        downloadTask18.setFileName("测试18.apk");
        this.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownLoadManager.addDownloadTask(downloadTask1,mListener);
                mDownLoadManager.addDownloadTask(downloadTask2,mListener);
                mDownLoadManager.addDownloadTask(downloadTask3,mListener);
                mDownLoadManager.addDownloadTask(downloadTask4,mListener);
                mDownLoadManager.addDownloadTask(downloadTask5,mListener);
                mDownLoadManager.addDownloadTask(downloadTask6,mListener);
                mDownLoadManager.addDownloadTask(downloadTask7,mListener);
                mDownLoadManager.addDownloadTask(downloadTask8,mListener);
                mDownLoadManager.addDownloadTask(downloadTask9,mListener);
                mDownLoadManager.addDownloadTask(downloadTask10,mListener);
                mDownLoadManager.addDownloadTask(downloadTask11,mListener);
                mDownLoadManager.addDownloadTask(downloadTask12,mListener);
                mDownLoadManager.addDownloadTask(downloadTask13,mListener);
                mDownLoadManager.addDownloadTask(downloadTask14,mListener);
                mDownLoadManager.addDownloadTask(downloadTask15,mListener);
                mDownLoadManager.addDownloadTask(downloadTask16,mListener);
                mDownLoadManager.addDownloadTask(downloadTask17,mListener);
                mDownLoadManager.addDownloadTask(downloadTask18,mListener);
            }
        });
        this.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownLoadManager.pause(downloadTask1);
                mDownLoadManager.pause(downloadTask2);
                mDownLoadManager.pause(downloadTask3);
                mDownLoadManager.pause(downloadTask4);
                mDownLoadManager.pause(downloadTask5);
                mDownLoadManager.pause(downloadTask6);
                mDownLoadManager.pause(downloadTask7);
                mDownLoadManager.pause(downloadTask8);
                mDownLoadManager.pause(downloadTask9);
                mDownLoadManager.pause(downloadTask10);
                mDownLoadManager.pause(downloadTask11);
                mDownLoadManager.pause(downloadTask12);
                mDownLoadManager.pause(downloadTask13);
                mDownLoadManager.pause(downloadTask14);
                mDownLoadManager.pause(downloadTask15);
                mDownLoadManager.pause(downloadTask16);
                mDownLoadManager.pause(downloadTask17);
                mDownLoadManager.pause(downloadTask18);


            }
        });
        this.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownLoadManager.cancel(downloadTask1);
                mDownLoadManager.cancel(downloadTask2);
                mDownLoadManager.cancel(downloadTask3);
                mDownLoadManager.cancel(downloadTask4);
                mDownLoadManager.cancel(downloadTask5);
                mDownLoadManager.cancel(downloadTask6);
                mDownLoadManager.cancel(downloadTask7);
                mDownLoadManager.cancel(downloadTask8);
                mDownLoadManager.cancel(downloadTask9);
                mDownLoadManager.cancel(downloadTask10);
                mDownLoadManager.cancel(downloadTask11);
                mDownLoadManager.cancel(downloadTask12);
                mDownLoadManager.cancel(downloadTask13);
                mDownLoadManager.cancel(downloadTask14);
                mDownLoadManager.cancel(downloadTask15);
                mDownLoadManager.cancel(downloadTask16);
                mDownLoadManager.cancel(downloadTask17);
                mDownLoadManager.cancel(downloadTask18);


            }
        });

        this.findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownLoadManager.resume(downloadTask1.getId());
                mDownLoadManager.resume(downloadTask2.getId());
                mDownLoadManager.resume(downloadTask3.getId());
                mDownLoadManager.resume(downloadTask4.getId());
                mDownLoadManager.resume(downloadTask5.getId());
                mDownLoadManager.resume(downloadTask6.getId());
                mDownLoadManager.resume(downloadTask7.getId());
                mDownLoadManager.resume(downloadTask8.getId());
                mDownLoadManager.resume(downloadTask9.getId());
                mDownLoadManager.resume(downloadTask10.getId());
                mDownLoadManager.resume(downloadTask11.getId());
                mDownLoadManager.resume(downloadTask12.getId());
                mDownLoadManager.resume(downloadTask13.getId());
                mDownLoadManager.resume(downloadTask14.getId());
                mDownLoadManager.resume(downloadTask15.getId());
                mDownLoadManager.resume(downloadTask16.getId());
                mDownLoadManager.resume(downloadTask17.getId());
                mDownLoadManager.resume(downloadTask18.getId());




            }
        });

        this.findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        FileUtils.deleteFile(downloadTask1.getSaveDirPath()+ File.separator+downloadTask1.getFileName());
                        FileUtils.deleteFile(downloadTask2.getSaveDirPath()+ File.separator+downloadTask2.getFileName());
                        FileUtils.deleteFile(downloadTask3.getSaveDirPath()+ File.separator+downloadTask3.getFileName());
                        FileUtils.deleteFile(downloadTask4.getSaveDirPath()+ File.separator+downloadTask4.getFileName());
                        FileUtils.deleteFile(downloadTask5.getSaveDirPath()+ File.separator+downloadTask5.getFileName());
                        FileUtils.deleteFile(downloadTask6.getSaveDirPath()+ File.separator+downloadTask6.getFileName());
                        FileUtils.deleteFile(downloadTask7.getSaveDirPath()+ File.separator+downloadTask7.getFileName());
                        FileUtils.deleteFile(downloadTask8.getSaveDirPath()+ File.separator+downloadTask8.getFileName());
                        FileUtils.deleteFile(downloadTask9.getSaveDirPath()+ File.separator+downloadTask9.getFileName());
                        FileUtils.deleteFile(downloadTask10.getSaveDirPath()+ File.separator+downloadTask10.getFileName());
                        FileUtils.deleteFile(downloadTask11.getSaveDirPath()+ File.separator+downloadTask11.getFileName());
                        FileUtils.deleteFile(downloadTask12.getSaveDirPath()+ File.separator+downloadTask12.getFileName());
                        FileUtils.deleteFile(downloadTask13.getSaveDirPath()+ File.separator+downloadTask13.getFileName());
                        FileUtils.deleteFile(downloadTask14.getSaveDirPath()+ File.separator+downloadTask14.getFileName());
                        FileUtils.deleteFile(downloadTask15.getSaveDirPath()+ File.separator+downloadTask15.getFileName());
                        FileUtils.deleteFile(downloadTask16.getSaveDirPath()+ File.separator+downloadTask16.getFileName());
                        FileUtils.deleteFile(downloadTask17.getSaveDirPath()+ File.separator+downloadTask17.getFileName());
                        FileUtils.deleteFile(downloadTask18.getSaveDirPath()+ File.separator+downloadTask18.getFileName());
                    }
                }.start();

            }
        });

        this.findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DownloadLeadAcitivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });
        this.findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DownloadLeadAcitivity.this,SpecialActivity.class);
                startActivity(intent);
            }
        });


    }

    public static class DownloadListener implements DownloadTaskListener{
        @Override
        public void onPrepare(DownloadTask downloadTask) {
            Log.i(TAG, "onPrepare: taskid="+downloadTask.getId());
        }

        @Override
        public void onStart(DownloadTask downloadTask) {
            Log.i(TAG, "onStart: taskTotalSize= "+downloadTask.getTotalSize());
        }

        @Override
        public void onDownloading(DownloadTask downloadTask) {
            Log.i(TAG, "onDownloading: taskName="+downloadTask.getFileName()+
                    "  taskCompelteSize="+downloadTask.getCompletedSize());
        }

        @Override
        public void onPause(DownloadTask downloadTask) {
            Log.i(TAG, "onPause: taskName="+downloadTask.getFileName());
        }

        @Override
        public void onCancel(DownloadTask downloadTask) {
            Log.i(TAG, "onCancel: taskName="+downloadTask.getFileName());
        }

        @Override
        public void onCompleted(DownloadTask downloadTask) {
            Log.i(TAG, "onCompleted: taskName="+downloadTask.getFileName());
        }

        @Override
        public void onError(DownloadTask downloadTask, int errorCode) {
            Log.i(TAG, "onError:taskName="+downloadTask.getFileName()+" errorCode="+errorCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Map<String,DownloadTask> taskMap=mDownLoadManager.getCurrentTaskList();
        for (String taskid:taskMap.keySet()){
            taskMap.get(taskid).removeAllDownloadListener();
        }

    }
}
