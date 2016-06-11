package com.keithsmyth.cutlery.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.keithsmyth.cutlery.model.Task;
import com.keithsmyth.cutlery.model.TaskListItem;

import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaskDao {

    public static final String TABLE = "Task";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_FREQUENCY = "frequency";
    public static final String COL_FREQUENCY_VALUE = "frequencyValue";
    public static final String COL_ICON_ID = "iconId";
    public static final String COL_COLOUR = "colour";
    public static final String COL_ARCHIVED = "archived";

    public static final String[] COLS = {
        COL_ID,
        COL_NAME,
        COL_FREQUENCY,
        COL_FREQUENCY_VALUE,
        COL_ICON_ID,
        COL_COLOUR,
        COL_ARCHIVED
    };

    private static final int ALL_TASK_ID = -1;

    private final SQLiteOpenHelper sqLiteOpenHelper;

    public TaskDao(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
    }

    public static final String CREATE = "create table " + TABLE + " (" +
        COL_ID + " integer not null primary key," +
        COL_NAME + " text not null," +
        COL_FREQUENCY + " text not null," +
        COL_FREQUENCY_VALUE + " integer not null," +
        COL_ICON_ID + " integer not null," +
        COL_COLOUR + " text not null, " +
        COL_ARCHIVED + " boolean not null" +
        ")";

    public static void upgrade(SQLiteDatabase db, int oldVersion) {
        if (oldVersion == 1) {
            final String sql = "alter table " + TABLE + " add column " + COL_ARCHIVED + " boolean";
            db.execSQL(sql);
            final ContentValues values = new ContentValues(1);
            values.put(COL_ARCHIVED, false);
            db.update(TABLE, values, null, null);
        }
    }

    public AsyncDataTask<List<TaskListItem>> list() {
        final String sql = buildTaskListItemSql(ALL_TASK_ID);

        return new AsyncDataTask<List<TaskListItem>>(false) {
            @Override
            public List<TaskListItem> task() {
                final SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
                try (final Cursor cursor = db.rawQuery(sql, null)) {
                    if (cursor == null) { return Collections.emptyList(); }
                    final List<TaskListItem> tasks = new ArrayList<>(cursor.getCount());
                    while (cursor.moveToNext()) {
                        tasks.add(mapToTaskListItem(cursor));
                    }
                    Collections.sort(tasks, new Comparator<TaskListItem>() {
                        @Override
                        public int compare(TaskListItem lhs, TaskListItem rhs) {
                            // reverse order for {@link TaskListItem.daysOverDue}
                            return Integer.compare(rhs.daysOverDue, lhs.daysOverDue);
                        }
                    });
                    return tasks;
                }
            }
        };
    }

    public AsyncDataTask<TaskListItem> listSingle(final int taskId) {
        final String sql = buildTaskListItemSql(taskId);
        return new AsyncDataTask<TaskListItem>(false) {
            @Override
            public TaskListItem task() {
                final SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
                try (final Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(taskId)})) {
                    if (cursor != null && cursor.moveToNext()) {
                        return mapToTaskListItem(cursor);
                    }
                    return null;
                }
            }
        };
    }

    public AsyncDataTask<Task> get(final int taskId) {
        return new AsyncDataTask<Task>(false) {
            @Override
            public Task task() {
                final SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
                try (final Cursor cursor = db.query(TABLE, COLS, COL_ID + " = ?", new String[]{String.valueOf(taskId)}, null, null, null)) {
                    if (cursor == null || !cursor.moveToNext()) { return null; }
                    return mapToTask(cursor);
                }
            }
        };
    }

    @NonNull
    public AsyncDataTask<Void> create(Task task) {
        final ContentValues values = new ContentValues(5);
        values.put(COL_NAME, task.name);
        values.put(COL_FREQUENCY, task.frequency);
        values.put(COL_FREQUENCY_VALUE, task.frequencyValue);
        values.put(COL_ICON_ID, task.iconId);
        values.put(COL_COLOUR, task.colour);
        values.put(COL_ARCHIVED, task.isArchived);
        return new AsyncDataTask<Void>(true) {
            @Override
            public Void task() {
                final SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
                db.insert(TABLE, null, values);
                return null;
            }
        };
    }

    @NonNull
    public AsyncDataTask<Void> update(final Task task) {
        return new AsyncDataTask<Void>(true) {
            @Override
            public Void task() {
                final SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
                final ContentValues values = new ContentValues(6);
                values.put(COL_ID, task.id);
                values.put(COL_NAME, task.name);
                values.put(COL_FREQUENCY, task.frequency);
                values.put(COL_FREQUENCY_VALUE, task.frequencyValue);
                values.put(COL_ICON_ID, task.iconId);
                values.put(COL_COLOUR, task.colour);
                values.put(COL_ARCHIVED, task.isArchived);
                db.update(TABLE, values, COL_ID + " = ?", new String[]{String.valueOf(task.id)});
                return null;
            }
        };
    }

    public AsyncDataTask<Void> delete(final int id) {
        return new AsyncDataTask<Void>(true) {
            @Override
            public Void task() {
                updateTaskArchived(id, true);
                return null;
            }
        };
    }

    public AsyncDataTask<Void> undoDelete(final int id) {
        return new AsyncDataTask<Void>(true) {
            @Override
            public Void task() {
                updateTaskArchived(id, false);
                return null;
            }
        };
    }

    private String buildTaskListItemSql(int taskId) {
        final StringBuilder builder = new StringBuilder("select t." + COL_ID + "," +
            "t." + COL_NAME + "," +
            "t." + COL_FREQUENCY + "," +
            "t." + COL_FREQUENCY_VALUE + "," +
            "t." + COL_ICON_ID + "," +
            "t." + COL_COLOUR + "," +
            "t." + COL_ARCHIVED + "," +
            "MAX(c." + TaskCompleteDao.COL_DATE_TIME + ") as dateTimeLastDone " +
            "from " + TABLE + " t " +
            "left outer join " + TaskCompleteDao.TABLE + " c " +
            "on t." + COL_ID + " = c." + TaskCompleteDao.COL_TASK_ID + " " +
            "where t." + COL_ARCHIVED + " = 0 ");
        if (taskId != ALL_TASK_ID) {
            builder.append("and t." + COL_ID + " = ? ");
        }
        builder.append("group by t." + COL_ID + "," +
            "t." + COL_NAME + "," +
            "t." + COL_FREQUENCY + "," +
            "t." + COL_FREQUENCY_VALUE + "," +
            "t." + COL_ICON_ID + "," +
            "t." + COL_COLOUR + "," +
            "t." + COL_ARCHIVED);
        return builder.toString();
    }

    private void updateTaskArchived(int id, boolean value) {
        final SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        final ContentValues values = new ContentValues(1);
        values.put(COL_ARCHIVED, value);
        db.update(TABLE, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    private Task mapToTask(Cursor cursor) {
        final int id = cursor.getInt(0);
        final String name = cursor.getString(1);
        final String frequency = cursor.getString(2);
        final int frequencyValue = cursor.getInt(3);
        final int iconId = cursor.getInt(4);
        final int colour = cursor.getInt(5);
        final boolean isArchived = cursor.getInt(6) != 0;

        return new Task(id,
            name,
            frequency,
            frequencyValue,
            iconId,
            colour,
            isArchived);
    }

    private TaskListItem mapToTaskListItem(Cursor cursor) {
        final Task task = mapToTask(cursor);
        final long dateTimeLastDone = cursor.getLong(7);
        final int daysOverDue = calcDaysOverDue(dateTimeLastDone, task.frequency, task.frequencyValue);
        return new TaskListItem(task, daysOverDue);
    }

    private int calcDaysOverDue(long dateTimeLastDone,
                                @Nullable String frequency, int frequencyValue) {
        if (dateTimeLastDone == 0) {
            return 0; // Never been done, due today
        }

        final long dayLastDone = DateUtils.truncate(new Date(dateTimeLastDone), Calendar.DAY_OF_MONTH).getTime();

        final long now = new Date().getTime();
        final long nextDue;
        switch (frequency == null ? "" : frequency) {
            case Task.FREQUENCY_EVERY_DAYS:
                nextDue = dayLastDone + TimeUnit.DAYS.toMillis(frequencyValue + 1);
                break;
            case Task.FREQUENCY_EVERY_WEEKS:
                nextDue = dayLastDone + TimeUnit.DAYS.toMillis((7 * frequencyValue) + 1);
                break;
            case Task.FREQUENCY_EVERY_MONTHS:
                final Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(dayLastDone);
                cal.add(Calendar.MONTH, frequencyValue);
                nextDue = cal.getTimeInMillis() + TimeUnit.DAYS.toMillis(1);
                break;
            default:
                Log.e(getClass().getSimpleName(), "Unknown frequency [" + frequency + "]");
                nextDue = now;
        }

        // calculate days overdue
        return (int) TimeUnit.DAYS.convert(now - nextDue, TimeUnit.MILLISECONDS);
    }
}
