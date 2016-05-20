package com.keithsmyth.cutlery.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.keithsmyth.cutlery.data.UndoStack;
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
    private UndoStack undoStack;

    private View emptyView;

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
        undoStack = App.inject().undoStack();
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

        final FloatingActionButton createFab = (FloatingActionButton) view.findViewById(R.id.create_fab);
        createFab.setColorFilter(Color.WHITE);
        createFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigates != null) {
                    navigates.to(new TaskFragment());
                }
            }
        });

        emptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (navigates != null) {
            navigates.setTitle(R.string.app_name);
        }

        if (taskAdapter != null) {
            taskAdapter.setTaskActionListener(new TaskActionListenerImpl());
        }

        fetchData();

        processUndoStack();
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

    private void processUndoStack() {
        final int taskId = undoStack.getDeleteTaskId();
        if (taskId == UndoStack.NO_TASK || getView() == null) { return; }

        undoStack.setDeleteTask(UndoStack.NO_TASK);
        Snackbar.make(getView(), R.string.task_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    asyncTaskDelegate.registerAsyncDataTask(taskDao.undoDelete(taskId)
                        .setListener(new UndoCompleteListenerImpl())
                        .execute());
                }
            })
            .show();
    }

    private void showEmptyView(boolean shouldShow) {
        emptyView.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
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
            final TaskComplete taskComplete = new TaskComplete(-1, new Date().getTime(), task.id);
            asyncTaskDelegate.registerAsyncDataTask(taskCompleteDao.create(taskComplete)
                .setListener(new CreateTaskCompleteListenerImpl())
                .execute());
        }
    }

    private class GetTasksListenerImpl implements AsyncDataTaskListener<List<Task>> {

        @Override
        public void onSuccess(List<Task> tasks) {
            if (getView() != null && taskAdapter != null) {
                showEmptyView(tasks.isEmpty());
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

    private class CreateTaskCompleteListenerImpl implements AsyncDataTaskListener<Integer> {

        @Override
        public void onSuccess(final Integer taskCompleteId) {
            if (getView() != null) {
                Snackbar.make(getView(), R.string.task_completed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            asyncTaskDelegate.registerAsyncDataTask(taskCompleteDao.delete(taskCompleteId)
                                .setListener(new UndoCompleteListenerImpl())
                                .execute());
                        }
                    })
                    .show();
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

    private class UndoCompleteListenerImpl implements AsyncDataTaskListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            if (getView() != null) {
                fetchData();
                Snackbar.make(getView(), R.string.task_undo_success, Snackbar.LENGTH_SHORT).show();
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
