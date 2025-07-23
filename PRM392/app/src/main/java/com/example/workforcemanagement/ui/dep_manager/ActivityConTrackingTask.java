package com.example.workforcemanagement.ui.dep_manager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workforcemanagement.R;
import com.example.workforcemanagement.data.api.ApiClient;
import com.example.workforcemanagement.data.api.ApiService;
import com.example.workforcemanagement.data.model.Task;
import com.example.workforcemanagement.data.model.TasksResponse;
import com.example.workforcemanagement.ui.adapter.TaskAdapter;
import com.example.workforcemanagement.util.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityConTrackingTask extends AppCompatActivity {
    private static final int REQUEST_CREATE_TASK = 1001;
    private static final int REQUEST_EDIT_TASK = 1002;
    private RecyclerView rvTaskList;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private EditText etSearchTask;
    private Spinner spinnerPriority;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con_tracking_task);

        rvTaskList = findViewById(R.id.rvTaskList);
        etSearchTask = findViewById(R.id.etSearchTask);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        progressBar = findViewById(R.id.progressBar);

        rvTaskList.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList);
        rvTaskList.setAdapter(taskAdapter);

        taskAdapter.setTaskActionListener(new TaskAdapter.TaskActionListener() {
            @Override
            public void onView(Task task) {
                Intent intent = new Intent(ActivityConTrackingTask.this, DetailTrackingTask.class);
                intent.putExtra("task", task);
                startActivity(intent);
            }

            @Override
            public void onEdit(Task task) {
                Intent intent = new Intent(ActivityConTrackingTask.this, EditTrackingTask.class);
                intent.putExtra("task", task);
                startActivityForResult(intent, REQUEST_EDIT_TASK);
            }

            @Override
            public void onCancel(Task task) {
                deleteTask(task.getId());
            }
        });

        etSearchTask.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = s.toString();
                String priority = getPriorityForAPI();
                loadTasks(search, priority);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        spinnerPriority.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String search = etSearchTask.getText().toString();
                String priority = getPriorityForAPI();
                loadTasks(search, priority);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        ImageView btnAddTask = findViewById(R.id.btnAddTask);
        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateTrackingTask.class);
            startActivityForResult(intent, REQUEST_CREATE_TASK);
        });

        ImageView btnBack = findViewById(R.id.btnBackTracking);
        btnBack.setOnClickListener(v -> finish());

        loadTasks("", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CREATE_TASK || requestCode == REQUEST_EDIT_TASK) && resultCode == RESULT_OK) {
            loadTasks("", "");
        }
    }

    // Đã fix: check null cho spinnerPriority và getSelectedItem
    private String getPriorityForAPI() {
        if (spinnerPriority == null) return "";
        Object selected = spinnerPriority.getSelectedItem();
        if (selected == null) return "";
        String priority = selected.toString();
        if (priority.equalsIgnoreCase("Tất cả") || priority.equalsIgnoreCase("All")) {
            return "";
        }
        return priority.toLowerCase();
    }

    private void loadTasks(String search, String priority) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        String authHeader = "Bearer " + token;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getTasks(authHeader, search, null, priority, 1, 100)
                .enqueue(new Callback<TasksResponse>() {
                    @Override
                    public void onResponse(Call<TasksResponse> call, Response<TasksResponse> response) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (response.body() != null && response.body().isSuccess() && response.body().getData() != null) {
                            taskList.clear();
                            if (response.body().getData().getTasks() != null) {
                                taskList.addAll(response.body().getData().getTasks());
                                taskAdapter.setFullList(taskList);
                            }
                            taskAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ActivityConTrackingTask.this, "Không có dữ liệu!", Toast.LENGTH_SHORT).show();
                            taskList.clear();
                            taskAdapter.setFullList(taskList);
                            taskAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<TasksResponse> call, Throwable t) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        Toast.makeText(ActivityConTrackingTask.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteTask(int taskId) {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        String authHeader = "Bearer " + token;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.deleteTask(authHeader, taskId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ActivityConTrackingTask.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                            loadTasks("", "");
                        } else {
                            Toast.makeText(ActivityConTrackingTask.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ActivityConTrackingTask.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}