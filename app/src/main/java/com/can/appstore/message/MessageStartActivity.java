package com.can.appstore.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.can.appstore.R;
import com.can.appstore.message.db.entity.MessageInfo;
import com.can.appstore.message.manager.MessageManager;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.NetworkUtils;

/**
 * 启动页
 */
public class MessageStartActivity extends AppCompatActivity {

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
        //获取假数据
        final List<MessageInfo> msgList = new ArrayList<MessageInfo>();
        MessageInfo msg;
        for (int i = 1; i <= 9; i++) {
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
        //获取网络时间戳
        final long timestamp = System.currentTimeMillis() / 1000;
        new Thread(){
            @Override
            public void run() {
                MessageManager.deleteExceedMsg(timestamp); // 删除过期数据
                MessageManager.insert(msgList);  //插入数据
            }
        }.start();
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
