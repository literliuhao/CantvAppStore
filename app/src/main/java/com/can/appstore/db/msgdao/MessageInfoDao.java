package com.can.appstore.db.msgdao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.can.appstore.db.entity.MessageInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MESSAGE_INFO".
*/
public class MessageInfoDao extends AbstractDao<MessageInfo, Long> {

    public static final String TABLENAME = "MESSAGE_INFO";

    /**
     * Properties of entity MessageInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Id = new Property(1, String.class, "id", false, "ID");
        public final static Property Date = new Property(2, String.class, "date", false, "DATE");
        public final static Property Expires = new Property(3, long.class, "expires", false, "EXPIRES");
        public final static Property Title = new Property(4, String.class, "title", false, "TITLE");
        public final static Property Status = new Property(5, boolean.class, "status", false, "STATUS");
        public final static Property Action = new Property(6, String.class, "action", false, "ACTION");
        public final static Property ActionData = new Property(7, String.class, "actionData", false, "ACTION_DATA");
        public final static Property UserId = new Property(8, String.class, "userId", false, "USER_ID");
        public final static Property Extra1 = new Property(9, String.class, "extra1", false, "EXTRA1");
        public final static Property Extra2 = new Property(10, String.class, "extra2", false, "EXTRA2");
    };


    public MessageInfoDao(DaoConfig config) {
        super(config);
    }
    
    public MessageInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MESSAGE_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: _id
                "\"ID\" TEXT," + // 1: id
                "\"DATE\" TEXT," + // 2: date
                "\"EXPIRES\" INTEGER NOT NULL ," + // 3: expires
                "\"TITLE\" TEXT," + // 4: title
                "\"STATUS\" INTEGER NOT NULL ," + // 5: status
                "\"ACTION\" TEXT," + // 6: action
                "\"ACTION_DATA\" TEXT," + // 7: actionData
                "\"USER_ID\" TEXT," + // 8: userId
                "\"EXTRA1\" TEXT," + // 9: extra1
                "\"EXTRA2\" TEXT);"); // 10: extra2
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MESSAGE_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MessageInfo entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(2, id);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(3, date);
        }
        stmt.bindLong(4, entity.getExpires());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(5, title);
        }
        stmt.bindLong(6, entity.getStatus() ? 1L: 0L);
 
        String action = entity.getAction();
        if (action != null) {
            stmt.bindString(7, action);
        }
 
        String actionData = entity.getActionData();
        if (actionData != null) {
            stmt.bindString(8, actionData);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(9, userId);
        }
 
        String extra1 = entity.getExtra1();
        if (extra1 != null) {
            stmt.bindString(10, extra1);
        }
 
        String extra2 = entity.getExtra2();
        if (extra2 != null) {
            stmt.bindString(11, extra2);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MessageInfo entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(2, id);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(3, date);
        }
        stmt.bindLong(4, entity.getExpires());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(5, title);
        }
        stmt.bindLong(6, entity.getStatus() ? 1L: 0L);
 
        String action = entity.getAction();
        if (action != null) {
            stmt.bindString(7, action);
        }
 
        String actionData = entity.getActionData();
        if (actionData != null) {
            stmt.bindString(8, actionData);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(9, userId);
        }
 
        String extra1 = entity.getExtra1();
        if (extra1 != null) {
            stmt.bindString(10, extra1);
        }
 
        String extra2 = entity.getExtra2();
        if (extra2 != null) {
            stmt.bindString(11, extra2);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MessageInfo readEntity(Cursor cursor, int offset) {
        MessageInfo entity = new MessageInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // date
            cursor.getLong(offset + 3), // expires
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // title
            cursor.getShort(offset + 5) != 0, // status
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // action
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // actionData
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // userId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // extra1
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // extra2
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MessageInfo entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDate(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setExpires(cursor.getLong(offset + 3));
        entity.setTitle(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStatus(cursor.getShort(offset + 5) != 0);
        entity.setAction(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setActionData(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setUserId(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setExtra1(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setExtra2(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MessageInfo entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MessageInfo entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
