package com.example.workforcemanagement.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.UpdateProfileResponse;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.util.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditProfile extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail;
    private Button btnSave;
    private ImageButton btnBack;
    private User user;
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_profile);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Initialize services
        tokenManager = new TokenManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);

        // Get user from intent
        user = (User) getIntent().getSerializableExtra("user");
        if (user == null || !user.getProfileType().equals("admin")) {
            Toast.makeText(this, "Error: Invalid admin profile", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Populate fields
        etUsername.setText(user.getUsername());
        etEmail.setText(user.getEmail());

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Save button
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please provide at least one field", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare update data
        Map<String, Object> updateFields = new HashMap<>();
        if (!TextUtils.isEmpty(username)) updateFields.put("username", username);
        if (!TextUtils.isEmpty(email)) updateFields.put("email", email);

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "No authentication token found", Toast.LENGTH_SHORT).show();
            return;
        }

        // API call
        apiService.updateProfile("Bearer " + token, updateFields).enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminEditProfile.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    // Update local user object
                    if (!TextUtils.isEmpty(username)) user.setUsername(username);
                    if (!TextUtils.isEmpty(email)) user.setEmail(email);
                    finish();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "Failed to update profile";
                    Toast.makeText(AdminEditProfile.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                Toast.makeText(AdminEditProfile.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}