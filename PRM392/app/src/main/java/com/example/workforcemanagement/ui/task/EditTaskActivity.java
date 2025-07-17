package com.example.workforcemanagement.ui.task;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Employee;
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.data.model.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {
    private Task task;
    private TaskViewModel taskViewModel;
    private TextInputEditText etTitle, etDescription, etStartDate, etDeadline, etEstimatedHours, etProgress;
    private AutoCompleteTextView spPriority, spAssignee, spStatus;
    private List<Employee> employees = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_task);

        // Get Task from Intent
        task = (Task) getIntent().getSerializableExtra("TASK");

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize Views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etStartDate = findViewById(R.id.etStartDate);
        etDeadline = findViewById(R.id.etDeadline);
        etEstimatedHours = findViewById(R.id.etEstimatedHours);
        etProgress = findViewById(R.id.etProgress);
        spPriority = findViewById(R.id.spPriority);
        spAssignee = findViewById(R.id.spAssignee);
        spStatus = findViewById(R.id.spStatus);
        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        MaterialButton btnUpdate = findViewById(R.id.btnUpdate);

        // Populate Fields
        populateFields();

        // Setup Spinners
        setupSpinners();

        // Setup Date Pickers
        setupDatePickers();

        // Setup Buttons
        btnCancel.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateTask());

        // Observe Employees
        taskViewModel.getEmployees().observe(this, employeesResponse -> {
            if (employeesResponse != null && employeesResponse.isSuccess()) {
                employees = employeesResponse.getData().getEmployees();
                List<String> employeeNames = new ArrayList<>();
                employeeNames.add("Unassigned");
                for (Employee employee : employees) {
                    String fullName = employee.getFullName();
                    if (fullName != null && !fullName.trim().isEmpty()) {
                        employeeNames.add(fullName);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, employeeNames);
                spAssignee.setAdapter(adapter);
                spAssignee.setText("Unassigned", false); // Set default selection
                btnUpdate.setEnabled(true); // Enable Create button after employees are loaded
            } else {
                Toast.makeText(this, "Failed to load employees: " + (employeesResponse != null ? employeesResponse.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                btnUpdate.setEnabled(false);
            }
        });

        // Fetch Employees
        taskViewModel.fetchEmployees();
    }

    private void populateFields() {
        etTitle.setText(task.getTitle());
        etDescription.setText(task.getDescription());
        etStartDate.setText(task.getStartDate());
        etDeadline.setText(task.getDeadline());
        etEstimatedHours.setText(task.getEstimatedHours() != null ? String.valueOf(task.getEstimatedHours()) : "");
        etProgress.setText(String.valueOf(task.getProgress()));
        spPriority.setText(task.getPriority(), false);
        spStatus.setText(task.getStatus(), false);
    }

    private void setupSpinners() {
        String[] priorities = {"low", "medium", "high", "urgent"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities);
        spPriority.setAdapter(priorityAdapter);

        String[] statuses = {"pending", "in_progress", "review", "completed", "cancelled"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        spStatus.setAdapter(statusAdapter);
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etDeadline.setOnClickListener(v -> showDatePicker(etDeadline));
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            editText.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();
        String priority = spPriority.getText().toString();
        String status = spStatus.getText().toString();
        String estimatedHours = etEstimatedHours.getText().toString().trim();
        String progress = etProgress.getText().toString().trim();
        String assigneeName = spAssignee.getText().toString();

        // Validation
        if (title.isEmpty()) {
            ((TextInputLayout) etTitle.getParent()).setError("Title is required");
            return;
        }

        Task updatedTask = new Task();
        updatedTask.setId(task.getId());
        updatedTask.setTitle(title);
        updatedTask.setDescription(description.isEmpty() ? null : description);
        updatedTask.setStartDate(startDate.isEmpty() ? null : startDate);
        updatedTask.setDeadline(deadline.isEmpty() ? null : deadline);
        updatedTask.setPriority(priority);
        updatedTask.setStatus(status);
        updatedTask.setEstimatedHours(estimatedHours.isEmpty() ? null : Float.parseFloat(estimatedHours));
        updatedTask.setProgress(progress.isEmpty() ? 0 : Integer.parseInt(progress));
        updatedTask.setCreatorId(null); // Replace with actual logged-in user ID
        if (assigneeName != null && !assigneeName.equals("Unassigned")) {
            for (Employee employee : employees) {
                String fullName = employee.getFullName();
                if (fullName != null && fullName.equals(assigneeName)) {
                    task.setAssigneeId(employee.getId());
                    break;
                }
            }
        }

        taskViewModel.updateTask(updatedTask).observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}