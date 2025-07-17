package com.example.workforcemanagement.ui.department;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Department;
import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.EmployeeListResponse;
import com.example.workforcemanagement.data.repository.DepartmentRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateDepartmentDialog extends Dialog {

    private TextInputEditText etName, etCode, etDescription;
    private Spinner spManager;
    private Button btnCreate, btnCancel;
    private DepartmentRepository departmentRepository;
    private List<Employee> eligibleManagers;
    private OnDepartmentCreateListener listener;

    public interface OnDepartmentCreateListener {
        void onDepartmentCreated();
    }

    public CreateDepartmentDialog(@NonNull Context context, DepartmentRepository departmentRepository, OnDepartmentCreateListener listener) {
        super(context);
        this.departmentRepository = departmentRepository;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_department);

        // Initialize views
        etName = findViewById(R.id.etName);
        etCode = findViewById(R.id.etCode);
        etDescription = findViewById(R.id.etDescription);
        spManager = findViewById(R.id.spManager);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);

        // Load eligible managers
        loadEligibleManagers();

        // Set up button listeners
        btnCancel.setOnClickListener(v -> dismiss());

        btnCreate.setOnClickListener(v -> createDepartment());
    }

    private void loadEligibleManagers() {
        departmentRepository.getEligibleManagers().observeForever(response -> {
            if (response.isSuccess() && response.getData() != null) {
                eligibleManagers = response.getData();
                List<String> managerNames = new ArrayList<>();
                managerNames.add("No Manager"); // Option for no manager
                for (Employee manager : eligibleManagers) {
                    managerNames.add(manager.getFirstName() + " " + manager.getLastName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        managerNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spManager.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Failed to load managers: " + response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDepartment() {
        String name = etName.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        Integer managerId = null;

        // Validate input
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        if (code.isEmpty()) {
            etCode.setError("Code is required");
            return;
        }

        // Get selected manager
        int selectedPosition = spManager.getSelectedItemPosition();
        if (selectedPosition > 0) { // If a manager is selected (not "No Manager")
            managerId = eligibleManagers.get(selectedPosition - 1).getId();
        }

        // Create department object
        Department department = new Department();
        department.setName(name);
        department.setCode(code);
        department.setDescription(description.isEmpty() ? null : description);
        department.setManagerId(managerId);

        // Call API to create department
        departmentRepository.createDepartment(department).observeForever(success -> {
            if (success) {
                Toast.makeText(getContext(), "Department created successfully", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onDepartmentCreated();
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to create department", Toast.LENGTH_SHORT).show();
            }
        });
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