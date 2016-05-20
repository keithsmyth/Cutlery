package com.keithsmyth.cutlery.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.keithsmyth.cutlery.model.TaskComplete;

public class TaskCompleteDao {

    public static final String TABLE = "TaskComplete";
    public static final String COL_ID = "id";
    public static final String COL_DATE_TIME = "dateTime";
    public static final String COL_TASK_ID = "taskId";

    public static final String[] COLS = {
        COL_ID,
        COL_DATE_TIME,
        COL_TASK_ID
    };

    public static final String CREATE = "create table " + TABLE + " (" +
        COL_ID + " integer not null primary key," +
        COL_DATE_TIME + " integer not null," +
        COL_TASK_ID + " integer not null references " + TaskDao.TABLE + "(" + TaskDao.COL_ID + ")" +
        ")";

    public static void upgrade(SQLiteDatabase db, int oldVersion) {
        if (oldVersion == 1) {
            String sql = "alter table " + TABLE + " rename to temp";
            db.execSQL(sql);
            db.execSQL(CREATE);
            sql = "insert into " + TABLE + " (" +
                COL_DATE_TIME + "," +
                COL_TASK_ID + ") " +
                "select " + COL_DATE_TIME + ", " +
                COL_TASK_ID + " " +
                "from temp";
            db.execSQL(sql);
            db.execSQL("drop table temp");
        }
    }

    private final SQLiteOpenHelper sqLiteOpenHelper;

    public TaskCompleteDao(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
    }

    public AsyncDataTask<Integer> create(TaskComplete taskComplete) {
        final ContentValues values = new ContentValues(2);
        values.put(COL_DATE_TIME, taskComplete.dateTime);
        values.put(COL_TASK_ID, taskComplete.taskId);
        return new AsyncDataTask<Integer>(true) {
            @Override
            public Integer task() {
                final SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
                return (int) db.insert(TABLE, null, values);
            }
        };
    }

    public AsyncDataTask<Void> delete(final int taskCompleteId) {
        return new AsyncDataTask<Void>(true) {
            @Override
            public Void task() {
                final SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
                db.delete(TABLE, COL_ID + " = ?", new String[]{String.valueOf(taskCompleteId)});
                return null;
            }
        };
    }
}
