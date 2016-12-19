package com.can.appstore.db;

import android.content.Context;

import com.can.appstore.db.msgdao.DaoMaster;
import com.can.appstore.db.msgdao.MessageInfoDao;


/**
 * Created by HEKANG on 2016/10/21.
 * 数据库说明：
 * 数据库名称： AppStore.db
 * 版本：1（见gradle配置）
 * 数据表名：MESSAGE_INFO（见MessageInfoDao）
 * 基本描述：该数据库隶属于CANAppStore项目。有一张数据表MESSAGE_INFO，存放从服务器请求到的消息数据。
 */
public class GreenDaoManager {

    private final String DB_NAME = "AppStore.db";
    private static volatile GreenDaoManager mInstance;
    private DaoMaster daoMaster;

    private GreenDaoManager(Context context) {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        daoMaster = new DaoMaster(openHelper.getWritableDatabase());
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static GreenDaoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {
                if (mInstance == null) {
                    mInstance = new GreenDaoManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public MessageInfoDao getMsgDao() {
        return daoMaster.newSession().getMessageInfoDao();
    }
}
