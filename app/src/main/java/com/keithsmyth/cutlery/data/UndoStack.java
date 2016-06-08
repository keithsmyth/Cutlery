package com.keithsmyth.cutlery.data;

import android.util.SparseIntArray;

/**
 * In memory Undo-stack for inter Fragment communication. Works with a poll pattern
 */
public class UndoStack {

    public static final int NO_TASK = -1;

    private int taskId = NO_TASK;
    private final SparseIntArray taskIdToCompleteTaskId = new SparseIntArray();

    public void setDeleteTask(int taskId) {
        this.taskId = taskId;
    }

    public int getDeleteTaskId() {
        return taskId;
    }

    public void setLatestTaskCompleteId(int taskId, int taskCompleteId) {
        taskIdToCompleteTaskId.put(taskId, taskCompleteId);
    }

    public int getLatestTaskCompeteId(int taskId) {
        return taskIdToCompleteTaskId.get(taskId, NO_TASK);
    }

    public void clearLatestTaskCompleteIds() {
        taskIdToCompleteTaskId.clear();
    }
}
