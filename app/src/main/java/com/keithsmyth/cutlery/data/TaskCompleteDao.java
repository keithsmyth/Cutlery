package com.keithsmyth.cutlery.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.keithsmyth.cutlery.model.TaskComplete;

public class TaskCompleteDao {

    public static final String TABLE = "TaskComplete";
    public static final String COL_DATE_TIME = "dateTime";
    public static final String COL_TASK_ID = "taskId";

    public static final String[] COLS = {
        COL_DATE_TIME,
        COL_TASK_ID
    };

    public static final String CREATE = "create table " + TABLE + " (" +
        COL_DATE_TIME + " integer not null," +
        COL_TASK_ID + " integer not null references " + TaskDao.TABLE + "(" + TaskDao.COL_ID + ")" +
        ")";
    private final SQLiteOpenHelper sqLiteOpenHelper;

    public TaskCompleteDao(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
    }

    public AsyncDataTask<Void> create(TaskComplete taskComplete) {
        final ContentValues values = new ContentValues(2);
        values.put(COL_DATE_TIME, taskComplete.dateTime);
        values.put(COL_TASK_ID, taskComplete.taskId);
        return new AsyncDataTask<Void>(true) {
            @Override
            public Void task() {
                final SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
                db.insert(TABLE, null, values);
                return null;
            }
        };
    }

    public void deleteChildren(int taskId, SQLiteDatabase db) {
        db.delete(TABLE, COL_TASK_ID + " = ?", new String[] {String.valueOf(taskId)});
    }
}
