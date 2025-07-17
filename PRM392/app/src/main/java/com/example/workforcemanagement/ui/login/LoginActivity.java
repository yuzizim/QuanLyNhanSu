//// LoginActivity.java
package com.example.workforcemanagement.ui.login;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.LoginResponse;
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

        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize ActivityResultLauncher for Google Sign-In
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
                                //Log.d("GoogleSignIn", "Email: " + email);
                                //Log.d("GoogleSignIn", "ID Token: " + idToken);
                                loginViewModel.googleLogin(idToken);
                            }
                        } catch (ApiException e) {
                            //Log.e("GoogleSignIn", "ApiException: " + e.getMessage(), e);
                            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            loginViewModel.setLoading(false);
                        }
                    } else {
                        //Log.d("GoogleSignIn", "Sign-In cancelled or failed, resultCode: " + result.getResultCode());
                        Toast.makeText(this, "Google Sign-In cancelled", Toast.LENGTH_LONG).show();
                        loginViewModel.setLoading(false);
                    }
                });

        // Set up observers
        setupObservers();

        // Set up click listeners
        setupClickListeners();

        // Restore saved email and remember me state if applicable
        restoreSavedCredentials();
    }

    private void setupObservers() {
        // Observe loading state
        loginViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        // Observe Google login response
//        loginViewModel.getGoogleLoginResponse().observe(this, response -> {
//            loginViewModel.setLoading(false);
//            if (response != null) {
//                if (response.isSuccess()) {
//                    handleSuccessfulLogin();
//                } else {
//                    Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Google login failed", Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(this, "Network error, please try again", Toast.LENGTH_LONG).show();
//            }
//        });

        loginViewModel.getGoogleLoginResponse().observe(this, response -> {
            loginViewModel.setLoading(false);
            if (response != null) {
                Log.d("GoogleLogin", "Response: success=" + response.isSuccess() + ", message=" + response.getMessage());
                if (response.isSuccess()) {
                    //handleSuccessfulLogin();
                    handleSuccessfulLogin(response);
                } else {
                    // Display specific Toast for unregistered email
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
        // Login button click listener
        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        // Forgot password click listener
        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Google login click listener
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
        // Get input values
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        boolean rememberMe = binding.cbRememberMe.isChecked();

        // Validate inputs
        if (!loginViewModel.validateInputs(email, password)) {
            showValidationErrors(email, password);
            return;
        }

        // Clear previous errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        // Perform login
        loginViewModel.login(email, password, rememberMe).observe(this, response -> {
            // Always reset loading state
            loginViewModel.setLoading(false);

            if (response != null) {
                if (response.isSuccess()) {
                    //handleSuccessfulLogin();
                    handleSuccessfulLogin(response);
                } else {
                    // Show specific error on email and password fields
                    binding.tilEmail.setError("Invalid email or password");
                    binding.tilPassword.setError("Invalid email or password");
                }
            } else {
                // Handle null response (e.g., network error)
                Toast.makeText(this, "Network error, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInWithGoogle() {
        Log.d("GoogleSignIn", "Starting Google Sign-In");
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

//    private void handleSuccessfulLogin() {
//        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
//
//        // Navigate to main activity
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }

    // Updated handleSuccessfulLogin method trong LoginActivity
    private void handleSuccessfulLogin(LoginResponse response) {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

        loginViewModel.getProfile().observe(this, profileResponse -> {
            loginViewModel.setLoading(false);
            Intent intent;
            User user = profileResponse.getData();

            if (profileResponse.isSuccess() && user != null) {
                Log.d("Profile", "Profile fetched: " + user.getUsername());
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
                intent.putExtra("user", user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to fetch profile: " + profileResponse.getMessage(), Toast.LENGTH_LONG).show();
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
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
