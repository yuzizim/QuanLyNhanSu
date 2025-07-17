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

import java.util.HashMap;
import java.util.Map;

public class EditUserDialog extends Dialog {
    private final User user;
    private final OnUserUpdateListener listener;
    private EditText etUsername, etEmail, etPassword;
    private Spinner spRole, spIsActive;
    private Button btnUpdate, btnCancel;

    public interface OnUserUpdateListener {
        void onUserUpdate(int id, Map<String, Object> updateFields);
    }

    public EditUserDialog(@NonNull Context context, User user, OnUserUpdateListener listener) {
        super(context);
        this.user = user;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_user);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spRole = findViewById(R.id.spRole);
        spIsActive = findViewById(R.id.spIsActive);
        btnUpdate = findViewById(R.id.btnUpdate);
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
        spIsActive.setAdapter(isActiveAdapter);

        // Populate fields
        etUsername.setText(user.getUsername());
        etEmail.setText(user.getEmail());
        spRole.setSelection(getRolePosition(user.getRole()));
        spIsActive.setSelection(user.isActive() ? 0 : 1);

        // Button listeners
        btnUpdate.setOnClickListener(v -> {
            Map<String, Object> updateFields = new HashMap<>();

            String username = etUsername.getText().toString().trim();
            if (!username.isEmpty() && !username.equals(user.getUsername())) {
                updateFields.put("username", username);
            }

            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty() && !email.equals(user.getEmail())) {
                updateFields.put("email", email);
            }

//            String password = etPassword.getText().toString().trim();
//            if (!password.isEmpty()) {
//                updateFields.put("password", password);
//            }

            String password = etPassword.getText().toString().trim();
            if (!password.isEmpty()) {
                String passwordError =  PasswordValidator.validatePassword(password);
                if (passwordError != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Invalid Password")
                            .setMessage(passwordError)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .setCancelable(true)
                            .show();
                    return;
                }
                updateFields.put("password", password);
            }

            String role = spRole.getSelectedItem().toString().toLowerCase();
            if (!role.equals(user.getRole())) {
                updateFields.put("role", role);
            }

            boolean isActive = spIsActive.getSelectedItemPosition() == 0;
            if (isActive != user.isActive()) {
                updateFields.put("is_active", isActive);
            }

            if (updateFields.isEmpty()) {
                Toast.makeText(getContext(), "No changes to update", Toast.LENGTH_SHORT).show();
                return;
            }

            listener.onUserUpdate(user.getId(), updateFields);
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

    private int getRolePosition(String role) {
        String[] roles = getContext().getResources().getStringArray(R.array.user_roles);
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equalsIgnoreCase(role)) {
                return i;
            }
        }
        return 0; // Default to first role
    }
}