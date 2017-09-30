package com.aidebar.greendaotest.gen;

import android.content.Context;

/**
 * Created by ZhangHaiLong on 2017/9/14.
 */

public class DaoManager { private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static volatile DaoManager mInstance = null;

    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = true;

    public static final String DATABASE_NAME = "yi-moments-db";
    public static final String DATABASE_NAME_ENCRYPTED = "yi-moments-db-encrypted";
    public static final String DATABASE_NAME_ENCRYPTED_KEY = "yi-moments-super-secret";

    private DaoManager(Context context){
        if (mInstance == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context,  ENCRYPTED ? DATABASE_NAME_ENCRYPTED : DATABASE_NAME);
            mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
            mDaoSession = mDaoMaster.newSession();
        }
    }
    public static DaoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DaoManager.class) {
                if (mInstance == null) {
                    mInstance = new DaoManager(context);
                }
            }
        }
        return mInstance;
    }
    public DaoMaster getMaster() {
        return mDaoMaster;
    }
    public DaoSession getSession() {
        return mDaoSession;
    }
    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }



}
