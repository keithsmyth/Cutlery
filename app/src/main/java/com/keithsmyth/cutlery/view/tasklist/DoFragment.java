package com.keithsmyth.cutlery.view.tasklist;

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
import com.keithsmyth.cutlery.data.DataInjector;
import com.keithsmyth.cutlery.data.IconDao;
import com.keithsmyth.cutlery.data.TaskCompleteDao;
import com.keithsmyth.cutlery.data.TaskDao;
import com.keithsmyth.cutlery.data.UndoStack;
import com.keithsmyth.cutlery.model.TaskComplete;
import com.keithsmyth.cutlery.model.TaskListItem;
import com.keithsmyth.cutlery.view.AsyncTaskDelegate;
import com.keithsmyth.cutlery.view.DividerItemDecoration;
import com.keithsmyth.cutlery.view.Navigates;
import com.keithsmyth.cutlery.view.SwipeItemTouchHelperCallback;
import com.keithsmyth.cutlery.view.task.TaskFragment;

import java.util.Date;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static com.keithsmyth.cutlery.view.DividerItemDecoration.VERTICAL_LIST;

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
        final DataInjector injector = App.get(getContext()).inject();
        iconDao = injector.iconDao();
        taskDao = injector.taskDao();
        undoStack = injector.undoStack();
        taskCompleteDao = injector.taskCompleteDao();
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
        tasksRecycler.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL_LIST));
        taskAdapter = new TaskAdapter(iconDao);
        tasksRecycler.setAdapter(taskAdapter);

        final SwipeItemTouchHelperCallback swipeItemTouchHelperCallback = new SwipeItemTouchHelperCallback(taskAdapter);
        final ViewGroup viewGroup = (ViewGroup) view;
        final View swipeItemBackground = LayoutInflater.from(getContext()).inflate(R.layout.item_swipe_background, viewGroup, false);
        viewGroup.addView(swipeItemBackground, 0);
        swipeItemTouchHelperCallback.setSwipeBackground(swipeItemBackground);

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeItemTouchHelperCallback);
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

    private void fetchSingleItemData(int taskId) {
        asyncTaskDelegate.registerAsyncDataTask(taskDao.listSingle(taskId)
            .setListener(new GetSingleTaskListenerImpl())
            .execute());
    }

    private void processUndoStack() {
        final int taskId = undoStack.getDeleteTaskId();
        if (taskId == UndoStack.NO_TASK || getView() == null) { return; }

        undoStack.setDeleteTaskId(UndoStack.NO_TASK);
        Snackbar.make(getView(), R.string.task_deleted, LENGTH_LONG)
            .setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    asyncTaskDelegate.registerAsyncDataTask(taskDao.undoDelete(taskId)
                        .setListener(new UndoDeleteTaskCompleteListenerImpl())
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
        public void onTaskEdit(TaskListItem taskListItem) {
            if (navigates != null && getView() != null) {
                navigates.to(TaskFragment.editTask(taskListItem.task.id));
            }
        }

        @Override
        public void onCompleted(TaskListItem taskListItem) {
            if (getView() == null) { return; }
            final TaskComplete taskComplete = new TaskComplete(-1, new Date().getTime(), taskListItem.task.id);
            asyncTaskDelegate.registerAsyncDataTask(taskCompleteDao.create(taskComplete)
                .setListener(new CreateTaskCompleteListenerImpl(taskListItem.task.id))
                .execute());
        }

        @Override
        public void onUndoCompleteTask(TaskListItem taskListItem) {
            if (getView() == null) { return; }
            final int taskCompleteId = undoStack.getLatestTaskCompeteId(taskListItem.task.id);
            if (taskCompleteId == UndoStack.NO_TASK) { return; }
            asyncTaskDelegate.registerAsyncDataTask(taskCompleteDao.delete(taskCompleteId)
                .setListener(new UndoCompleteTaskCompleteListenerImpl(taskListItem.task.id))
                .execute());
        }
    }

    private class GetTasksListenerImpl implements AsyncDataTaskListener<List<TaskListItem>> {

        @Override
        public void onSuccess(List<TaskListItem> tasks) {
            if (getView() != null && taskAdapter != null) {
                undoStack.clearLatestTaskCompleteIds();
                showEmptyView(tasks.isEmpty());
                taskAdapter.setTasks(tasks);
            }
        }

        @Override
        public void onError(Exception e) {
            if (getView() != null) {
                Snackbar.make(getView(), e.getMessage(), LENGTH_LONG).show();
            }
        }
    }

    private class GetSingleTaskListenerImpl implements AsyncDataTaskListener<TaskListItem> {

        @Override
        public void onSuccess(TaskListItem model) {
            if (getView() != null && model != null && taskAdapter != null) {
                taskAdapter.setSingleTask(model);
            }
        }

        @Override
        public void onError(Exception e) {
            if (getView() != null) {
                Snackbar.make(getView(), e.getMessage(), LENGTH_LONG).show();
            }
        }
    }

    private class CreateTaskCompleteListenerImpl implements AsyncDataTaskListener<Integer> {

        private final int taskId;

        private CreateTaskCompleteListenerImpl(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void onSuccess(Integer taskCompleteId) {
            undoStack.setLatestTaskCompleteId(taskId, taskCompleteId);
        }

        @Override
        public void onError(Exception e) {
            if (getView() != null) {
                Snackbar.make(getView(), e.getMessage(), LENGTH_LONG).show();
            }
        }
    }

    private class UndoDeleteTaskCompleteListenerImpl implements AsyncDataTaskListener<Void> {

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
                Snackbar.make(getView(), e.getMessage(), LENGTH_LONG).show();
            }
        }
    }

    private class UndoCompleteTaskCompleteListenerImpl implements AsyncDataTaskListener<Void> {

        private final int taskId;

        public UndoCompleteTaskCompleteListenerImpl(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void onSuccess(Void aVoid) {
            if (getView() != null) {
                fetchSingleItemData(taskId);
                Snackbar.make(getView(), R.string.task_undo_success, Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(Exception e) {
            if (getView() != null) {
                Snackbar.make(getView(), e.getMessage(), LENGTH_LONG).show();
            }
        }
    }
}
