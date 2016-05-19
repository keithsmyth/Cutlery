package com.keithsmyth.cutlery.model;

public class TaskComplete {

    public final int id;
    public final long dateTime;
    public final int taskId;

    public TaskComplete(int id, long dateTime, int taskId) {
        this.id = id;
        this.dateTime = dateTime;
        this.taskId = taskId;
    }
}
