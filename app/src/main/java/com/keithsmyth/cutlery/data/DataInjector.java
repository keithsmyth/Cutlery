package com.keithsmyth.cutlery.data;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;

public class DataInjector {

    private final Application application;

    @Nullable private TaskDao taskDao;
    @Nullable private TaskCompleteDao taskCompleteDao;
    @Nullable private IconDao iconDao;
    @Nullable private SQLiteOpenHelper sqLiteOpenHelper;
    @Nullable private UndoStack undoStack;
    @Nullable private FirebaseAnalytics firebaseAnalytics;

    public DataInjector(Application application) {
        this.application = application;
    }

    public TaskDao taskDao() {
        if (taskDao == null) {
            taskDao = new TaskDao(sqLiteOpenHelper());
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

    public UndoStack undoStack() {
        if (undoStack == null) {
            undoStack = new UndoStack();
        }
        return undoStack;
    }

    public FirebaseAnalytics firebaseAnalytics() {
        if (firebaseAnalytics == null) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(application);
        }
        return firebaseAnalytics;
    }
}
