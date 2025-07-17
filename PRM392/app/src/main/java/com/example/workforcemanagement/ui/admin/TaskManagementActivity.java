package com.example.workforcemanagement.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.data.model.User;
import com.example.workforcemanagement.ui.task.AddTaskActivity;
import com.example.workforcemanagement.ui.task.CreateTaskDialog;
import com.example.workforcemanagement.ui.task.EditTaskActivity;
import com.example.workforcemanagement.ui.task.EditTaskDialog;
import com.example.workforcemanagement.ui.task.TaskAdapter;
import com.example.workforcemanagement.ui.task.TaskViewModel;
import com.example.workforcemanagement.util.TokenManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class TaskManagementActivity extends AppCompatActivity {
    private TaskViewModel taskViewModel;
    private TaskAdapter taskAdapter;
    private TextInputEditText etSearch;
    private AutoCompleteTextView spStatus, spPriority;
    private LinearLayout layoutEmptyState;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        // Initialize TokenManager
        tokenManager = new TokenManager(this);

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize Views
        etSearch = findViewById(R.id.etSearch);
        spStatus = findViewById(R.id.spStatus);
        spPriority = findViewById(R.id.spPriority);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        RecyclerView rvTasks = findViewById(R.id.rvTasks);
        ExtendedFloatingActionButton btnAddTask = findViewById(R.id.btnAddTask);

        // Setup RecyclerView
        //taskAdapter = new TaskAdapter(new ArrayList<>(), this::showEditTaskDialog, this::deleteTask);
        taskAdapter = new TaskAdapter(new ArrayList<>(), this::showEditTaskActivity, this::deleteTask);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        // Setup Filters
        setupFilters();

        // Setup Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                fetchTasks();
            }
        });

        // Setup FAB
        //btnAddTask.setOnClickListener(v -> showCreateTaskDialog());
        btnAddTask.setOnClickListener(v -> showAddTaskActivity());

        // Observe Tasks
        taskViewModel.getTasks().observe(this, tasksResponse -> {
            if (tasksResponse != null && tasksResponse.isSuccess()) {
                List<Task> tasks = tasksResponse.getData().getTasks();
                taskAdapter.setTasks(tasks);
                layoutEmptyState.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });

        // Fetch initial data
        taskViewModel.fetchUsers();
        fetchTasks();
    }

    private void setupFilters() {
        // Status filter
        String[] statuses = {"All", "pending", "in_progress", "review", "completed", "cancelled"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        spStatus.setAdapter(statusAdapter);
        spStatus.setOnItemClickListener((parent, view, position, id) -> fetchTasks());

        // Priority filter
        String[] priorities = {"All", "low", "medium", "high", "urgent"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities);
        spPriority.setAdapter(priorityAdapter);
        spPriority.setOnItemClickListener((parent, view, position, id) -> fetchTasks());
    }

    private void fetchTasks() {
        String search = etSearch.getText().toString().trim();
        String status = spStatus.getText().toString().equals("All") ? "" : spStatus.getText().toString();
        String priority = spPriority.getText().toString().equals("All") ? "" : spPriority.getText().toString();
        taskViewModel.fetchTasks(search, status, priority, 1, 10);
    }

    private void showCreateTaskDialog() {
        CreateTaskDialog dialog = new CreateTaskDialog(this, taskViewModel);
        dialog.show();
    }

    private void showEditTaskDialog(Task task) {
        EditTaskDialog dialog = new EditTaskDialog(this, task, taskViewModel);
        dialog.show();
    }

    private void showAddTaskActivity() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }

    private void showEditTaskActivity(Task task) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra("TASK", task);
        startActivity(intent);
    }

    private void deleteTask(Task task) {
        taskViewModel.deleteTask(task.getId()).observe(this, success -> {
            if (success) {
                fetchTasks();
            }
        });
    }
}