package com.example.workforcemanagement.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Department;
import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.ManagerDashboardStats;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.ui.profile.UserProfileActivity;

public class ManagerDashboardActivity extends AppCompatActivity {
    private TextView tvManagerName, tvDeptName, tvDeptEmployees, tvDeptPerformance;
    private ManagerDashboardStats stats;
    private User user;
    private Department department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        user = (User) getIntent().getSerializableExtra("user");
        stats = (ManagerDashboardStats) getIntent().getSerializableExtra("managerStats");
        department = (Department) getIntent().getSerializableExtra("department");

        tvManagerName = findViewById(R.id.tvManagerName);
        tvDeptName = findViewById(R.id.tvDeptName);
        tvDeptEmployees = findViewById(R.id.tvDeptEmployees);
        tvDeptPerformance = findViewById(R.id.tvDeptPerformance);

        if (user != null) {
            String name = user.getFullName() != null ? user.getFullName() : user.getUsername();
            tvManagerName.setText("Chào " + name);
        }

        if (department != null) {
            tvDeptName.setText(department.getName());
        } else if (user != null && user.getDepartment() != null) {
            tvDeptName.setText(user.getDepartment().getName());
        }

        if (stats != null) {
            tvDeptEmployees.setText(stats.getEmployeeCount() + "/" + stats.getEmployeeTotal());
            tvDeptPerformance.setText(stats.getDeptPerformance() + "%");
            // Set other stats similarly if needed
        }

        ImageView ivManagerAvatar = findViewById(R.id.ivManagerAvatar);
        ivManagerAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }
}