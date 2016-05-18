package com.keithsmyth.cutlery.view;

import com.keithsmyth.cutlery.data.AsyncDataTask;

import java.util.ArrayList;
import java.util.List;

public class AsyncTaskDelegate {

    private final List<AsyncDataTask<?>> dataTasks;

    public AsyncTaskDelegate() {
        dataTasks = new ArrayList<>();
    }

    public void registerAsyncDataTask(AsyncDataTask<?> asyncDataTask) {
        dataTasks.add(asyncDataTask);
    }

    public void clearAsyncDataTasks() {
        for (AsyncDataTask<?> dataTask : dataTasks) {
            dataTask.setListener(null);
        }
        dataTasks.clear();
    }
}
