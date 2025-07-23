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
        this.employeeList = new ArrayList<>(employeeList);
        this.fullList = new ArrayList<>(employeeList);
    }

    // Method để cập nhật danh sách nhân viên từ API
    public void updateEmployeeList(List<Employee> newList) {
        this.employeeList.clear();
        this.fullList.clear();

        this.employeeList.addAll(newList);
        this.fullList.addAll(newList);

        notifyDataSetChanged();
    }

    // Method để set lại fullList (giữ lại để tương thích)
    public void setFullList(List<Employee> list) {
        fullList.clear();
        fullList.addAll(list);
    }

    // Tìm kiếm trong danh sách hiện có
    public void filter(String query) {
        employeeList.clear();

        if (query == null || query.trim().isEmpty()) {
            // Nếu không có từ khóa search, hiển thị tất cả
            employeeList.addAll(fullList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();

            // Tìm kiếm theo tên nhân viên
            for (Employee emp : fullList) {
                if (emp.getFullName() != null &&
                        emp.getFullName().toLowerCase().contains(lowerCaseQuery)) {
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

        // Set tên nhân viên
        holder.tvEmployeeName.setText(employee.getFullName() != null ? employee.getFullName() : "Chưa cập nhật");

        // Set chức vụ
        holder.tvEmployeePosition.setText(employee.getPosition() != null ? employee.getPosition() : "Chưa cập nhật");

        // Set hiệu suất (có thể tùy chỉnh logic này)
        String performanceText = "Hiệu suất: ";
        if (employee.getStatus() != null && employee.getStatus().equals("active")) {
            performanceText += "85%";
        } else {
            performanceText += "0%";
        }
        holder.tvEmployeePerformance.setText(performanceText);

        // Set avatar và status icon (có thể tùy chỉnh)
        // holder.ivEmployeeAvatar.setImageResource(R.drawable.ic_person);
        holder.ivEmployeeStatus.setImageResource(R.drawable.ic_task_done);
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