package com.keithsmyth.cutlery.data;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CutleryOpenHelper extends SQLiteOpenHelper {

    private static final String NAME = "cutlery.db";
    private static final int VERSION = 2;

    public CutleryOpenHelper(Application application) {
        super(application, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(TaskDao.CREATE);
            db.execSQL(TaskCompleteDao.CREATE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.beginTransaction();
            TaskDao.upgrade(db, oldVersion);
            TaskCompleteDao.upgrade(db, oldVersion);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
