package com.keithsmyth.cutlery.data;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DataInjector {

    private final Application application;

    @Nullable private TaskDao taskDao;
    @Nullable private TaskCompleteDao taskCompleteDao;
    @Nullable private IconDao iconDao;
    @Nullable private SQLiteOpenHelper sqLiteOpenHelper;

    public DataInjector(Application application) {
        this.application = application;
    }

    public TaskDao taskDao() {
        if (taskDao == null) {
            taskDao = new TaskDao(sqLiteOpenHelper(), taskCompleteDao());
        }
        return taskDao;
    }

    public TaskCompleteDao taskCompleteDao() {
        if (taskCompleteDao == null) {
            taskCompleteDao = new TaskCompleteDao(sqLiteOpenHelper());
        }
        return taskCompleteDao;
    }

    public IconDao iconDao() {
        if (iconDao == null) {
            iconDao = new IconDao();
        }
        return iconDao;
    }

    private SQLiteOpenHelper sqLiteOpenHelper() {
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new CutleryOpenHelper(application);
        }
        return sqLiteOpenHelper;
    }
}
