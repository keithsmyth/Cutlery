package com.keithsmyth.cutlery.data;

/**
 * In memory Undo-stack for inter Fragment communication. Works with a poll pattern
 */
public class UndoStack {

    public static final int NO_TASK = -1;

    private int taskId = NO_TASK;

    public void setDeleteTask(int taskId) {
        this.taskId = taskId;
    }

    public int getDeleteTaskId() {
        return taskId;
    }
}
