package com.example.workforcemanagement.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.ui.profile.UserProfileActivity;

public class EmployeeDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        User user = (User) getIntent().getSerializableExtra("user");

        ImageView ivEmployeeAvatar = findViewById(R.id.ivEmployeeAvatar);
        ivEmployeeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }
}