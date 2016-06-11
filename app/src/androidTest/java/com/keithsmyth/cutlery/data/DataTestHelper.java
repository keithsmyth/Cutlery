package com.keithsmyth.cutlery.data;


import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class DataTestHelper {

    private DataTestHelper() {
        // no instances
    }

    public static void clearData(Application application) {
        final CutleryOpenHelper openHelper = new CutleryOpenHelper(application);
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            for (String table : new String[]{TaskDao.TABLE, TaskCompleteDao.TABLE}) {
                db.execSQL("delete from " + table);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
