package com.example.workforcemanagement.ui.forgotpassword;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.workforcemanagement.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {
    private ActivityResetPasswordBinding binding;
    private PasswordViewModel viewModel;

    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one number.";
        }
        if (!password.matches(".*[@$!%*?&].*")) {
            return "Password must contain at least one special character (@$!%*?&).";
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PasswordViewModel.class);

        // Xử lý nút back
        binding.btnBack.setOnClickListener(v -> finish());

        // Lấy email từ Intent
        String emailFromIntent = getIntent().getStringExtra("email");
        if (emailFromIntent != null && !emailFromIntent.isEmpty()) {
            binding.etEmail.setText(emailFromIntent);
        }

        binding.btnResetPassword.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String otp = binding.etOtp.getText().toString().trim();
            String newPassword = binding.etNewPassword.getText().toString();

            if (email.isEmpty()) {
                binding.etEmail.setError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.setError("Invalid email format");
                return;
            }

            if (otp.isEmpty()) {
                binding.etOtp.setError("OTP is required");
                return;
            }

            String passwordError = validatePassword(newPassword);
            if (passwordError != null) {
                binding.etNewPassword.setError(passwordError);
                return;
            }

            viewModel.resetPassword(email, otp, newPassword);
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getResetSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Password reset successful. Please login.", Toast.LENGTH_LONG).show();
                finish(); // Quay lại màn hình login
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
