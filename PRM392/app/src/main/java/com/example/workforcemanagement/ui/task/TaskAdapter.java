package com.example.workforcemanagement.ui.task;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private final OnEditClickListener editClickListener;
    private final OnDeleteClickListener deleteClickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

    public interface OnEditClickListener {
        void onEditClick(Task task);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener) {
        this.tasks = tasks;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvTaskDescription, tvAssignee, tvDueDate, tvProgress, tvEstimatedHours;
        Chip chipStatus, chipPriority;
        LinearProgressIndicator progressBar;
        ImageView ivAssigneeAvatar;
        MaterialButton btnEdit, btnDelete;
        View priorityIndicator;
        View estimatedHoursCard;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvAssignee = itemView.findViewById(R.id.tvAssignee);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            tvEstimatedHours = itemView.findViewById(R.id.tvEstimatedHours);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            chipPriority = itemView.findViewById(R.id.chipPriority);
            progressBar = itemView.findViewById(R.id.progressBar);
            ivAssigneeAvatar = itemView.findViewById(R.id.ivAssigneeAvatar);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            estimatedHoursCard = itemView.findViewById(R.id.estimatedHoursCard);
        }

        void bind(Task task) {
            tvTaskTitle.setText(task.getTitle());
            tvTaskDescription.setText(task.getDescription());
            tvTaskDescription.setVisibility(task.getDescription() != null ? View.VISIBLE : View.GONE);
            tvAssignee.setText(task.getAssigneeName() != null ? task.getAssigneeName() : "Unassigned");
            chipStatus.setText(task.getStatus().replace("_", " "));
            chipPriority.setText(task.getPriority());

            // Format due date
            if (task.getDeadline() != null) {
                try {
                    Date date = dateFormat.parse(task.getDeadline());
                    tvDueDate.setText("Due: " + displayFormat.format(date));
                } catch (ParseException e) {
                    tvDueDate.setText("Due: " + task.getDeadline());
                }
            } else {
                tvDueDate.setText("No due date");
            }

            // Progress
            progressBar.setProgress(task.getProgress());
            tvProgress.setText(task.getProgress() + "%");

            // Estimated Hours
            if (task.getEstimatedHours() != null) {
                tvEstimatedHours.setText(String.format(Locale.US, "%.1fh", task.getEstimatedHours()));
                estimatedHoursCard.setVisibility(View.VISIBLE);
            } else {
                estimatedHoursCard.setVisibility(View.GONE);
            }

            // Priority Indicator Color
            int colorRes;
            switch (task.getPriority().toLowerCase()) {
                case "urgent":
                    colorRes = R.color.priority_urgent;
                    break;
                case "high":
                    colorRes = R.color.priority_high;
                    break;
                case "medium":
                    colorRes = R.color.priority_medium;
                    break;
                default:
                    colorRes = R.color.priority_low;
            }
            priorityIndicator.setBackgroundResource(colorRes);

            // Click Listeners
            btnEdit.setOnClickListener(v -> editClickListener.onEditClick(task));
            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("OK", (dialog, which) -> deleteClickListener.onDeleteClick(task))
                        .setNegativeButton("Cancel", null)
                        .show();
            });
            itemView.setOnClickListener(v -> editClickListener.onEditClick(task));
        }
    }
}