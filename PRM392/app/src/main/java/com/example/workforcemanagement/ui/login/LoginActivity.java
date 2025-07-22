package com.example.workforcemanagement.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.Department;
import com.example.workforcemanagement.data.model.LoginResponse;
import com.example.workforcemanagement.data.model.ManagerDashboardResponse;
import com.example.workforcemanagement.data.model.ManagerDashboardStats;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.databinding.ActivityLoginBinding;
import com.example.workforcemanagement.ui.forgotpassword.ForgotPasswordActivity;
import com.example.workforcemanagement.ui.main.AdminDashboardActivity;
import com.example.workforcemanagement.ui.main.EmployeeDashboardActivity;
import com.example.workforcemanagement.ui.main.HRDashboardActivity;
import com.example.workforcemanagement.ui.main.MainActivity;
import com.example.workforcemanagement.ui.main.ManagerDashboardActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                String idToken = account.getIdToken();
                                String email = account.getEmail();
                                loginViewModel.googleLogin(idToken);
                            }
                        } catch (ApiException e) {
                            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            loginViewModel.setLoading(false);
                        }
                    } else {
                        Toast.makeText(this, "Google Sign-In cancelled", Toast.LENGTH_LONG).show();
                        loginViewModel.setLoading(false);
                    }
                });

        setupObservers();
        setupClickListeners();
        restoreSavedCredentials();
    }

    private void setupObservers() {
        loginViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        loginViewModel.getGoogleLoginResponse().observe(this, response -> {
            loginViewModel.setLoading(false);
            if (response != null) {
                Log.d("GoogleLogin", "Response: success=" + response.isSuccess() + ", message=" + response.getMessage());
                if (response.isSuccess()) {
                    handleSuccessfulLogin(response);
                } else {
                    if (response.getMessage() != null && response.getMessage().contains("not registered")) {
                        Toast.makeText(this, "This email is not registered. Please sign up.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Google login failed", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Log.e("GoogleLogin", "Response is null");
                Toast.makeText(this, "Network error, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        binding.cvGoogleLogin.setOnClickListener(v -> signInWithGoogle());
        binding.cvFacebookLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Facebook login feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void restoreSavedCredentials() {
        if (loginViewModel.isRememberMeEnabled()) {
            String savedEmail = loginViewModel.getSavedEmail();
            binding.etEmail.setText(savedEmail);
            binding.cbRememberMe.setChecked(true);
        }
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        boolean rememberMe = binding.cbRememberMe.isChecked();

        if (!loginViewModel.validateInputs(email, password)) {
            showValidationErrors(email, password);
            return;
        }

        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        loginViewModel.login(email, password, rememberMe).observe(this, response -> {
            loginViewModel.setLoading(false);
            if (response != null) {
                if (response.isSuccess()) {
                    handleSuccessfulLogin(response);
                } else {
                    binding.tilEmail.setError("Invalid email or password");
                    binding.tilPassword.setError("Invalid email or password");
                }
            } else {
                Toast.makeText(this, "Network error, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void showValidationErrors(String email, String password) {
        if (email.isEmpty()) {
            binding.tilEmail.setError("Email is required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Invalid email format");
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Password is required");
        } else if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
        } else {
            binding.tilPassword.setError(null);
        }
    }

    // After login, call dashboard API if dep_manager. Otherwise, go to dashboard directly.
    private void handleSuccessfulLogin(LoginResponse response) {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

        User user = response.getData();
        String token = response.getToken();
        if (user != null && user.getRole() != null) {
            Log.d("Profile", "Profile fetched: " + user.getUsername());
            switch (user.getRole()) {
                case "admin":
                    startDashboard(AdminDashboardActivity.class, user);
                    break;
                case "hr":
                    startDashboard(HRDashboardActivity.class, user);
                    break;
                case "dep_manager":
                    fetchDepManagerDashboard(token);
                    break;
                case "employee":
                    startDashboard(EmployeeDashboardActivity.class, user);
                    break;
                default:
                    startDashboard(MainActivity.class, user);
                    break;
            }
        } else {
            Toast.makeText(this, "Failed to fetch profile after login", Toast.LENGTH_LONG).show();
            startDashboard(MainActivity.class, null);
        }
    }

    // Call dashboard API for dep_manager to get user and stats, then start dashboard
    private void fetchDepManagerDashboard(String token) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ManagerDashboardResponse> call = apiService.getDepManagerDashboard("Bearer " + token);
        call.enqueue(new Callback<ManagerDashboardResponse>() {
            @Override
            public void onResponse(Call<ManagerDashboardResponse> call, Response<ManagerDashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ManagerDashboardResponse dashboardResponse = response.body();
                    User user = dashboardResponse.getUser();
                    ManagerDashboardStats stats = dashboardResponse.getStats();
                    Department department = (user != null) ? user.getDepartment() : null;

                    Intent intent = new Intent(LoginActivity.this, ManagerDashboardActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("managerStats", stats);
                    intent.putExtra("department", department);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to load manager dashboard!", Toast.LENGTH_LONG).show();
                    startDashboard(MainActivity.class, null);
                }
            }

            @Override
            public void onFailure(Call<ManagerDashboardResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                startDashboard(MainActivity.class, null);
            }
        });
    }

    // Start other dashboards: admin, hr, employee, main
    private void startDashboard(Class<?> dashboardActivity, User user) {
        Intent intent = new Intent(this, dashboardActivity);
        if (user != null) intent.putExtra("user", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading() {
        binding.btnLogin.setEnabled(false);
        binding.btnLogin.setText("Logging in...");
    }

    private void hideLoading() {
        binding.btnLogin.setEnabled(true);
        binding.btnLogin.setText(R.string.login);
    }
}