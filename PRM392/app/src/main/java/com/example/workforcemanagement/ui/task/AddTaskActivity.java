//package com.example.workforcemanagement.ui.task;
//
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.workforcemanagement.R;
//import com.example.workforcemanagement.data.model.Task;
//import com.example.workforcemanagement.data.model.User;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.android.material.textfield.TextInputLayout;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//public class AddTaskActivity extends AppCompatActivity {
//    private TaskViewModel taskViewModel;
//    private TextInputEditText etTitle, etDescription, etStartDate, etDeadline, etEstimatedHours;
//    private AutoCompleteTextView spPriority, spAssignee;
//    private List<User> users = new ArrayList<>();
//    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dialog_create_task);
//
//        // Initialize ViewModel
//        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
//
//        // Setup Toolbar
//        MaterialToolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(v -> finish());
//
//        // Initialize Views
//        etTitle = findViewById(R.id.etTitle);
//        etDescription = findViewById(R.id.etDescription);
//        etStartDate = findViewById(R.id.etStartDate);
//        etDeadline = findViewById(R.id.etDeadline);
//        etEstimatedHours = findViewById(R.id.etEstimatedHours);
//        spPriority = findViewById(R.id.spPriority);
//        spAssignee = findViewById(R.id.spAssignee);
//        MaterialButton btnCancel = findViewById(R.id.btnCancel);
//        MaterialButton btnCreate = findViewById(R.id.btnCreate);
//
//        // Setup Spinners
//        setupSpinners();
//
//        // Setup Date Pickers
//        setupDatePickers();
//
//        // Setup Buttons
//        btnCancel.setOnClickListener(v -> finish());
//        btnCreate.setOnClickListener(v -> createTask());
//
//        // Observe Users
//        taskViewModel.getUsers().observe(this, usersResponse -> {
//            if (usersResponse != null && usersResponse.isSuccess()) {
//                users = usersResponse.getData().getUsers();
//                List<String> userNames = new ArrayList<>();
//                userNames.add("Unassigned");
//                for (User user : users) {
//                    userNames.add(user.getFullName());
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, userNames);
//                spAssignee.setAdapter(adapter);
//            }
//        });
//
//        // Fetch Users
//        taskViewModel.fetchUsers();
//    }
//
//    private void setupSpinners() {
//        String[] priorities = {"low", "medium", "high", "urgent"};
//        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities);
//        spPriority.setAdapter(priorityAdapter);
//        spPriority.setText("medium", false);
//    }
//
//    private void setupDatePickers() {
//        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
//        etDeadline.setOnClickListener(v -> showDatePicker(etDeadline));
//    }
//
//    private void showDatePicker(TextInputEditText editText) {
//        Calendar calendar = Calendar.getInstance();
//        new android.app.DatePickerDialog(this, (view, year, month, day) -> {
//            calendar.set(year, month, day);
//            editText.setText(dateFormat.format(calendar.getTime()));
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
//    }
//
//    private void createTask() {
//        String title = etTitle.getText().toString().trim();
//        String description = etDescription.getText().toString().trim();
//        String startDate = etStartDate.getText().toString().trim();
//        String deadline = etDeadline.getText().toString().trim();
//        String priority = spPriority.getText().toString();
//        String estimatedHours = etEstimatedHours.getText().toString().trim();
//        String assigneeName = spAssignee.getText().toString();
//
//        // Validation
//        if (title.isEmpty()) {
//            ((TextInputLayout) etTitle.getParent()).setError("Title is required");
//            return;
//        }
//
//        Task task = new Task();
//        task.setTitle(title);
//        task.setDescription(description.isEmpty() ? null : description);
//        task.setStartDate(startDate.isEmpty() ? null : startDate);
//        task.setDeadline(deadline.isEmpty() ? null : deadline);
//        task.setPriority(priority);
//        task.setEstimatedHours(estimatedHours.isEmpty() ? null : Float.parseFloat(estimatedHours));
//        task.setCreatorId(1); // Replace with actual logged-in user ID
//        if (!assigneeName.equals("Unassigned")) {
//            for (User user : users) {
//                if (user.getUsername().equals(assigneeName)) {
//                    task.setAssigneeId(user.getId());
//                    break;
//                }
//            }
//        }
//
//        taskViewModel.createTask(task).observe(this, success -> {
//            if (success) {
//                Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                Toast.makeText(this, "Failed to create task", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}

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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private TextInputEditText etTitle, etDescription, etStartDate, etDeadline, etEstimatedHours;
    private AutoCompleteTextView spPriority, spAssignee;
    private MaterialButton btnCreate;
    private List<Employee> employees = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_task);

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
        spPriority = findViewById(R.id.spPriority);
        spAssignee = findViewById(R.id.spAssignee);
        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        btnCreate = findViewById(R.id.btnCreate);

        // Disable Create button initially
        btnCreate.setEnabled(false);

        // Setup Spinners
        setupSpinners();

        // Setup Date Pickers
        setupDatePickers();

        // Setup Buttons
        btnCancel.setOnClickListener(v -> finish());
        btnCreate.setOnClickListener(v -> createTask());

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
                btnCreate.setEnabled(true); // Enable Create button after employees are loaded
            } else {
                Toast.makeText(this, "Failed to load employees: " + (employeesResponse != null ? employeesResponse.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                btnCreate.setEnabled(false);
            }
        });

        // Fetch Employees
        taskViewModel.fetchEmployees();
    }

    private void setupSpinners() {
        String[] priorities = {"low", "medium", "high", "urgent"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities);
        spPriority.setAdapter(priorityAdapter);
        spPriority.setText("medium", false);
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

    private void createTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();
        String priority = spPriority.getText().toString();
        String estimatedHours = etEstimatedHours.getText().toString().trim();
        String assigneeName = spAssignee.getText() != null ? spAssignee.getText().toString() : "Unassigned";

        // Validation
        if (title.isEmpty()) {
            ((TextInputLayout) etTitle.getParent()).setError("Title is required");
            return;
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description.isEmpty() ? null : description);
        task.setStartDate(startDate.isEmpty() ? null : startDate);
        task.setDeadline(deadline.isEmpty() ? null : deadline);
        task.setPriority(priority);
        task.setStatus("pending");
        task.setProgress(0);
        task.setEstimatedHours(estimatedHours.isEmpty() ? null : Float.parseFloat(estimatedHours));
        //task.setId(null); // Explicitly set to null
        task.setCreatorId(null); // Explicitly set to null

        if (assigneeName != null && !assigneeName.equals("Unassigned")) {
            for (Employee employee : employees) {
                String fullName = employee.getFullName();
                if (fullName != null && fullName.equals(assigneeName)) {
                    task.setAssigneeId(employee.getId());
                    break;
                }
            }
        }

        taskViewModel.createTask(task).observe(this, response -> {
            if (response != null && response) {
                Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to create task", Toast.LENGTH_LONG).show();
            }
        });
    }
}