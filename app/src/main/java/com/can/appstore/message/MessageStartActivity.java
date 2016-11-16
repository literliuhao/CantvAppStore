package com.can.appstore.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.can.appstore.R;
import com.can.appstore.entity.Message;
import com.can.appstore.entity.MessageContainer;
import com.can.appstore.entity.Result;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.message.db.entity.MessageInfo;
import com.can.appstore.message.manager.MessageManager;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.NetworkUtils;
import retrofit2.Response;

/**
 * 启动页
 */
public class MessageStartActivity extends AppCompatActivity {

    private final String TAG = "MessageStartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_start);
        //检查网络
        checkNetwork();
        Intent intent = new Intent(this, MessageHomeActivity.class);
        startActivity(intent);
    }

    /**
     * 检查网络
     */
    private void checkNetwork() {
        if (NetworkUtils.isNetworkConnected(MessageStartActivity.this)) {
            requestMessagesData();
        }
    }

    /**
     * 请求接口数据
     */
    private void requestMessagesData() {
        CanCall<Result<MessageContainer>> mMessageContainer = HttpManager.getApiService().getMessages();
        mMessageContainer.enqueue(new CanCallback<Result<MessageContainer>>() {
            @Override
            public void onResponse(CanCall<Result<MessageContainer>> call, Response<Result<MessageContainer>> response) throws Exception {
                Result<MessageContainer> body = response.body();
                MessageContainer msgContainer = body.getData();
                long timestamp = msgContainer.getTimestamp();
                MessageManager.deleteExceedMsg(timestamp); // 删除过期数据
                List<Message> list = msgContainer.getMessages();
                if (list != null && !list.isEmpty()) {
                    List<MessageInfo> msgList = new ArrayList<MessageInfo>();
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        Message msg = list.get(i);
                        MessageInfo msgInfo = new MessageInfo();
                        msgInfo.setMsgId(msg.getId()+ System.currentTimeMillis()); //注：测试阶段给每个msgId后拼接一个当前系统时间串，避免id重复操作数据库时报错
                        msgInfo.setMsgDate(msg.getDate());
                        msgInfo.setMsgExpires(msg.getExpires());
                        msgInfo.setMsgTitle(msg.getTitle());
                        msgInfo.setStatus(true);
                        if (TextUtils.isEmpty(msg.getAction())){
                            msgInfo.setAction("action_nothing");
                        }else{
                            msgInfo.setAction(msg.getAction().trim());
                        }
                        msgInfo.setActionData(msg.getActionData());
                        msgInfo.setUserId(NetworkUtils.getMac());
                        msgList.add(msgInfo);
                    }
                    MessageManager.insert(msgList); // 插入数据库
                }
            }

            @Override
            public void onFailure(CanCall<Result<MessageContainer>> call, CanErrorWrapper errorWrapper) {
                Log.w(TAG, errorWrapper.getReason(), errorWrapper.getThrowable());
            }
        });

     /*   //获取假数据
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final List<MessageInfo> msgList = new ArrayList<MessageInfo>();
                MessageInfo msg;
                for (int i = 1; i <= 20; i++) {
                    msg = new MessageInfo();
                    msg.setMsgId(System.currentTimeMillis() + i + "");
                    msg.setMsgDate("2016-10-0"+i);
                    msg.setMsgExpires(System.currentTimeMillis() / 1000 + 3600);  // 有效期一分钟
                    msg.setMsgTitle("【下载专区】新版本发布，赶快升级体验");
                    msg.setStatus(true);
                    msg.setAction("action_nothing");
                    msg.setActionData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                    msg.setUserId(NetworkUtils.getMac());
                    msgList.add(msg);
                }

                final long timestamp = System.currentTimeMillis() / 1000;
                MessageManager.deleteExceedMsg(timestamp); // 删除过期数据
                MessageManager.insert(msgList);  //插入数据
            }
        }.start();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
