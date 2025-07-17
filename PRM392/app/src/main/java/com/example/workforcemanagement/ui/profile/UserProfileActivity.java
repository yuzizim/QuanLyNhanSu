package com.example.workforcemanagement.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.ui.login.LoginActivity;
import com.example.workforcemanagement.ui.main.AdminDashboardActivity;
import com.example.workforcemanagement.ui.main.EmployeeDashboardActivity;
import com.example.workforcemanagement.ui.main.HRDashboardActivity;
import com.example.workforcemanagement.ui.main.MainActivity;
import com.example.workforcemanagement.ui.main.ManagerDashboardActivity;
import com.example.workforcemanagement.util.TokenManager;

public class UserProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Lấy user từ Intent
        User user = (User) getIntent().getSerializableExtra("user");

        // Khởi tạo các view
        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvUserPosition = findViewById(R.id.tvUserPosition);
        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
        TextView tvUserPhone = findViewById(R.id.tvUserPhone);
        TextView tvUserDepartment = findViewById(R.id.tvUserDepartment);
        TextView tvUserRole = findViewById(R.id.tvUserRole);
        LinearLayout quickCheckIn = findViewById(R.id.quickCheckIn);
        LinearLayout ivLogout = findViewById(R.id.ivLogout);

        // Hiển thị thông tin user
        if (user != null && user.getProfileType().equals("employee")) {
            tvFullName.setText(user.getFullName() != null ? user.getFullName() : "N/A");
            tvUserPosition.setText(user.getPosition() != null ? user.getPosition() : "N/A");
            tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            tvUserPhone.setText(user.getPhone() != null ? user.getPhone() : "N/A");
            tvUserDepartment.setText(user.getDepartment() != null && user.getDepartment().getName() != null
                    ? user.getDepartment().getName() : "N/A");
            tvUserRole.setText(user.getRole() != null ? user.getRole() : "N/A");
        } else {
            tvFullName.setText("Unknown User");
            tvUserPosition.setText("N/A");
            tvUserEmail.setText("N/A");
            tvUserPhone.setText("N/A");
            tvUserDepartment.setText("N/A");
            tvUserRole.setText("N/A");
            Toast.makeText(this, "Error: User profile data not available", Toast.LENGTH_LONG).show();
        }

        // Xử lý nút Home
        quickCheckIn.setOnClickListener(v -> {
            Intent intent;
            if (user != null && user.getRole() != null) {
                switch (user.getRole()) {
                    case "admin":
                        intent = new Intent(this, AdminDashboardActivity.class);
                        break;
                    case "hr":
                        intent = new Intent(this, HRDashboardActivity.class);
                        break;
                    case "dep_manager":
                        intent = new Intent(this, ManagerDashboardActivity.class);
                        break;
                    case "employee":
                        intent = new Intent(this, EmployeeDashboardActivity.class);
                        break;
                    default:
                        intent = new Intent(this, MainActivity.class);
                        break;
                }
                // Truyền user object để dashboard sử dụng nếu cần
                intent.putExtra("user", user);
                startActivity(intent);
                finish(); // Kết thúc UserProfileActivity để không quay lại
            } else {
                Toast.makeText(this, "Error: User role not found", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, user.getProfileType().equals("admin") ? AdminEditProfile.class : EmployeeEditProfile.class);
            intent.putExtra("user", user);
            startActivity(intent);
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