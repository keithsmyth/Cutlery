package com.keithsmyth.cutlery.view.tasklist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keithsmyth.cutlery.R;
import com.keithsmyth.cutlery.data.IconDao;
import com.keithsmyth.cutlery.model.Task;
import com.keithsmyth.cutlery.model.TaskListItem;
import com.keithsmyth.cutlery.view.SwipeItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>
    implements SwipeItemTouchHelperCallback.SwipeListener {

    // TODO: Group by Overdue, Due, This week etc

    private final IconDao iconDao;
    private final List<TaskListItem> tasks;
    private final Set<TaskListItem> completedTasks;

    @Nullable private TaskActionListener taskActionListener;

    public TaskAdapter(IconDao iconDao) {
        this.iconDao = iconDao;
        tasks = new ArrayList<>();
        completedTasks = new HashSet<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TaskListItem taskListItem = tasks.get(position);
        final Task task = taskListItem.task;

        final boolean isCompleted = completedTasks.contains(taskListItem);

        holder.itemView.setBackgroundResource(isCompleted ? R.color.primary : android.R.color.white);

        holder.iconImage.setImageResource(isCompleted ? R.drawable.ic_done_black_24dp : iconDao.get(task.iconId).resId);
        holder.iconImage.setColorFilter(isCompleted ? Color.WHITE : task.colour);

        holder.nameText.setText(isCompleted ? holder.context.getString(R.string.task_completed) : task.name);
        @ColorInt final int textColour = isCompleted ? Color.WHITE : ContextCompat.getColor(holder.context, R.color.text_gray);
        holder.nameText.setTextColor(textColour);

        final String dueText;
        if (isCompleted) {
            dueText = holder.context.getString(R.string.undo);
        } else if (taskListItem.daysOverDue == 0) {
            dueText = holder.context.getString(R.string.task_due);
        } else if (taskListItem.daysOverDue < 0) {
            final int daysOverDue = Math.abs(taskListItem.daysOverDue);
            dueText = holder.context.getResources().getQuantityString(R.plurals.task_due_soon, daysOverDue, daysOverDue);
        } else {
            dueText = holder.context.getResources().getQuantityString(R.plurals.task_due_overdue, taskListItem.daysOverDue, taskListItem.daysOverDue);
        }
        holder.dueText.setText(dueText);
        holder.dueText.setTextColor(textColour);
        holder.dueText.setAllCaps(isCompleted);

        holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskActionListener != null) {
                    if (isCompleted) {
                        taskActionListener.onUndoCompleteTask(taskListItem);
                    } else {
                        taskActionListener.onTaskEdit(taskListItem);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTaskActionListener(@Nullable TaskActionListener taskActionListener) {
        this.taskActionListener = taskActionListener;
    }

    public void setTasks(List<TaskListItem> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        completedTasks.clear();
        notifyDataSetChanged();
    }

    public void setSingleTask(TaskListItem taskListItem) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).task.id == taskListItem.task.id) {
                tasks.remove(i);
                tasks.add(i, taskListItem);
                notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    public void onItemSwiped(int position) {
        final TaskListItem taskListItem = tasks.get(position);
        completedTasks.add(taskListItem);
        notifyItemChanged(position);
        if (taskActionListener != null) {
            taskActionListener.onCompleted(taskListItem);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final ImageView iconImage;
        public final TextView nameText;
        public final TextView dueText;
        public final View clickView;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            iconImage = (ImageView) itemView.findViewById(R.id.icon_image);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            dueText = (TextView) itemView.findViewById(R.id.due_undo_text);
            clickView = itemView.findViewById(R.id.click_view);
        }
    }

    interface TaskActionListener {

        void onTaskEdit(TaskListItem taskListItem);

        void onCompleted(TaskListItem taskListItem);

        void onUndoCompleteTask(TaskListItem taskListItem);
    }
}
