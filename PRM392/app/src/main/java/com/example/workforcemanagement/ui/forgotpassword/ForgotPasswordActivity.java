package com.example.workforcemanagement.ui.forgotpassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.workforcemanagement.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private PasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PasswordViewModel.class);

        // Xử lý nút back
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnRequestReset.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                binding.etEmail.setError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.setError("Invalid email format");
                return;
            }

            viewModel.requestPasswordReset(email);
        });

        observeViewModel();
    }

//    private void observeViewModel() {
//        viewModel.getRequestResetSuccess().observe(this, success -> {
//            if (success != null && success) {
//                Toast.makeText(this, "Reset request sent. Check your email.", Toast.LENGTH_LONG).show();
//                finish(); // Quay lại màn hình login hoặc bạn có thể mở ResetPasswordActivity
//            }
//        });
//
//        viewModel.getErrorMessage().observe(this, error -> {
//            if (error != null) {
//                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void observeViewModel() {
        viewModel.getRequestResetSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Reset request sent. Check your email.", Toast.LENGTH_LONG).show();

                // Chuyển sang ResetPasswordActivity
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);

                // Có thể truyền email sang nếu cần
                intent.putExtra("email", binding.etEmail.getText().toString().trim());

                startActivity(intent);
                finish(); // Đóng ForgotPasswordActivity nếu không cần quay lại
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

}
