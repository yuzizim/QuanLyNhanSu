package com.example.workforcemanagement.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList = new ArrayList<>();
    private List<Task> fullList = new ArrayList<>();
    private TaskActionListener listener;

    // Giao diện cho các hành động trên task
    public interface TaskActionListener {
        void onView(Task task);
        void onEdit(Task task);
        void onCancel(Task task);
    }

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
        this.fullList = new ArrayList<>(taskList);
    }

    public void setTaskActionListener(TaskActionListener listener) {
        this.listener = listener;
    }

    public void setFullList(List<Task> list) {
        fullList = new ArrayList<>(list);
    }

    public void filter(String query, String priority) {
        taskList.clear();
        for (Task t : fullList) {
            boolean matchQuery = query.isEmpty() || t.getTitle().toLowerCase().contains(query.toLowerCase());
            boolean matchPriority = priority.isEmpty() || priority.equals("Tất cả") || t.getPriority().equalsIgnoreCase(priority);
            if (matchQuery && matchPriority) {
                taskList.add(t);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tracking_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task t = taskList.get(position);
        holder.tvTaskTitle.setText(t.getTitle());
        holder.tvAssignee.setText(t.getAssigneeName() != null ? t.getAssigneeName() : "");
        holder.tvDeadline.setText("Hạn: " + (t.getDeadlineString() != null ? t.getDeadlineString() : ""));
        holder.tvPriority.setText("Ưu tiên: " + (t.getPriority() != null ? t.getPriority() : ""));

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(t);
        });
        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(t);
        });
        holder.btnView.setOnClickListener(v -> {
            if (listener != null) listener.onView(t);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvAssignee, tvDeadline, tvPriority;
        ImageView btnView, btnEdit, btnCancel;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvAssignee = itemView.findViewById(R.id.tvAssignee);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}