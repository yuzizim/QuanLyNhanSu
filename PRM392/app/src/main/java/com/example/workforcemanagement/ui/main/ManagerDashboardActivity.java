package com.example.workforcemanagement.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Department;
import com.example.workforcemanagement.data.model.ManagerDashboardStats;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.ui.profile.UserProfileActivity;

public class ManagerDashboardActivity extends AppCompatActivity {
    private TextView tvManagerName, tvDeptName, tvDeptEmployees, tvDeptPerformance;
    private TextView tvDeptTaskInProgress, tvDeptCompletedThisWeek, tvDeptTitle;
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
        tvDeptTaskInProgress = findViewById(R.id.tvDeptTaskInProgress);
        tvDeptCompletedThisWeek = findViewById(R.id.tvDeptCompletedThisWeek);
        tvDeptTitle = findViewById(R.id.tvDeptTitle);

        // Lấy tên phòng ban ưu tiên từ department, nếu không có lấy từ user
        String deptName = null;
        if (department != null && department.getName() != null && !department.getName().isEmpty()) {
            deptName = department.getName();
        } else if (user != null && user.getDepartment() != null && user.getDepartment().getName() != null) {
            deptName = user.getDepartment().getName();
        } else {
            deptName = "[Tên Phòng Ban]";
        }

        // Set tên manager và phòng ban
        String managerName = user != null && user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName()
                : (user != null ? user.getUsername() : "[Tên Quản Lý]");
        tvManagerName.setText("Chào " + managerName);
        tvDeptName.setText(deptName);
        tvDeptTitle.setText("📊 PHÒNG BAN: " + deptName);

        // Set dữ liệu phòng ban
        if (stats != null) {
            tvDeptEmployees.setText(stats.getEmployeeCount() + "/" + stats.getEmployeeTotal());
            tvDeptPerformance.setText(stats.getDeptPerformance() + "%");
            tvDeptTaskInProgress.setText("Công việc đang thực hiện: " + stats.getTasksInProgress());
            tvDeptCompletedThisWeek.setText("Hoàn thành tuần này: " + stats.getTasksCompletedThisWeek());
        } else {
            // Nếu không có dữ liệu thì để mặc định
            tvDeptEmployees.setText("0/0");
            tvDeptPerformance.setText("0%");
            tvDeptTaskInProgress.setText("Công việc đang thực hiện: 0");
            tvDeptCompletedThisWeek.setText("Hoàn thành tuần này: 0");
        }

        // Avatar click mở Profile
        ImageView ivManagerAvatar = findViewById(R.id.ivManagerAvatar);
        ivManagerAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        findViewById(R.id.cardTeam).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.workforcemanagement.ui.dep_manager.ActivityEmListDep.class);
            if (department != null) {
                intent.putExtra("department", department); // truyền phòng ban sang
            }
            startActivity(intent);
        });
    }
}