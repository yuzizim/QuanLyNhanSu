package com.example.workforcemanagement.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {
    private List<Employee> employeeList;
    private List<Employee> fullList;

    public EmployeeAdapter(List<Employee> employeeList) {
        this.employeeList = employeeList;
        this.fullList = new ArrayList<>(employeeList);
    }

    public void setFullList(List<Employee> list) {
        fullList = new ArrayList<>(list);
    }

    // Tìm kiếm
    public void filter(String query) {
        employeeList.clear();
        if (query.isEmpty()) {
            employeeList.addAll(fullList);
        } else {
            for (Employee emp : fullList) {
                if (emp.getFullName().toLowerCase().contains(query.toLowerCase())) {
                    employeeList.add(emp);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.tvEmployeeName.setText(employee.getFullName());
        holder.tvEmployeePosition.setText(employee.getPosition() != null ? employee.getPosition() : "Chưa cập nhật");
        holder.tvEmployeePerformance.setText("Hiệu suất: " + (employee.getStatus() != null && employee.getStatus().equals("active") ? "85%" : "0%"));
        // Avatar và trạng thái: có thể set hình động, trạng thái tùy theo mẫu (fix cứng hoặc lấy từ dữ liệu)
        // holder.ivEmployeeAvatar.setImageResource(R.drawable.ic_person);
        holder.ivEmployeeStatus.setImageResource(R.drawable.ic_task_done); // hoặc đổi icon theo status
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEmployeeAvatar, ivEmployeeStatus;
        TextView tvEmployeeName, tvEmployeePosition, tvEmployeePerformance;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEmployeeAvatar = itemView.findViewById(R.id.ivEmployeeAvatar);
            ivEmployeeStatus = itemView.findViewById(R.id.ivEmployeeStatus);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeePosition = itemView.findViewById(R.id.tvEmployeePosition);
            tvEmployeePerformance = itemView.findViewById(R.id.tvEmployeePerformance);
        }
    }
}