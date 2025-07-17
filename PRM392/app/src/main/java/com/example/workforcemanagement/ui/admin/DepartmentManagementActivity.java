package com.example.workforcemanagement.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Department;
import com.example.workforcemanagement.data.repository.DepartmentRepository;
import com.example.workforcemanagement.ui.department.CreateDepartmentDialog;
import com.example.workforcemanagement.ui.department.DepartmentAdapter;
import com.example.workforcemanagement.ui.department.DepartmentViewModel;
import com.example.workforcemanagement.ui.department.DepartmentViewModelFactory;
import com.example.workforcemanagement.ui.department.EditDepartmentDialog;
import com.example.workforcemanagement.util.TokenManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class DepartmentManagementActivity extends AppCompatActivity implements DepartmentAdapter.OnDepartmentActionListener, EditDepartmentDialog.OnDepartmentUpdateListener, CreateDepartmentDialog.OnDepartmentCreateListener {

    private DepartmentViewModel viewModel;
    private DepartmentAdapter adapter;
    private RecyclerView rvDepartments;
    private LinearLayout layoutEmptyState;
    private TextView tvTotalDepartments, tvManagedDepartments;
    private EditText etSearch;
    private MaterialToolbar toolbar;
    private ExtendedFloatingActionButton btnAddDepartment;
    private DepartmentRepository departmentRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_management);

        // Initialize TokenManager and Repository
        TokenManager tokenManager = new TokenManager(this);
        departmentRepository = new DepartmentRepository(tokenManager);

        // Initialize ViewModel with custom factory
        DepartmentViewModelFactory factory = new DepartmentViewModelFactory(departmentRepository);
        viewModel = new ViewModelProvider(this, factory).get(DepartmentViewModel.class);

        // Initialize views
        rvDepartments = findViewById(R.id.rvDepartments);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvTotalDepartments = findViewById(R.id.tvTotalDepartments);
        tvManagedDepartments = findViewById(R.id.tvManagedDepartments);
        etSearch = findViewById(R.id.etSearch);
        toolbar = findViewById(R.id.toolbar);
        btnAddDepartment = findViewById(R.id.btnAddDepartment);

        // Set up toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Set up RecyclerView
        rvDepartments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DepartmentAdapter(this);
        rvDepartments.setAdapter(adapter);

        // Set up search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setSearchQuery(s.toString());
            }
        });

        // Set up FAB
        btnAddDepartment.setOnClickListener(v -> {
            CreateDepartmentDialog dialog = new CreateDepartmentDialog(this, departmentRepository, this);
            dialog.show();
        });

        // Observe departments data
        viewModel.getDepartments().observe(this, departmentResponse -> {
            if (departmentResponse.isSuccess() && departmentResponse.getData() != null) {
                adapter.setDepartments(departmentResponse.getData().getDepartments());
                tvTotalDepartments.setText(String.valueOf(departmentResponse.getData().getStats().getTotalDepartments()));
                tvManagedDepartments.setText(String.valueOf(departmentResponse.getData().getStats().getDepartmentsWithManagers()));
                layoutEmptyState.setVisibility(departmentResponse.getData().getDepartments().isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                Toast.makeText(this, departmentResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditDepartment(Department department) {
        EditDepartmentDialog dialog = new EditDepartmentDialog(this, department, departmentRepository, this);
        dialog.show();
    }

    @Override
    public void onDeleteDepartment(Department department) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete the department " + department.getName() + "?")
                .setPositiveButton("OK", (dialog, which) -> viewModel.deleteDepartment(department.getId()).observe(this, success -> {
                    if (success) {
                        Toast.makeText(this, "Department deleted", Toast.LENGTH_SHORT).show();
                        viewModel.loadDepartments();
                    } else {
                        Toast.makeText(this, "Failed to delete department", Toast.LENGTH_SHORT).show();
                    }
                }))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

//    @Override
//    public void onDeleteDepartment(int departmentId) {
//        viewModel.deleteDepartment(departmentId).observe(this, success -> {
//            if (success) {
//                Toast.makeText(this, "Department deleted successfully", Toast.LENGTH_SHORT).show();
//                viewModel.loadDepartments(); // Refresh the list
//            } else {
//                Toast.makeText(this, "Failed to delete department", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    public void onDepartmentUpdated() {
        viewModel.loadDepartments(); // Refresh the list after update
    }

    @Override
    public void onDepartmentCreated() {
        viewModel.loadDepartments(); // Refresh the list after creation
    }
}