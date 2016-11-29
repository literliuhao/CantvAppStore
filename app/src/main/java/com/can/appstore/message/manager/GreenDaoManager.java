package com.can.appstore.message.manager;

import android.content.Context;

import com.can.appstore.message.db.dao.DaoMaster;
import com.can.appstore.message.db.dao.MessageInfoDao;
import com.can.appstore.message.db.entity.MessageInfo;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Created by HEKANG on 2016/10/21.
 */
public class GreenDaoManager {

    private MessageInfoDao msgDao;
    private final String DB_NAME = "AppStore.db";   // 数据库名称

    public GreenDaoManager(Context context) {
        /*
        * 初始化数据库
        * 数据库名称：AppStrore.db
        * 版本：1（gradle配置）
        * 表名：MESSAGE_INFO（消息数据存储表）
        * */
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        msgDao = new DaoMaster(devOpenHelper.getWritableDatabase()).newSession().getMessageInfoDao();
    }

    /**
     * 根据msgId更新对应的status
     *
     * @param msgId 消息id
     */
    public void updateStatus(String msgId) {
        MessageInfo msg = msgDao.queryBuilder().where(MessageInfoDao.Properties.MsgId.eq(msgId)).build().unique();
        if (msg != null) {
            msg.setStatus(false);
            msgDao.update(msg);
        }
    }

    /**
     * 更新全部数据status
     */
    public void updateAllMsgStatus() {
        //第一步：查询出status为true的数据
        QueryBuilder qb = msgDao.queryBuilder();
        qb.where(MessageInfoDao.Properties.Status.eq(1));
        final List<MessageInfo> msgList = qb.list();
        if (msgList == null || msgList.isEmpty()) {
            return;
        }
        //第二步：更新数据
        msgDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                int count = msgList.size();
                for (int i = 0; i < count; i++) {
                    msgList.get(i).setStatus(false);
                    msgDao.updateInTx(msgList.get(i));
                }
            }
        });
    }

    /**
     * 删除单条数据
     *通过msgId来进行删除
     * @param msgId
     */
    public void deleteMsg(String msgId) {
        MessageInfo msgInfo = msgDao.queryBuilder().where(MessageInfoDao.Properties.MsgId.eq(msgId)).build().unique();
        if (msgInfo != null) {
            msgDao.deleteByKeyInTx(msgInfo.get_id());
        }
    }

    /**
     * 删除过期数据
     *
     * @param timestamp
     */
    public void deleteExceedMsg(long timestamp) {
        QueryBuilder qb = msgDao.queryBuilder();
        qb.where(MessageInfoDao.Properties.MsgExpires.le(timestamp));
        qb.buildDelete();
    }

    /**
     * 清空数据
     */
    public void clear() {
        msgDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                msgDao.deleteAll();
            }
        });
    }

    /**
     * 查询数据库数据
     * @param mCurrentTime  // 当前系统时间
     *
     */
    public List<MessageInfo> queryMsg(long mCurrentTime) {
        QueryBuilder qb = msgDao.queryBuilder();
        qb.where(MessageInfoDao.Properties.MsgExpires.ge(mCurrentTime));
        qb.orderDesc(MessageInfoDao.Properties.MsgDate);
        List<MessageInfo> msgList = qb.list();
        if (msgList == null || msgList.isEmpty()) {
            return Collections.EMPTY_LIST;  // 返回一个空集合
        }
        return msgList;
    }

    /**
     * 查询数据库数据（无擦拭农户）
     */
    public List<MessageInfo> queryMsg() {
        QueryBuilder qb = msgDao.queryBuilder();
        qb.orderDesc(MessageInfoDao.Properties.MsgDate);
        List<MessageInfo> msgList = qb.list();
        if (msgList == null || msgList.isEmpty()) {
            return Collections.EMPTY_LIST;  // 返回一个空集合
        }
        return msgList;
    }

    /**
     * 查询数据表中是否有未读消息
     */
    public boolean existUnreadMsg() {
        QueryBuilder qb = msgDao.queryBuilder();
        qb.limit(1);
        qb.where(MessageInfoDao.Properties.Status.eq(1));
        List<MessageInfo> msgList = qb.list();
        if (msgList != null && !msgList.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 插入数据
     */
    public void insert(final List<MessageInfo> msgList) {
        if (msgList == null || msgList.isEmpty()) {
            return;
        }
        msgDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                msgDao.insertInTx(msgList);
            }
        });
    }

    /**
     * 插入数据
     * */
    public void insert(final MessageInfo msgInfo){
        if (msgInfo != null){
            msgDao.getSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    msgDao.insertInTx(msgInfo);
                }
            });
        }
    }

}
