package com.keithsmyth.cutlery.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keithsmyth.cutlery.R;
import com.keithsmyth.cutlery.data.IconDao;
import com.keithsmyth.cutlery.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>
    implements SwipeItemTouchHelperCallback.SwipeListener {

    private final IconDao iconDao;
    private final List<Task> tasks;

    @Nullable private TaskActionListener taskActionListener;

    public TaskAdapter(IconDao iconDao) {
        this.iconDao = iconDao;
        tasks = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Task task = tasks.get(position);

        holder.iconImage.setImageResource(iconDao.get(task.iconId).resId);
        holder.iconImage.setColorFilter(task.colour);

        holder.nameText.setText(task.name);

        final String dueText;
        if (task.daysOverDue == 0) {
            dueText = holder.context.getString(R.string.task_due);
        } else if (task.daysOverDue < 0) {
            dueText = holder.context.getString(R.string.task_due_soon, Math.abs(task.daysOverDue));
        } else {
            dueText = holder.context.getString(R.string.task_due_overdue, task.daysOverDue);
        }
        holder.dueText.setText(dueText);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskActionListener != null) {
                    taskActionListener.onTaskEdit(task);
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

    public void setTasks(List<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }

    @Override
    public void onItemSwiped(int position) {
        final Task task = tasks.get(position);
        tasks.remove(position);
        notifyItemRemoved(position);
        if (taskActionListener != null) {
            taskActionListener.onCompleted(task);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final ImageView iconImage;
        public final TextView nameText;
        public final TextView dueText;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            iconImage = (ImageView) itemView.findViewById(R.id.icon_image);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            dueText = (TextView) itemView.findViewById(R.id.due_text);
        }
    }

    interface TaskActionListener {

        void onTaskEdit(Task task);

        void onCompleted(Task task);
    }
}
