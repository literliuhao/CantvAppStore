package com.can.appstore.message.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.can.appstore.ActionConstants;
import com.can.appstore.entity.Message;
import com.can.appstore.entity.MessageContainer;
import com.can.appstore.entity.Result;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;
import com.can.appstore.message.db.entity.MessageInfo;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.NetworkUtils;
import retrofit2.Response;

/**
 * Created by HEKANG on 2016/11/8.
 * 消息数据操作
 */

public class MessageManager {

    private static final  String TAG = "MessageManager";

    // 数据库消息数据更新
    private static  CallMsgDataUpdate mCallMsgDataUpdate;
    public  interface CallMsgDataUpdate {
        void onUpdate();
    }



    public static void setCallMsgDataUpdate(CallMsgDataUpdate callMsgDataUpdate) {
        mCallMsgDataUpdate = callMsgDataUpdate;
    }

    /**
     * 删除数据库过期消息数据
     * 参数：从服务器解析拿到的时间戳
     */
    public static void deleteExceedMsg( Context context , long timestamp) {
        new GreenDaoManager(context).deleteExceedMsg(timestamp);
    }

    /**
     * 插入数据
     * 参数：插入的数据集合
     */
    public static void insert( Context context , List<MessageInfo> msgList) {
        new GreenDaoManager(context).insert(msgList);
        if (mCallMsgDataUpdate != null){
            mCallMsgDataUpdate.onUpdate();
        }
    }

    /**
     * 检查数据库是否有未读消息
     */
    public static boolean existUnreadMsg(Context context) {
        return new GreenDaoManager(context).existUnreadMsg();
    }

    public static void  deleteMsg(Context context , String msgId){
        new GreenDaoManager(context).deleteMsg(msgId);
    }

    public static  void updateStatus(Context context , String msgId){
        new GreenDaoManager(context).updateStatus(msgId);
    }

    /**
     * 请求服务器消息数据
     * */
    public static void requestMsg(final  Context context){
        if (NetworkUtils.isNetworkConnected(context)){
            CanCall<Result<MessageContainer>> mMessageContainer = HttpManager.getApiService().getMessages();
            mMessageContainer.enqueue(new CanCallback<Result<MessageContainer>>() {
                @Override
                public void onResponse(CanCall<Result<MessageContainer>> call, Response<Result<MessageContainer>> response) throws Exception {
                    Result<MessageContainer> body = response.body();
                    MessageContainer msgContainer = body.getData();
                    if (msgContainer == null){
                        Log.w(TAG, "message data error");
                        return;
                    }
                    long timestamp = msgContainer.getTimestamp();
                    MessageManager.deleteExceedMsg( context ,timestamp); // 删除过期数据
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
                            //接口数据异常处理
                            if (TextUtils.isEmpty(msg.getAction()) || TextUtils.isEmpty(msg.getActionData())){
                                msgInfo.setAction(ActionConstants.ACTION_NOTHIN);
                            }else{
                                msgInfo.setAction(msg.getAction().trim());  //处理数据首尾空格（经测试会出现首尾有空格情况）
                                msgInfo.setActionData(msg.getActionData());
                            }
                            msgInfo.setUserId(NetworkUtils.getMac());
                            msgList.add(msgInfo);
                        }
                        MessageManager.insert(context , msgList); // 插入数据库
                    }else{
                        Log.i(TAG, "no new message");
                    }
                }

                @Override
                public void onFailure(CanCall<Result<MessageContainer>> call, CanErrorWrapper errorWrapper) {
                    Log.w(TAG, errorWrapper.getReason(), errorWrapper.getThrowable());
                }
            });
        }
    }

}
