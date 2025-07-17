package com.example.workforcemanagement.ui.task;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.data.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateTaskDialog extends Dialog {
    private final TaskViewModel taskViewModel;
    private TextInputEditText etTitle, etDescription, etStartDate, etDeadline, etEstimatedHours;
    private AutoCompleteTextView spPriority, spAssignee;
    private List<User> users = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public CreateTaskDialog(@NonNull Context context, TaskViewModel taskViewModel) {
        super(context);
        this.taskViewModel = taskViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_task);

        // Initialize Views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etStartDate = findViewById(R.id.etStartDate);
        etDeadline = findViewById(R.id.etDeadline);
        etEstimatedHours = findViewById(R.id.etEstimatedHours);
        spPriority = findViewById(R.id.spPriority);
        spAssignee = findViewById(R.id.spAssignee);
        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        MaterialButton btnCreate = findViewById(R.id.btnCreate);

        // Setup Spinners
        setupSpinners();

        // Setup Date Pickers
        setupDatePickers();

        // Setup Buttons
        btnCancel.setOnClickListener(v -> dismiss());
        btnCreate.setOnClickListener(v -> createTask());

        // Observe Users
        taskViewModel.getUsers().observeForever(usersResponse -> {
            if (usersResponse != null && usersResponse.isSuccess()) {
                users = usersResponse.getData().getUsers();
                List<String> userNames = new ArrayList<>();
                userNames.add("Unassigned");
                for (User user : users) {
                    userNames.add(user.getFullName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, userNames);
                spAssignee.setAdapter(adapter);
            }
        });
    }

    private void setupSpinners() {
        String[] priorities = {"low", "medium", "high", "urgent"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, priorities);
        spPriority.setAdapter(priorityAdapter);
        spPriority.setText("medium", false);
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etDeadline.setOnClickListener(v -> showDatePicker(etDeadline));
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        new android.app.DatePickerDialog(getContext(), (view, year, month, day) -> {
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
        String assigneeName = spAssignee.getText().toString();

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
        task.setEstimatedHours(estimatedHours.isEmpty() ? null : Float.parseFloat(estimatedHours));
        task.setCreatorId(1); // Replace with actual logged-in user ID
        if (!assigneeName.equals("Unassigned")) {
            for (User user : users) {
                if (user.getFullName().equals(assigneeName)) {
                    task.setAssigneeId(user.getId());
                    break;
                }
            }
        }

        taskViewModel.createTask(task).observeForever(success -> {
            if (success) {
                Toast.makeText(getContext(), "Task created successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to create task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}