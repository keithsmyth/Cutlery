package com.keithsmyth.cutlery.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.keithsmyth.cutlery.App;
import com.keithsmyth.cutlery.R;
import com.keithsmyth.cutlery.data.AsyncDataTaskListener;
import com.keithsmyth.cutlery.data.IconDao;
import com.keithsmyth.cutlery.data.TaskCompleteDao;
import com.keithsmyth.cutlery.data.TaskDao;
import com.keithsmyth.cutlery.model.Task;
import com.keithsmyth.cutlery.model.TaskComplete;

import java.util.Date;
import java.util.List;

public class DoFragment extends Fragment {

    @Nullable private Navigates navigates;
    @Nullable private TaskAdapter taskAdapter;

    private AsyncTaskDelegate asyncTaskDelegate;
    private IconDao iconDao;
    private TaskDao taskDao;
    private TaskCompleteDao taskCompleteDao;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigates = (Navigates) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asyncTaskDelegate = new AsyncTaskDelegate();
        iconDao = App.inject().iconDao();
        taskDao = App.inject().taskDao();
        taskCompleteDao = App.inject().taskCompleteDao();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_do, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView tasksRecycler = (RecyclerView) view.findViewById(R.id.tasks_recycler);
        tasksRecycler.setHasFixedSize(true);
        tasksRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(iconDao);
        tasksRecycler.setAdapter(taskAdapter);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeItemTouchHelperCallback(taskAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecycler);

        view.findViewById(R.id.create_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigates != null) {
                    navigates.to(new TaskFragment());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (taskAdapter != null) {
            taskAdapter.setTaskActionListener(new TaskActionListenerImpl());
        }

        fetchData();
    }

    @Override
    public void onStop() {
        asyncTaskDelegate.clearAsyncDataTasks();

        if (taskAdapter != null) {
            taskAdapter.setTaskActionListener(null);
        }

        super.onStop();
    }

    private void fetchData() {
        asyncTaskDelegate.registerAsyncDataTask(taskDao.list()
            .setListener(new GetTasksListenerImpl())
            .execute());
    }

    private class TaskActionListenerImpl implements TaskAdapter.TaskActionListener {

        @Override
        public void onTaskEdit(Task task) {
            if (navigates != null && getView() != null) {
                navigates.to(TaskFragment.editTask(task.id));
            }
        }

        @Override
        public void onCompleted(Task task) {
            if (getView() == null) { return; }
            final TaskComplete taskComplete = new TaskComplete(new Date().getTime(), task.id);
            asyncTaskDelegate.registerAsyncDataTask(taskCompleteDao.create(taskComplete)
                .setListener(new CreateTaskCompleteListenerImpl())
                .execute());
        }
    }

    private class GetTasksListenerImpl implements AsyncDataTaskListener<List<Task>> {

        // TODO: Add empty state

        @Override
        public void onSuccess(List<Task> tasks) {
            if (getView() != null && taskAdapter != null) {
                taskAdapter.setTasks(tasks);
            }
        }

        @Override
        public void onError(Exception e) {
            if (getView() != null) {
                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class CreateTaskCompleteListenerImpl implements AsyncDataTaskListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            if (getView() != null) {
                Snackbar.make(getView(), "Task completed", Snackbar.LENGTH_SHORT).show();
                fetchData();
            }
        }

        @Override
        public void onError(Exception e) {
            if (getView() != null) {
                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}