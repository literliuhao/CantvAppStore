package com.can.appstore.message.manager;

import com.can.appstore.message.db.entity.MessageInfo;

import java.util.List;

/**
 * Created by HEKANG on 2016/11/8.
 * 消息数据操作
 */

public class MessageManager {

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
    public static void deleteExceedMsg(long timestamp) {
        new GreenDaoManager().deleteExceedMsg(timestamp);
    }

    /**
     * 插入数据
     * 参数：插入的数据集合
     */
    public static void insert(List<MessageInfo> msgList) {
        new GreenDaoManager().insert(msgList);
        if (mCallMsgDataUpdate != null){
            mCallMsgDataUpdate.onUpdate();
        }
    }

    /**
     * 检查数据库是否有未读消息
     */
    public static boolean existUnreadMsg() {
        return new GreenDaoManager().existUnreadMsg();
    }

}
