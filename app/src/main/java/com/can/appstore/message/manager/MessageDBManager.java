package com.can.appstore.message.manager;

import android.content.Context;
import android.util.Log;

import com.can.appstore.db.GreenDaoManager;
import com.can.appstore.db.entity.MessageInfo;
import com.can.appstore.db.msgdao.MessageInfoDao;
import com.can.appstore.entity.MessageContainer;
import com.can.appstore.entity.Result;
import com.can.appstore.http.CanCall;
import com.can.appstore.http.CanCallback;
import com.can.appstore.http.CanErrorWrapper;
import com.can.appstore.http.HttpManager;

import java.util.List;

import cn.can.tvlib.utils.NetworkUtils;
import retrofit2.Response;

/**
 * Created by HEKANG on 2016/11/8.
 * 消息数据操作
 */

public class MessageDBManager {

    private final String TAG = "MessageDBManager";

    private MessageInfoDao msgDao;
    private OnReceivedMsgListener mOnReceivedMsgListener;

    public MessageDBManager(Context context) {
        msgDao = GreenDaoManager.getInstance(context).getMsgDao();
    }

    public interface OnReceivedMsgListener {
        void onReceivedMsg();
    }

    public void setOnReceivedMsgListener(OnReceivedMsgListener onReceivedMsgListener) {
        mOnReceivedMsgListener = onReceivedMsgListener;
    }

    public void removeOnReceivedMsgListener() {
        mOnReceivedMsgListener = null;
    }

    /**
     * 删除数据库过期消息数据
     * 参数timestamp：从服务器返回数据解析的时间戳
     */
    private void deleteOverdueMsg(long timestamp) {
        msgDao.queryBuilder()
                .where(MessageInfoDao.Properties.Expires.le(timestamp))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    private void insertMsgList(final List<MessageInfo> msgList) {
        msgDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                msgDao.insertInTx(msgList);
            }
        });
        if (mOnReceivedMsgListener != null) {
            mOnReceivedMsgListener.onReceivedMsg();
        }
    }

    /**
     * 在数据库中是否有未读消息
     * 返回值：true表示有   false表示没有
     */
    public boolean existUnreadMsg() {
        return msgDao.queryBuilder()
                .where(MessageInfoDao.Properties.Status.eq(0))
                .count() > 0;
    }

    /**
     * 从数据库中删除单条消息数据
     * 参数：该条数据的主键
     */
    public void deleteMsg(long _id) {
        msgDao.deleteByKeyInTx(_id);
    }

    public void setMsgRead(long _id) {
        MessageInfo msg = msgDao.queryBuilder().where(MessageInfoDao.Properties._id.eq(_id)).build().unique();
        if (msg != null) {
            msg.setStatus(true);
            msgDao.update(msg);
        }
    }

    public void setAllMsgRead(final List<MessageInfo> msgList) {
        msgDao.deleteAll();
        msgDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                msgDao.insertInTx(msgList);
            }
        });
    }

    public void clearAllMsg() {
        msgDao.deleteAll();
    }

    public List<MessageInfo> queryMsgList() {
        List<MessageInfo> msgList = msgDao.queryBuilder()
                .orderDesc(MessageInfoDao.Properties.Date)
                .list();
        if (msgList == null || msgList.isEmpty()) {
            return null;
        }
        return msgList;
    }

    /**
     * 请求服务器消息数据
     */
    public void requestMsgData(Context context) {
        //假数据
        /*List<MessageInfo> msgList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MessageInfo msg = new MessageInfo();
            msg.setAction(ActionConstants.ACTION_TOPIC_DETAIL);
            msg.setActionData("1");
            msg.setDate(1482480855+i+"");
            msg.setExpires(1485074460);
            msg.setId("123");
            msg.setTitle("啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊阿萨德飞凤飞飞凤飞飞凤飞飞凤飞飞凤飞飞凤飞飞凤飞飞凤飞飞凤飞飞凤飞飞凤飞");
            msgList.add(msg);
        }
        insertMsgList(msgList);*/
        if (NetworkUtils.isNetworkConnected(context)) {
            CanCall<Result<MessageContainer>> mMessageContainer = HttpManager.getApiService().getMessages();
            mMessageContainer.enqueue(new CanCallback<Result<MessageContainer>>() {
                @Override
                public void onResponse(CanCall<Result<MessageContainer>> call, final Response<Result<MessageContainer>> response) throws Exception {
                    MessageContainer msgContainer = response.body().getData();
                    if (msgContainer == null) {
                        Log.w(TAG, "Request message data error");
                        return;
                    }
                    long timestamp = msgContainer.getTimestamp();
                    if (timestamp > 0) {
                        deleteOverdueMsg(timestamp);
                    }
                    List<MessageInfo> list = msgContainer.getMessages();
                    if (list != null && !list.isEmpty()) {
                        insertMsgList(list);
                    } else {
                        Log.i(TAG, "No new message");
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
