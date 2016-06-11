package com.keithsmyth.cutlery.model;


public class TaskListItem {

    public final Task task;
    public final int daysOverDue;

    public TaskListItem(Task task, int daysOverDue) {
        this.task = task;
        this.daysOverDue = daysOverDue;
    }
}
