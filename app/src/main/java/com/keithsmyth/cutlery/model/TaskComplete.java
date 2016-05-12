package com.keithsmyth.cutlery.model;

public class TaskComplete {

    public final long dateTime;
    public final int taskId;

    public TaskComplete(long dateTime, int taskId) {
        this.dateTime = dateTime;
        this.taskId = taskId;
    }
}
