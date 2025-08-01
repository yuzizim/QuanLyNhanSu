package com.example.workforcemanagement.ui.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.util.PasswordValidator;

public class CreateUserDialog extends Dialog {
    private final OnUserCreateListener listener;
    private EditText etUsername, etEmail, etPassword;
    private Spinner spRole;
    private Button btnCreate, btnCancel;

    public interface OnUserCreateListener {
        void onUserCreate(User user);
    }

    public CreateUserDialog(@NonNull Context context, OnUserCreateListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_user);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spRole = findViewById(R.id.spRole);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);

        // Setup spinners
        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.user_roles,
                android.R.layout.simple_spinner_item
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        ArrayAdapter<CharSequence> isActiveAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.user_active_states,
                android.R.layout.simple_spinner_item
        );
        isActiveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spIsActive.setAdapter(isActiveAdapter);

        // Button listeners
        btnCreate.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = spRole.getSelectedItem().toString().toLowerCase();
            //boolean isActive = spIsActive.getSelectedItemPosition() == 0;

            // Basic validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Password validation
            String passwordError = PasswordValidator.validatePassword(password);
            if (passwordError != null) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Invalid Password")
                        .setMessage(passwordError)
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);

            listener.onUserCreate(user);
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Set dialog width to 90% of screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}