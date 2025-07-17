package com.example.workforcemanagement.ui.department;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {

    private List<Department> departments = new ArrayList<>();
    private final OnDepartmentActionListener listener;

    public interface OnDepartmentActionListener {
        void onEditDepartment(Department department);
        void onDeleteDepartment(Department department); // Use Department instead of int
    }

    public DepartmentAdapter(OnDepartmentActionListener listener) {
        this.listener = listener;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_department, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        Department department = departments.get(position);
        holder.tvDepartmentName.setText(department.getName());
        holder.tvDepartmentCode.setText("Code: " + department.getCode());
        holder.tvDescription.setText("Description: " + (department.getDescription() != null ? department.getDescription() : "N/A"));
        holder.tvManager.setText("Manager: " + department.getManagerFullName());

        holder.btnEdit.setOnClickListener(v -> listener.onEditDepartment(department));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteDepartment(department));
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDepartmentName, tvDepartmentCode, tvDescription, tvManager;
        ImageView btnEdit, btnDelete;

        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepartmentName = itemView.findViewById(R.id.tvDepartmentName);
            tvDepartmentCode = itemView.findViewById(R.id.tvDepartmentCode);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvManager = itemView.findViewById(R.id.tvManager);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}