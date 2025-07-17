package com.example.workforcemanagement.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.data.model.Stats;
import com.example.workforcemanagement.ui.admin.DepartmentManagementActivity;
import com.example.workforcemanagement.ui.admin.StatsViewModel;
import com.example.workforcemanagement.ui.admin.TaskManagementActivity;
import com.example.workforcemanagement.ui.admin.UserManagementActivity;
import com.example.workforcemanagement.ui.profile.AdminProfileActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private StatsViewModel statsViewModel;
    private TextView tvTotalEmployees, tvDeptCount, tvActiveJobs,
            tvAvgPerformance, tvAttendanceRate, tvTaskCompletionRate;
    private CardView tvEmployeeManagement, cardDepartmentManagement, cardTaskManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Get user data from intent
        User user = (User) getIntent().getSerializableExtra("user");

        // Initialize ViewModel
        statsViewModel = new ViewModelProvider(this).get(StatsViewModel.class);

        // Initialize views
        tvTotalEmployees = findViewById(R.id.tvTotalEmployees);
        tvDeptCount = findViewById(R.id.tvDeptCount);
        tvActiveJobs = findViewById(R.id.tvActiveJobs);
        tvAvgPerformance = findViewById(R.id.tvAvgPerformance);
        tvAttendanceRate = findViewById(R.id.tvAttendanceRate);
        tvTaskCompletionRate = findViewById(R.id.tvTaskCompletionRate);
        tvEmployeeManagement = findViewById(R.id.cardEmployeeManagement);
        cardDepartmentManagement = findViewById(R.id.cardDepartmentManagement);
        cardTaskManagement = findViewById(R.id.cardTaskManagement);

       // Set click listener for employee management card
        tvEmployeeManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserManagementActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        // Set click listener for department management card
        cardDepartmentManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, DepartmentManagementActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        // Set click listener for task management card
        cardTaskManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskManagementActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });



        // Set up avatar and profile click listeners
        ImageView ivAdminAvatar = findViewById(R.id.ivAdminAvatar);
        ImageView ivAdminProfile = findViewById(R.id.ivAdminProfile);

        View.OnClickListener profileClickListener = v -> {
            Intent intent = new Intent(this, AdminProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        };

        ivAdminAvatar.setOnClickListener(profileClickListener);
        ivAdminProfile.setOnClickListener(profileClickListener);

        // Observe stats data
        statsViewModel.getDashboardStats().observe(this, statsResponse -> {
            if (statsResponse != null && statsResponse.isSuccess()) {
                Stats stats = statsResponse.getData();
                updateStatsUI(stats);
            } else {
                Toast.makeText(this, statsResponse != null ?
                        statsResponse.getMessage() : "Failed to load stats", Toast.LENGTH_LONG).show();
            }
        });

        // Observe loading state
        statsViewModel.getIsLoading().observe(this, isLoading -> {
            // Update UI based on loading state if needed
        });
    }


    private void updateStatsUI(Stats stats) {
        tvTotalEmployees.setText(String.valueOf(stats.getTotalEmployees()));
        tvDeptCount.setText(String.valueOf(stats.getDepartmentCount()));
        tvActiveJobs.setText(String.valueOf(stats.getActiveJobs()));
        tvAvgPerformance.setText(stats.getAvgPerformance() + "%");
        tvAttendanceRate.setText(stats.getAttendanceRate() + "%");
        tvTaskCompletionRate.setText(stats.getTaskCompletionRate() + "%");
    }
}