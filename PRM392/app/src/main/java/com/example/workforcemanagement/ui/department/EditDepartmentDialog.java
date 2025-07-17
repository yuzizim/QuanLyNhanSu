package com.example.workforcemanagement.ui.department;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.example.workforcemanagement.data.repository.DepartmentRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class EditDepartmentDialog extends Dialog {

    private TextInputEditText etName, etCode, etDescription;
    private Spinner spManager;
    private Button btnUpdate, btnCancel;
    private Department department;
    private DepartmentRepository departmentRepository;
    private List<Employee> eligibleManagers;
    private OnDepartmentUpdateListener listener;

    public interface OnDepartmentUpdateListener {
        void onDepartmentUpdated();
    }

    public EditDepartmentDialog(@NonNull Context context, Department department, DepartmentRepository departmentRepository, OnDepartmentUpdateListener listener) {
        super(context);
        this.department = department;
        this.departmentRepository = departmentRepository;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_department);

        // Initialize views
        etName = findViewById(R.id.etName);
        etCode = findViewById(R.id.etCode);
        etDescription = findViewById(R.id.etDescription);
        spManager = findViewById(R.id.spManager);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        // Set existing department data
        etName.setText(department.getName());
        etCode.setText(department.getCode());
        etDescription.setText(department.getDescription());

        // Load eligible managers
        loadEligibleManagers();

        // Set up button listeners
        btnCancel.setOnClickListener(v -> dismiss());

        btnUpdate.setOnClickListener(v -> updateDepartment());
    }

    private void loadEligibleManagers() {
        departmentRepository.getEligibleManagers().observeForever(response -> {
            if (response.isSuccess() && response.getData() != null) {
                eligibleManagers = response.getData();
                List<String> managerNames = new ArrayList<>();
                managerNames.add("No Manager");
                int selectedIndex = 0;

                for (int i = 0; i < eligibleManagers.size(); i++) {
                    Employee manager = eligibleManagers.get(i);
                    managerNames.add(manager.getFirstName() + " " + manager.getLastName());
                    if (department.getManagerId() != null && department.getManagerId().equals(manager.getId())) {
                        selectedIndex = i + 1;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        managerNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spManager.setAdapter(adapter);
                spManager.setSelection(selectedIndex);
            } else {
                Toast.makeText(getContext(), "Failed to load managers: " + response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDepartment() {
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

        // Create updated department object
        Department updatedDepartment = new Department();
        updatedDepartment.setName(name);
        updatedDepartment.setCode(code);
        updatedDepartment.setDescription(description.isEmpty() ? null : description);
        updatedDepartment.setManagerId(managerId);

        // Call API to update department
        departmentRepository.updateDepartment(department.getId(), updatedDepartment).observeForever(success -> {
            if (success) {
                Toast.makeText(getContext(), "Department updated successfully", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onDepartmentUpdated();
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to update department", Toast.LENGTH_SHORT).show();
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