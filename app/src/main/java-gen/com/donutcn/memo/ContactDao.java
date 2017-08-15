package com.donutcn.memo;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.donutcn.memo.entity.Contact;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CONTACT".
*/
public class ContactDao extends AbstractDao<Contact, String> {

    public static final String TABLENAME = "CONTACT";

    /**
     * Properties of entity Contact.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UserId = new Property(0, String.class, "userId", true, "USER_ID");
        public final static Property DisplayName = new Property(1, String.class, "displayName", false, "DISPLAY_NAME");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Avatar = new Property(3, String.class, "avatar", false, "AVATAR");
        public final static Property PhoneNum = new Property(4, String.class, "phoneNum", false, "PHONE_NUM");
        public final static Property SortKey = new Property(5, String.class, "sortKey", false, "SORT_KEY");
        public final static Property LookUpKey = new Property(6, String.class, "lookUpKey", false, "LOOK_UP_KEY");
    }


    public ContactDao(DaoConfig config) {
        super(config);
    }
    
    public ContactDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CONTACT\" (" + //
                "\"USER_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: userId
                "\"DISPLAY_NAME\" TEXT," + // 1: displayName
                "\"NAME\" TEXT," + // 2: name
                "\"AVATAR\" TEXT," + // 3: avatar
                "\"PHONE_NUM\" TEXT," + // 4: phoneNum
                "\"SORT_KEY\" TEXT," + // 5: sortKey
                "\"LOOK_UP_KEY\" TEXT);"); // 6: lookUpKey
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CONTACT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Contact entity) {
        stmt.clearBindings();
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(1, userId);
        }
 
        String displayName = entity.getDisplayName();
        if (displayName != null) {
            stmt.bindString(2, displayName);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(4, avatar);
        }
 
        String phoneNum = entity.getPhoneNum();
        if (phoneNum != null) {
            stmt.bindString(5, phoneNum);
        }
 
        String sortKey = entity.getSortKey();
        if (sortKey != null) {
            stmt.bindString(6, sortKey);
        }
 
        String lookUpKey = entity.getLookUpKey();
        if (lookUpKey != null) {
            stmt.bindString(7, lookUpKey);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Contact entity) {
        stmt.clearBindings();
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(1, userId);
        }
 
        String displayName = entity.getDisplayName();
        if (displayName != null) {
            stmt.bindString(2, displayName);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(4, avatar);
        }
 
        String phoneNum = entity.getPhoneNum();
        if (phoneNum != null) {
            stmt.bindString(5, phoneNum);
        }
 
        String sortKey = entity.getSortKey();
        if (sortKey != null) {
            stmt.bindString(6, sortKey);
        }
 
        String lookUpKey = entity.getLookUpKey();
        if (lookUpKey != null) {
            stmt.bindString(7, lookUpKey);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public Contact readEntity(Cursor cursor, int offset) {
        Contact entity = new Contact( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // userId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // displayName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // avatar
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // phoneNum
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // sortKey
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // lookUpKey
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Contact entity, int offset) {
        entity.setUserId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setDisplayName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAvatar(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPhoneNum(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSortKey(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLookUpKey(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final String updateKeyAfterInsert(Contact entity, long rowId) {
        return entity.getUserId();
    }
    
    @Override
    public String getKey(Contact entity) {
        if(entity != null) {
            return entity.getUserId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Contact entity) {
        return entity.getUserId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
