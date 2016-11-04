package com.can.appstore.download;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.can.appstore.R;
import com.can.appstore.subject.SubjectActivity;

import java.io.File;

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

    private static final String TAG = "MainActivity";
    private DownloadTaskListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlead);
        initListener();
        initTest();
    }

    private void initListener() {
        mListener=new DownloadTaskListener() {
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
        };
    }


    private void initTest() {
        String url1="http://app.znds.com/down/20160909/dsj2.0-2.9.1-dangbei.jpg";
        String url2="http://img-download.pchome.net/download/1k0/sc/3n/ob5ypk-1he9.jpg";
        String url3="http://img-download.pchome.net/download/1k0/sc/3n/ob5ypk-os5.jpg";
        String url4="http://img-download.pchome.net/download/1k0/mp/36/o7vbrz-hsf.jpg";
        String url5="http://img-download.pchome.net/download/1k0/sg/2v/obd613-k6v.jpg";
        String url6="http://img-download.pchome.net/download/1k0/mp/36/o7vbqv-1fiz.jpg";
        String url7="http://img-download.pchome.net/download/1k0/mp/36/o7vbr1-1e94.jpg";
        String url8="http://img-download.pchome.net/download/1k0/mp/36/o7vbre-rhr.jpg";
        String url9="http://img-download.pchome.net/download/1k0/mp/36/o7vbrk-16cg.jpg";
        String url10="http://img-download.pchome.net/download/1k0/mp/36/o7vbrp-1lyp.jpg";
        String url11="http://img-download.pchome.net/download/1k0/mp/36/o7vbru-1i1z.jpg";
        String url12="http://img-download.pchome.net/download/1k0/mp/36/o7vbs5-1hya.jpg";
        String url13="http://img-download.pchome.net/download/1k0/mn/3p/o7rrf7-mpc.jpg";
        String url14="http://img-download.pchome.net/download/1k0/mn/3p/o7rrf5-18wm.jpg";
        String url15="http://img-download.pchome.net/download/1k0/mn/3p/o7rrf5-1eyx.jpg";
        String url16="http://img-download.pchome.net/download/1k0/mn/3p/o7rrf5-qdv.jpg";
        String url17="http://img-download.pchome.net/download/1k0/mn/3p/o7rrf7-20sp.jpg";
        String url18="http://img-download.pchome.net/download/1k0/mn/3p/o7rrf7-iv4.jpg";

        final DownloadManager mDownLoadManager=DownloadManager.getInstance(this.getApplicationContext());
        mDownLoadManager.setPoolSize(3);
        final DownloadTask downloadTask2=new DownloadTask(url1);
        downloadTask2.setFileName("当贝.apk");


        final DownloadTask downloadTask1=new DownloadTask(url2);
        downloadTask1.setFileName("芒果tv.jpg");
        final DownloadTask downloadTask3=new DownloadTask(url3);
        downloadTask3.setFileName("爱奇艺.jpg");
        final DownloadTask downloadTask4=new DownloadTask(url4);
        downloadTask4.setFileName("优酷.jpg");
        final DownloadTask downloadTask5=new DownloadTask(url5);
        downloadTask5.setFileName("迅雷看看.jpg");
        final DownloadTask downloadTask6=new DownloadTask(url6);
        downloadTask6.setFileName("qq.jpg");
        final DownloadTask downloadTask7=new DownloadTask(url7);
        downloadTask7.setFileName("qq音乐.jpg");
        final DownloadTask downloadTask8=new DownloadTask(url8);
        downloadTask8.setFileName("酷狗音乐.jpg");
        final DownloadTask downloadTask9=new DownloadTask(url9);
        downloadTask9.setFileName("网易云音乐.jpg");
        final DownloadTask downloadTask10=new DownloadTask(url10);
        downloadTask10.setFileName("虾米音乐.jpg");
        final DownloadTask downloadTask11=new DownloadTask(url11);
        downloadTask11.setFileName("百度音乐.jpg");
        final DownloadTask downloadTask12=new DownloadTask(url12);
        downloadTask12.setFileName("限时福利卡.jpg");
        final DownloadTask downloadTask13=new DownloadTask(url13);
        downloadTask13.setFileName("是否是.jpg");
        final DownloadTask downloadTask14=new DownloadTask(url14);
        downloadTask14.setFileName("对方水电费水电费.jpg");
        final DownloadTask downloadTask15=new DownloadTask(url15);
        downloadTask15.setFileName("sdfsdf.jpg");
        final DownloadTask downloadTask16=new DownloadTask(url16);
        downloadTask16.setFileName("放上的方式.jpg");
        final DownloadTask downloadTask17=new DownloadTask(url17);
        downloadTask17.setFileName("就肯定是发.jpg");
        final DownloadTask downloadTask18=new DownloadTask(url18);
        downloadTask18.setFileName("房间里的设计费的设计费.jpg");


        this.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownLoadManager.addDownloadTask(downloadTask1,mListener);
                mDownLoadManager.addDownloadTask(downloadTask11,mListener);
                mDownLoadManager.addDownloadTask(downloadTask3,mListener);
                mDownLoadManager.addDownloadTask(downloadTask4,mListener);
                mDownLoadManager.addDownloadTask(downloadTask5,mListener);
                mDownLoadManager.addDownloadTask(downloadTask6,mListener);
                mDownLoadManager.addDownloadTask(downloadTask7,mListener);
                mDownLoadManager.addDownloadTask(downloadTask8,mListener);
                mDownLoadManager.addDownloadTask(downloadTask9,mListener);
                mDownLoadManager.addDownloadTask(downloadTask10,mListener);
                mDownLoadManager.addDownloadTask(downloadTask2,mListener);
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
                Intent intent=new Intent(DownloadLeadAcitivity.this,SubjectActivity.class);
                startActivity(intent);
            }
        });


    }
}
