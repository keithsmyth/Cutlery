package com.keithsmyth.cutlery.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.keithsmyth.cutlery.App;
import com.keithsmyth.cutlery.R;
import com.keithsmyth.cutlery.data.AsyncDataTaskListener;
import com.keithsmyth.cutlery.data.IconDao;
import com.keithsmyth.cutlery.data.TaskDao;
import com.keithsmyth.cutlery.data.UndoStack;
import com.keithsmyth.cutlery.model.Icon;
import com.keithsmyth.cutlery.model.Task;

public class TaskFragment extends Fragment {

    private static final String EXTRA_ICON_ID = "EXTRA_ICON_ID";
    private static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";

    private EditText nameEdit;
    private EditText frequencyValueEdit;
    private AppCompatSpinner frequencyDropdown;
    private BottomSheetBehavior bottomSheetBehavior;
    private View shadeView;
    private ImageButton iconButton;
    private Button saveButton;
    private Button deleteButton;
    @Nullable private AlertDialog confirmDeleteDialog;

    private Navigates navigates;
    private AsyncTaskDelegate asyncTaskDelegate;
    private IconDao iconDao;
    private TaskDao taskDao;
    private UndoStack undoStack;

    private int taskId;
    private int iconId;

    public static TaskFragment editTask(int taskId) {
        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TASK_ID, taskId);
        final TaskFragment fragment = new TaskFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigates = (Navigates) context;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        asyncTaskDelegate = new AsyncTaskDelegate();
        iconDao = App.inject().iconDao();
        taskDao = App.inject().taskDao();
        undoStack = App.inject().undoStack();
        taskId = getArguments() != null ? getArguments().getInt(EXTRA_TASK_ID, -1) : -1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameEdit = (EditText) view.findViewById(R.id.name_edit);
        frequencyValueEdit = (EditText) view.findViewById(R.id.frequency_value_edit);
        frequencyDropdown = (AppCompatSpinner) view.findViewById(R.id.frequency_dropdown);
        frequencyDropdown.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_frequency, R.id.frequency_text, Task.FREQUENCIES));

        final RecyclerView iconRecycler = (RecyclerView) view.findViewById(R.id.icon_recycler);
        iconRecycler.setHasFixedSize(true);
        iconRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        final IconAdapter iconAdapter = new IconAdapter();
        iconAdapter.setIconClickListener(new IconClickListenerImpl());
        iconRecycler.setAdapter(iconAdapter);
        iconAdapter.setIcons(iconDao.list());

        view.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeIconDrawer();
            }
        });

        shadeView = view.findViewById(R.id.shade_view);
        shadeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeIconDrawer();
            }
        });

        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetCallbackImpl());

        iconButton = (ImageButton) view.findViewById(R.id.icon_button);
        iconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIconDrawer();
            }
        });

        saveButton = (Button) view.findViewById(R.id.save_button);
        deleteButton = (Button) view.findViewById(R.id.delete_button);

        iconId = savedInstanceState != null ? savedInstanceState.getInt(EXTRA_ICON_ID, -1) : -1;
        updateIcon();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_ICON_ID, iconId);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (taskId > 0) {
            // edit mode
            fetchData();
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update();
                }
            });
            deleteButton.setText(R.string.create_delete);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete();
                }
            });
            navigates.setTitle(R.string.create_title_edit);
        } else {
            // create mode
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });
            deleteButton.setText(R.string.create_cancel);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
            navigates.setTitle(R.string.create_title_create);
        }
    }

    @Override
    public void onStop() {
        AndroidUtil.closeKeyboard(getView());
        asyncTaskDelegate.clearAsyncDataTasks();
        if (confirmDeleteDialog != null) {
            confirmDeleteDialog.dismiss();
        }
        super.onStop();
    }

    private void openIconDrawer() {
        if (getView() != null) {
            AndroidUtil.closeKeyboard(getView());
            shadeView.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void closeIconDrawer() {
        if (getView() != null) {
            shadeView.setVisibility(View.GONE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void updateIcon() {
        if (getView() != null) {
            iconButton.setImageResource(iconDao.get(iconId).resId);
            iconButton.setColorFilter(Icon.getDefaultColor(getContext()));
        }
    }

    private void fetchData() {
        asyncTaskDelegate.registerAsyncDataTask(taskDao.get(taskId)
            .setListener(new GetTaskListenerImpl())
            .execute());
    }

    private void populateData(Task task) {
        iconId = task.iconId;
        updateIcon();
        nameEdit.setText(task.name);
        frequencyValueEdit.setText(String.valueOf(task.frequencyValue));
        if (!TextUtils.isEmpty(task.frequency)) {
            final int position = Task.FREQUENCIES.indexOf(task.frequency);
            frequencyDropdown.setSelection(position, true);
        }
    }

    private void save() {
        if (validate()) {
            asyncTaskDelegate.registerAsyncDataTask(taskDao.create(createTaskFromUserInput())
                .setListener(new SaveTaskListenerImpl())
                .execute());
        } else {
            showError(getString(R.string.create_error_invalid));
        }
    }

    private void update() {
        if (validate()) {
            asyncTaskDelegate.registerAsyncDataTask(taskDao.update(createTaskFromUserInput())
                .setListener(new SaveTaskListenerImpl())
                .execute());
        } else {
            showError(getString(R.string.create_error_invalid));
        }
    }

    private void delete() {
        confirmDeleteDialog = new AlertDialog.Builder(getContext())
            .setTitle(R.string.create_confirm_delete_title)
            .setMessage(R.string.create_confirm_delete_msg)
            .setPositiveButton(R.string.create_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    asyncTaskDelegate.registerAsyncDataTask(taskDao.delete(taskId)
                        .setListener(new DeleteTaskListenerImpl(taskId))
                        .execute());
                }
            })
            .setNegativeButton(R.string.create_cancel, null)
            .create();
        confirmDeleteDialog.show();
    }

    private void cancel() {
        if (navigates != null) {
            navigates.back();
        }
    }

    private boolean validate() {
        final String frequency = frequencyDropdown.getSelectedItem().toString();
        boolean isValid = true;
        if (iconId == -1) {
            isValid = false;
        }
        if (TextUtils.isEmpty(nameEdit.getText().toString())) {
            nameEdit.setError(getString(R.string.create_error_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(frequencyValueEdit.getText().toString())) {
            frequencyValueEdit.setError(getString(R.string.create_error_required));
            isValid = false;
        } else if (!TextUtils.isDigitsOnly(frequencyValueEdit.getText().toString())) {
            frequencyValueEdit.setError(getString(R.string.create_error_numeric));
            isValid = false;
        }
        if (TextUtils.isEmpty(frequency)) {
            isValid = false;
        }
        return isValid;
    }

    private Task createTaskFromUserInput() {
        return new Task(taskId,
            nameEdit.getText().toString(),
            frequencyDropdown.getSelectedItem().toString(),
            Integer.parseInt(frequencyValueEdit.getText().toString()),
            iconId,
            Icon.getDefaultColor(getContext()),
            false,
            0,
            0);
    }

    private void showError(String error) {
        if (getView() != null) {
            Snackbar.make(getView(), error, Snackbar.LENGTH_LONG).show();
        }
    }

    private class BottomSheetCallbackImpl extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (getView() == null) {
                return;
            }
            switch (newState) {
                case BottomSheetBehavior.STATE_EXPANDED:
                    shadeView.setVisibility(View.VISIBLE);
                    break;
                case BottomSheetBehavior.STATE_HIDDEN:
                case BottomSheetBehavior.STATE_COLLAPSED:
                    shadeView.setVisibility(View.GONE);
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // no op
        }
    }

    private class IconClickListenerImpl implements IconAdapter.IconClickListener {

        @Override
        public void onIconClicked(Icon icon) {
            if (getView() != null) {
                iconId = icon.id;
                updateIcon();
                closeIconDrawer();
            }
        }
    }

    private class GetTaskListenerImpl implements AsyncDataTaskListener<Task> {

        @Override
        public void onSuccess(Task task) {
            if (getView() != null) {
                populateData(task);
            }
        }

        @Override
        public void onError(Exception e) {
            showError(e.getMessage());
        }
    }

    private class SaveTaskListenerImpl implements AsyncDataTaskListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            if (getView() != null && navigates != null) {
                navigates.back();
            }
        }

        @Override
        public void onError(Exception e) {
            showError(e.getMessage());
        }
    }

    private class DeleteTaskListenerImpl implements AsyncDataTaskListener<Void> {

        private final int taskId;

        private DeleteTaskListenerImpl(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void onSuccess(Void aVoid) {
            if (getView() != null && navigates != null) {
                undoStack.setDeleteTask(taskId);
                navigates.back();
            }
        }

        @Override
        public void onError(Exception e) {
            showError(e.getMessage());
        }
    }
}
