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

public class EditTaskDialog extends Dialog {
    private final Task task;
    private final TaskViewModel taskViewModel;
    private TextInputEditText etTitle, etDescription, etStartDate, etDeadline, etEstimatedHours, etProgress;
    private AutoCompleteTextView spPriority, spAssignee, spStatus;
    private List<User> users = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public EditTaskDialog(@NonNull Context context, Task task, TaskViewModel taskViewModel) {
        super(context);
        this.task = task;
        this.taskViewModel = taskViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_task);

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
        btnCancel.setOnClickListener(v -> dismiss());
        btnUpdate.setOnClickListener(v -> updateTask());

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
                spAssignee.setText(task.getAssigneeName() != null ? task.getAssigneeName() : "Unassigned", false);
            }
        });
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
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, priorities);
        spPriority.setAdapter(priorityAdapter);

        String[] statuses = {"pending", "in_progress", "review", "completed", "cancelled"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, statuses);
        spStatus.setAdapter(statusAdapter);
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
        updatedTask.setCreatorId(1); // Replace with actual logged-in user ID
        if (!assigneeName.equals("Unassigned")) {
            for (User user : users) {
                if (user.getFullName().equals(assigneeName)) {
                    updatedTask.setAssigneeId(user.getId());
                    break;
                }
            }
        }

        taskViewModel.updateTask(updatedTask).observeForever(success -> {
            if (success) {
                Toast.makeText(getContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to update task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}