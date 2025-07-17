package com.example.workforcemanagement.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.ui.login.LoginActivity;
import com.example.workforcemanagement.ui.main.AdminDashboardActivity;
import com.example.workforcemanagement.util.TokenManager;

public class AdminProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Lấy user từ Intent
        User user = (User) getIntent().getSerializableExtra("user");

        // Khởi tạo các view
        TextView tvAdminUsername = findViewById(R.id.tvAdminUsername);
        TextView tvAdminRole = findViewById(R.id.tvAdminRole);
        TextView tvAdminEmail = findViewById(R.id.tvAdminEmail);
        TextView tvAdminRoleDetail = findViewById(R.id.tvAdminRoleDetail);
        LinearLayout quickCheckIn = findViewById(R.id.quickCheckIn);
        LinearLayout ivLogout = findViewById(R.id.ivLogout);

        // Hiển thị thông tin user
        if (user != null && user.getProfileType().equals("admin")) {
            tvAdminUsername.setText(user.getUsername() != null ? user.getUsername() : "N/A");
            tvAdminRole.setText(getString(R.string.admin_role));
            tvAdminEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            tvAdminRoleDetail.setText(user.getRole() != null ? user.getRole() : "Admin");
        } else {
            tvAdminUsername.setText("Unknown User");
            tvAdminRole.setText(getString(R.string.admin_role));
            tvAdminEmail.setText("N/A");
            tvAdminRoleDetail.setText("Admin");
            Toast.makeText(this, "Error: Admin profile data not available", Toast.LENGTH_LONG).show();
        }

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, user.getProfileType().equals("admin") ? AdminEditProfile.class : EmployeeEditProfile.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        // Xử lý nút quay về dashboard
        quickCheckIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        // Xử lý logout
        ivLogout.setOnClickListener(v -> {
            TokenManager tokenManager = new TokenManager(this);
            tokenManager.clearToken();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
