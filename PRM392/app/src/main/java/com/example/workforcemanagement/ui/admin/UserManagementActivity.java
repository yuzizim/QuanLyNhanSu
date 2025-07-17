package com.example.workforcemanagement.ui.admin;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.data.model.UserStats;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Map;

public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener, EditUserDialog.OnUserUpdateListener {
    private UserViewModel viewModel;
    private UserAdapter adapter;
    private RecyclerView rvUsers;
    private LinearLayout layoutEmptyState;
    private TextView tvTotalUsers, tvActiveUsers, tvInactiveUsers, tvNewUsers;
    private EditText etSearch;
    private TextView tabAll, tabActive, tabInactive, tabAdmin;
    private ImageView btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize views
        rvUsers = findViewById(R.id.rvUsers);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvInactiveUsers = findViewById(R.id.tvInactiveUsers);
        tvNewUsers = findViewById(R.id.tvNewUsers);
        etSearch = findViewById(R.id.etSearch);
        tabAll = findViewById(R.id.tabAll);
        tabActive = findViewById(R.id.tabActive);
        tabInactive = findViewById(R.id.tabInactive);
        tabAdmin = findViewById(R.id.tabAdmin);
        btnFilter = findViewById(R.id.btnFilter);

        // Setup RecyclerView
        adapter = new UserAdapter(this, this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        // Setup search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
                loadUsers();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup filter tabs
        tabAll.setOnClickListener(v -> setFilter("all"));
        tabActive.setOnClickListener(v -> setFilter("active"));
        tabInactive.setOnClickListener(v -> setFilter("inactive"));
        tabAdmin.setOnClickListener(v -> setFilter("admin"));

        // Setup buttons
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddUser).setOnClickListener(v -> {
            CreateUserDialog dialog = new CreateUserDialog(this, user -> {
                viewModel.createUser(user).observe(this, success -> {
                    if (success) {
                        Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            dialog.show();
        });
//        findViewById(R.id.btnFilter).setOnClickListener(v -> {
//            Toast.makeText(this, "Filter clicked", Toast.LENGTH_SHORT).show();
//        });

        // Setup filter button to show role selection dialog
        btnFilter.setOnClickListener(v -> {
            // Get user_roles array from strings.xml
            String[] roles = getResources().getStringArray(R.array.user_roles);
            new AlertDialog.Builder(this)
                    .setTitle("Filter by Role")
                    .setItems(roles, (dialog, which) -> {
                        String selectedRole = roles[which].toLowerCase();
                        setFilter(selectedRole);
                        Toast.makeText(this, "Filtering by " + roles[which], Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        });


//        FloatingActionButton fabQuickActions = findViewById(R.id.fabQuickActions);
//        fabQuickActions.setOnClickListener(v -> {
//            Toast.makeText(this, "Quick actions clicked", Toast.LENGTH_SHORT).show();
//        });

        // Observe users data
        loadUsers();

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Show/hide loading indicator if needed
        });
    }

    private void setFilter(String filter) {
        viewModel.setFilter(filter);
        updateTabUI(filter);
        loadUsers();
    }

    private void updateTabUI(String selectedFilter) {
        tabAll.setBackgroundResource(selectedFilter.equals("all") ?
                R.drawable.tab_selected_background : R.drawable.tab_unselected_background);
        tabActive.setBackgroundResource(selectedFilter.equals("active") ?
                R.drawable.tab_selected_background : R.drawable.tab_unselected_background);
        tabInactive.setBackgroundResource(selectedFilter.equals("inactive") ?
                R.drawable.tab_selected_background : R.drawable.tab_unselected_background);
        tabAdmin.setBackgroundResource(selectedFilter.equals("admin") ?
                R.drawable.tab_selected_background : R.drawable.tab_unselected_background);

        tabAll.setTextColor(getResources().getColor(selectedFilter.equals("all") ?
                R.color.indigo_500 : R.color.slate_500));
        tabActive.setTextColor(getResources().getColor(selectedFilter.equals("active") ?
                R.color.indigo_500 : R.color.slate_500));
        tabInactive.setTextColor(getResources().getColor(selectedFilter.equals("inactive") ?
                R.color.indigo_500 : R.color.slate_500));
        tabAdmin.setTextColor(getResources().getColor(selectedFilter.equals("admin") ?
                R.color.indigo_500 : R.color.slate_500));
    }

    private void loadUsers() {
        viewModel.getUsers(1, 10).observe(this, response -> {
            if (response != null && response.isSuccess() && response.getData() != null) {
                adapter.setUsers(response.getData().getUsers());
                updateStats(response.getData().getStats());

                layoutEmptyState.setVisibility(
                        response.getData().getUsers().isEmpty() ?
                                View.VISIBLE : View.GONE
                );
                rvUsers.setVisibility(
                        response.getData().getUsers().isEmpty() ?
                                View.GONE : View.VISIBLE
                );
            } else {
                Toast.makeText(this,
                        response != null ? response.getMessage() : "Failed to load users",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateStats(UserStats stats) {
        tvTotalUsers.setText(String.valueOf(stats.getTotalUsers()));
        tvActiveUsers.setText(String.valueOf(stats.getActiveUsers()));
        tvInactiveUsers.setText(String.valueOf(stats.getInactiveUsers()));
        tvNewUsers.setText(String.valueOf(stats.getNewUsers()));
    }

    @Override
    public void onEditUser(User user) {
        EditUserDialog dialog = new EditUserDialog(this, user, this);
        dialog.show();
    }

    @Override
    public void onDeleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to deactivate the account for " + user.getUsername() + "?")
                .setPositiveButton("OK", (dialog, which) -> {
                    viewModel.deleteUser(user.getId()).observe(this, success -> {
                        if (success) {
                            Toast.makeText(this, "User deactivated", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        } else {
                            Toast.makeText(this, "Failed to deactivate user", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    @Override
    public void onUserUpdate(int id, Map<String, Object> updateFields) {
        viewModel.updateUser(id, updateFields).observe(this, success -> {
            if (success) {
                Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                loadUsers();
            } else {
                Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}